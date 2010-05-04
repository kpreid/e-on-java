package net.captp.jcomm;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import net.captp.tables.AnswersTable;
import net.captp.tables.ExportsTable;
import net.captp.tables.ImportsTable;
import net.captp.tables.NearGiftTable;
import net.captp.tables.PromiseGiftTable;
import net.captp.tables.QuestionsTable;
import net.captp.tables.SwissTable;
import net.captp.tables.Vine;
import net.vattp.data.Msg;
import net.vattp.data.MsgHandler;
import net.vattp.data.VatTPConnection;
import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.exception.EBacktraceException;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.DelayedRedirector;
import org.erights.e.elib.ref.EProxyResolver;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.ref.ReferenceMonitor;
import org.erights.e.elib.ref.Resolver;
import org.erights.e.elib.sealing.Sealer;
import org.erights.e.elib.sealing.Unsealer;
import org.erights.e.elib.serial.Replacer;
import org.erights.e.elib.serial.Reviver;
import org.erights.e.elib.serial.SerializationStream;
import org.erights.e.elib.serial.UnserializationStream;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.meta.java.math.BigIntegerSugar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OptionalDataException;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Object which manages a CapTP protocol connection to a remote vat and which
 * will receive incoming CapTP protocol messages.<p>
 * <p/>
 * The processMessage(byte[] message, VatTPConnection) method will be called
 * when a message of type E_MSG is received from the remote vat.<p>
 * <p/>
 * CapTPConnection manages four tables for 2-vat interactions: <UL>
 * <LI>Questions: Handlers for remote objects we requested </LI> <LI>Imports:
 * Handlers for remote objects exported to us unsolicited </LI> <LI>Answers:
 * Answers for remote Questions </LI> <LI>Exports: local objects matching
 * remote Imports </LI> </UL>
 * <p/>
 * The four tables can be arranged into a matrix: <pre>
 * <p/>
 *                     |   we generate pos   | remote generates pos
 *                     |                     |
 * --------------------+---------------------+----------------------
 * outgoing remote ref |                     |
 *     we have handler |       Questions     |       Imports
 *  remote has object  |          -          |          +
 * --------------------+---------------------+----------------------
 * incoming remote ref |                     |
 *     we have object  |       Exports       |       Answers
 *  remote has handler |          +          |          -
 * --------------------+---------------------+----------------------
 * </pre>
 * <p/>
 * When referencing an object or handler by pos number alone (independent of
 * what table it should be looked up in), we distinguish the table by the sign
 * of pos: Questions and Answers entries are referenced using a negative pos,
 * while Imports and Exports entries use positive pos. The sign of pos is
 * determined by the following rule: when pos is allocated by the outgoing
 * side, it negative; when pos is allocated by the incoming side, pos is
 * positive. The usefulness of this scheme is that whenever we know one axis of
 * this table (which we always know from context), we can be determine the
 * other axis by looking at the sign of pos. <p>
 *
 * @author Chip Morningstar
 * @author Bill Frantz
 * @author Mark S. Miller
 */
class CapTPConnection implements MsgHandler {

    /**
     * If three-vat introductions don't actually work, turn off this flag and
     * recompile.
     * <p/>
     * You'll be broken, but in a safe way.
     */
    static private final boolean DOES_3VAT = true;

    /**
     * Queue delivery of message, no answer expected. <p>
     * <p/>
     * Start counting at 8 since the previous version of the protocol (0.8.9)
     * stopped at 7.
     */
    static private final byte DELIVER_ONLY_OP = 8;

    /**
     * Queue delivery of message, resolve answer to the outcome.
     * <p/>
     * DELIVER_OP as 9 had a different argument order
     */
    static private final byte DELIVER_OP = 13;

    /**
     * An import on the other has gone away dropping some number of wireCounts.
     * Clean up the export table.
     */
    static private final byte GC_EXPORT_OP = 10;

    /**
     * A question on the other has gone away. Clean up the answer table.
     */
    static private final byte GC_ANSWER_OP = 11;

    /**
     * Please shut down the connection if you have no messages pending.
     */
    static private final byte SHUTDOWN_OP = 12;


    /**
     * The CapTPMgr for our vat
     */
    private final CapTPMgr myCapTPMgr;

    /**
     * Means of communication with our partner at the other end of the line.
     */
    private final VatTPConnection myDataConnection;

    /**
     * Number of things (XXX what's a "thing"?) which are currently holding the
     * connection open
     */
    private int myUseCount;

    /**
     * Number of messages sent on this connection
     */
    private int mySendCount;

    /**
     * Number of messages received on this connection
     */
    private int myReceiveCount;

    /**
     * Flag indicating we are in the midst of shutting down the connection
     */
    private boolean myShuttingDownFlag;

    /**
     * If the connection died, anybody who talks to us will get this. <p>
     * <p/>
     * This should be null exactly when the connection is still alive.
     */
    private Throwable myOptProblem;

    /**
     * Resolver of the promise used to buffer lookup requests made in midst of
     * connection shutdown.
     */
    private Resolver myOptBufferedLookups;

    /**
     * Generates new swiss numbers
     */
    private final SecureRandom myEntropy;

    /**
     * The Questions table: these are handlers this end created and is
     * expecting the other end to hook up to the relevent objects themselves
     * (e.g., results of sendAll()s).
     */
    private QuestionsTable myQuestions;

    /**
     * The Answers table: this is the counterpart to the Questions table at the
     * other end of the connection. <p>
     * <p/>
     * Messages sent through a handler in the questions table will be delivered
     * to the corresponding answer.
     */
    private AnswersTable myAnswers;

    /**
     * The Imports table: these are handles to objects the other end exported.
     * <p>
     * <p/>
     * We just hook up the handler in the specified location.
     */
    private ImportsTable myImports;

    /**
     * The Exports table: this is the counterpart to the Imports table at the
     * other end of the connection. <p>
     * <p/>
     * These are objects that have been exported from this end (i.e., mentioned
     * by us in the parameters of a message). The other end installs them in
     * the same spot in its Imports table.
     */
    private ExportsTable myExports;

    /**
     * At incoming position 0, for bringing about 3-vat introductions using
     * nonces.
     */
    private final NonceLocator myLocalNonceLocator;

    /**
     * A remote reference to the other side's myLocalNonceLocator, at outgoing
     * position 0, for bringing about 3-vat introductions using nonces.
     */
    private final Object myRemoteNonceLocator;

    /**
     * For bringing about 3-vat introductions of unresolved references using
     * nonces.
     */
    private PromiseGiftTable myPGifts;

    /**
     * For bringing about 3-vat introductions of Near references using nonces.
     */
    private NearGiftTable myNGifts;

    /**
     * When having a problem with a particular position or set of positions,
     * you can change checkPos to check for these positions and set
     * <pre>    -DTraceLog_captp=event</pre>
     * In order to generate output involving only these positions.
     */
    private boolean checkPos(int pos) {
        return false;
    }

    /**
     *
     */
    boolean debug(int pos) {
        if (Trace.captp.debug && Trace.ON) {
            return true;
        } else if (Trace.captp.event && Trace.ON) {
            return checkPos(pos);
        } else {
            return false;
        }
    }

    /**
     *
     */
    void debugm(int pos, String msg) {
        if (checkPos(pos)) {
            Trace.captp.eventm(toString() + "." + msg);
        } else {
            Trace.captp.debugm(toString() + "." + msg);
        }
    }

    /**
     * Makes a CapTPConnection for a particular CapTP instance (capTPMgr) and a
     * particular VatTP connection (dataConn).
     *
     * @param capTPMgr The CapTPMgr managing this connection.
     * @param dataConn The VatTP-level data connection to be associated with
     *                 this CapTPConnection.
     * @param entropy  Where new swiss numbers come from.
     */
    CapTPConnection(CapTPMgr capTPMgr,
                    VatTPConnection dataConn,
                    SecureRandom entropy) throws IOException {
        myCapTPMgr = capTPMgr;
        myDataConnection = dataConn;
        myOptProblem = null;
        myUseCount = 0;
        myReceiveCount = 0;
        mySendCount = 0;
        myShuttingDownFlag = false;
        myOptBufferedLookups = null;

        /* We want to listen for E_MSG traffic */
        myDataConnection.registerMsgHandler(Msg.E_MSG, this);
        myEntropy = entropy;

        /* Create a new set of empty tables */
        myQuestions = new QuestionsTable();
        myAnswers = new AnswersTable();
        myImports = new ImportsTable();
        myExports = new ExportsTable();

        //XXX for now
        LookupHandler lookupHandler = new LookupHandler(this);
        myRemoteNonceLocator = lookupHandler.myResolver.getProxy();

        myPGifts = new PromiseGiftTable(myRemoteNonceLocator);
        myNGifts = new NearGiftTable();
        myLocalNonceLocator = new NonceLocator(myPGifts,
                                               myNGifts,
                                               dataConn.getRemoteVatID(),
                                               capTPMgr,
                                               capTPMgr.getSwissTable());

        if (Trace.captp.debug && Trace.ON) {
            Trace.captp
              .debugm("Create CapTPConnection " + this + " mgr=" + capTPMgr +
                " dataConn=" + dataConn + " vat=%" +
                dataConn.getRemoteVatID() + "/%" + dataConn.getLocalVatID());
        }
    }

    /**
     *
     */
    public String toString() {
        return "Conn(" + localVatID() + " <-> " + remoteVatID() + ")";
    }

    /**
     *
     */
    PromiseGiftTable getPromiseGiftTable() {
        return myPGifts;
    }

    /**
     *
     */
    NearGiftTable getNearGiftTable() {
        return myNGifts;
    }

    /**
     *
     */
    Object getRemoteNonceLocator() {
        return myRemoteNonceLocator;
    }

    /**
     *
     */
    Sealer getSealer() {
        return myCapTPMgr.mySealer;
    }

    /**
     *
     */
    Unsealer getUnsealer() {
        return myCapTPMgr.myUnsealer;
    }

    /**
     * If non-null, this is a dead connection
     */
    Throwable getOptProblem() {
        return myOptProblem;
    }

    /**
     * Return our identity.
     */
    String localVatID() {
        return myDataConnection.getLocalVatID();
    }

    /**
     * Return the identity of the party to whom we are speaking.
     */
    String remoteVatID() {
        return myDataConnection.getRemoteVatID();
    }

    /**
     *
     */
    ConstList remoteSearchPath() {
        return myDataConnection.getRemoteSearchPath();
    }

    /**
     *
     */
    EProxyResolver makeQuestion() {
        int pos = -myQuestions.bind(null);
        RemotePromiseHandler handler = new RemotePromiseHandler(this, pos);
        EProxyResolver result = handler.myResolver;
        myQuestions.put(-pos, result);
        return result;
    }

    /**
     * Drop this index in our own Questions table, and in the other vat's
     * Answers table.
     */
    void dropQuestion(int pos) {
        myQuestions.free(-pos);
        sendGCAnswerOp(pos);
    }

    /**
     *
     */
    public ReferenceMonitor getReferenceMonitor() {
        return myCapTPMgr.getReferenceMonitor();
    }

    /************************ Desc Creation *********************/

    /**
     * The wireCount is initialized to one
     */
    private NewFarDesc newFarDesc(Object obj) {
        SwissTable swiss = myCapTPMgr.getSwissTable();
        BigInteger id = swiss.getIdentity(obj);
        BigInteger hash = BigIntegerSugar.cryptoHash(id);
        return new NewFarDesc(myExports.newFarPos(obj), hash);
    }

    /**
     * The wireCount is initialized to one
     */
    private NewRemotePromiseDesc newRemotePromiseDesc(Object promise) {
        int importPos = myExports.bind(promise);

        SwissTable swiss = myCapTPMgr.getSwissTable();
        BigInteger rdrBase = swiss.nextSwiss();
        BigInteger rdrNum = BigIntegerSugar.cryptoHash(rdrBase);
        BigInteger rdrHash = BigIntegerSugar.cryptoHash(rdrNum);

        int rdrPos = -myQuestions.bind(null);
        FarHandler handler = new FarHandler(this, rdrPos, rdrHash);
        EProxyResolver rdrResolver = handler.myResolver;
        myQuestions.put(-rdrPos, rdrResolver);
        Object farRdr = rdrResolver.getProxy();
        E.sendOnly(promise, "__whenMoreResolved", farRdr);

        return new NewRemotePromiseDesc(importPos, rdrPos, rdrBase);
    }

    /**
     * Returns a NewFarDesc, NewRemotePromiseDesc, or an ImportDesc for
     * exporting obj, which is assumed to be suitable for being in our exports
     * table.
     * <p/>
     * obj is assumed to be a Near reference to a PassByProxy object (actual or
     * HONORARY), or eventual.
     */
    ObjectRefDesc makeImportingDesc(Object obj) {
        T.test(Ref.isNear(obj), "Must be near");
        T.test(Ref.isPassByProxy(obj), "Must be PassByProxy");

        int index = myExports.indexFor(obj);
        if (-1 == index) {
            // the wireCount is initialized to 1
            return newFarDesc(obj);
        }
        // increments the wireCount
        myExports.incr(index);
        return new ImportDesc(index);
    }

    private Object new3Desc(RemoteHandler handler, Ref ref) {
        if (!DOES_3VAT) {
            Throwable problem =
              new RuntimeException("XXX 3vats not yet implemented");
            return Ref.broken(problem);
        }
        CapTPConnection conn = handler.myConn;
        long nonce = myEntropy.nextLong();
        Object toHost = conn.getRemoteNonceLocator();
        Object remoteVine =
          E.send(toHost, "provideFor", ref, remoteVatID(), new Long(nonce));
        Promise3Desc result = new Promise3Desc(conn.remoteSearchPath(),
                                               conn.remoteVatID(),
                                               nonce,
                                               new Vine(remoteVine));
        return result;
    }

    /**
     * Figure out what kind of eventual reference 'ref' is, and return an
     * appropriate descriptor for encoding it over the wire.
     */
    Object makeEventualDesc(Ref ref) {
        Unsealer unsealer = getUnsealer();
        RemoteHandler optHandler =
          (RemoteHandler)EProxyResolver.getOptProxyHandler(unsealer, ref);
        if (null == optHandler) {
            //a local promise
            return newRemotePromiseDesc(ref);
        }
        if (this == optHandler.myConn) {
            int pos = optHandler.getPos();
            return new IncomingDesc(pos);
        }
        return new3Desc(optHandler, ref);
    }

//    /**
//     *
//     */
//    LocatorUnumDesc makeLocatorUnumDesc() {
//        return LocatorUnumDesc.THE_ONE;
//    }

    /************************ Desc Messages *********************/

    /**
     * Dereferencing of a NewFarDesc. <p>
     * <p/>
     * On entry, importPos may be free, or may be allocated to an entry with a
     * zero wireCount. (XXX we don't currently check the wirecount.) In the
     * latter case, the entry is overwritten.
     *
     * @param importPos The import position at which a new FarRef should be
     *                  created.
     * @param swissHash The sameness identity of that FarRef.
     * @return The newly created FarRef.
     */
    Ref newFarRef(int importPos, BigInteger swissHash) {
        FarHandler handler = new FarHandler(this, importPos, swissHash);
        myImports.store(importPos, handler.myResolver);
        return handler.myResolver.getProxy();
    }

    /**
     * Dereferencing of a NewRemotePromiseDesc. <p>
     * <p/>
     * On entry, importPos may be free, or may be allocated to an entry with a
     * zero wireCount. In the latter case, the entry is overwritten.
     *
     * @param importPos The import position at which the new RemotePromise
     *                  should be created.
     * @param rdrPos    The answers position at which the DelayedRedirector of
     *                  that new RemotePromise is made available.
     * @param rdrBase   The sameness identity of that DelayedRedirector must be
     *                  the cryptohash of rdrBase.
     * @return The newly created RemotePromise.
     */
    Ref newRemotePromise(int importPos, int rdrPos, BigInteger rdrBase) {
        RemotePromiseHandler handler =
          new RemotePromiseHandler(this, importPos);
        EProxyResolver resolver = handler.myResolver;
        myImports.store(importPos, resolver);
        DelayedRedirector rdr = new DelayedRedirector(resolver);
        myAnswers.put(-rdrPos, rdr, true);
        myCapTPMgr.getSwissTable().registerNewSwiss(rdr, rdrBase);
        return resolver.getProxy();
    }

    /**
     * Dereferencing of an ImportDesc. <p>
     * <p/>
     * On entry, importPos must be allocated, but may be allocated to an entry
     * with a zero wireCount. <p>
     * <p/>
     * Return an imported Proxy, or its resolution, and, if it still has a
     * handler (ie, it isn't resolved) increment its wire count.
     *
     * @param importPos The position of the import in the Imports table
     * @return Whatever the resolution is of the Proxy in the appropriate table
     *         at importPos
     */
    Ref getImport(int importPos) {
        EProxyResolver pr = myImports.getProxyResolver(importPos);
        RemoteHandler optHandler = (RemoteHandler)pr.optHandler();
        if (null != optHandler) {
            optHandler.countWireRef();
        }
        return pr.getProxy();
    }

    /**
     * Dereferencing of an IncomingDesc
     * <p/>
     * Return an Exports or Answers table entry.
     *
     * @param incomingPos A positive pos refer to the Exports table, a negative
     *                    one to the Answers table.
     * @return Whatever object was in the appropriate table at incomingPos.
     */
    Object getIncoming(int incomingPos) {
        if (0 < incomingPos) {
            return myExports.get(incomingPos);
        } else if (0 > incomingPos) {
            return myAnswers.get(-incomingPos);
        } else {
            return myLocalNonceLocator;
        }
    }

    /**
     * Dereferencing of a Promise3Desc
     *
     * @param searchPath hints to find the vat identified by vatID
     * @param vatID      The fingerprint of the public key of the vat hosting
     *                   the object to be looked up.
     * @param nonce      Identifies the object in that vat's appropriate gift
     *                   table.
     * @param optFarVine Hold on to this until the object has been retrieved.
     * @return A promise for the looked up object.
     */
    Ref getLookup(ConstList searchPath,
                  String vatID,
                  long nonce,
                  Object optFarVine) {
        CapTPConnection hostConn = null;
        try {
            hostConn = myCapTPMgr.getOrMakeProxyConnection(searchPath, vatID);
        } catch (IOException e) {
            throw ExceptionMgr.asSafe(e);
        }
        Object toHost = hostConn.getRemoteNonceLocator();
        return E.send(toHost,
                      "acceptFrom",
                      remoteSearchPath(),
                      remoteVatID(),
                      new Long(nonce),
                      new Vine(optFarVine));
    }

    /**
     * Dereferencing of a Far3Desc
     *
     * @param searchPath hints to find the vat identified by vatID
     * @param vatID      The fingerprint of the public key of the vat hosting
     *                   the object to be looked up.
     * @param nonce      Identifies the object in that vat's appropriate gift
     *                   table.
     * @param swissHash  Identity of object being looked up. getLookup returns
     *                   a resolved reference with that identity. If it can't
     *                   return a FarRef with that identity, then it returns a
     *                   DisconnectedRef with that identity.
     * @param optFarVine Hold on to this until the object has been retrieved.
     * @return A promise for the looked up object.
     */
    Ref getLookup(ConstList searchPath,
                  String vatID,
                  long nonce,
                  BigInteger swissHash,
                  Object optFarVine) {
        T.fail("XXX getLookup/5 not yet implemented");
        return null; //make compiler happy
    }

    /**
     *
     */
    LocatorUnum getLocatorUnum() {
        return myCapTPMgr.getLocatorUnum();
    }

    /***************************** receiving ************************/


    /**
     * Process an incoming message from the VatTPConnection.
     *
     * @param message  The incoming message.
     * @param dataConn The VatTPConnection on which the message arrived
     * @see net.vattp.data.MsgHandler
     */
    public void processMessage(byte[] message, VatTPConnection dataConn) {
        try {
            T.require(dataConn == myDataConnection,
                      "message's VatTPConnection doesn't match");

            if (myOptProblem != null) {
                /* Just to be safe */
                return;
            }

            if (myShuttingDownFlag) {
                myShuttingDownFlag = false;
                if (null != myOptBufferedLookups) {
                    submitLookups(myOptBufferedLookups);
                    myOptBufferedLookups = null;
                }
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(message);

            /* The message type (the first byte) should *always* be E_MSG */
            if (Msg.E_MSG != (byte)bis.read()) {
                T.fail("CapTPConnection was handed a non-E_MSG message");
            }

            //Read the command byte *before* starting to interpret the stream
            ++myReceiveCount;
            byte cmd = (byte)bis.read();

            if (Trace.captp.debug && Trace.ON) {
                Trace.captp
                  .debugm(
                    "CapTPConnection " + this + " receive msg cmd=" + cmd);
            }

            Reviver reviver = new CapTPReviver(this);
            UnserializationStream uns = reviver.getUnserializationStream(bis);
            receiveMsg(cmd, uns);

        } catch (Throwable problem) {
            /* They sent a badly formed message, trace it & ignore it */
            if (Trace.captp.warning && Trace.ON) {
                Trace.captp.warningm("ignoring", problem);
            }
        }
    }

    private void receiveMsg(byte cmd, UnserializationStream uns)
      throws IOException, ClassNotFoundException, OptionalDataException {
        switch (cmd) {
        case DELIVER_ONLY_OP: {
            int recipPos = uns.readInt();
            String verb = uns.readUTF().intern();
            try {
                Object[] args = (Object[])uns.readObject();

                execDeliverOnlyOp(recipPos, verb, args);
            } catch (Throwable problem) {
                whyNoDeliverOnlyOp(recipPos, verb, problem);
            }
            break;
        }
        case DELIVER_OP: {
            int answerPos = uns.readInt();
            Object rdr = uns.readObject();
            int recipPos = uns.readInt();
            String verb = uns.readUTF().intern();
            try {
                Object[] args = (Object[])uns.readObject();

                execDeliverOp(answerPos, rdr, recipPos, verb, args);
            } catch (Throwable problem) {
                whyNoDeliverOp(answerPos, rdr, recipPos, verb, problem);
            }
            break;
        }
        case GC_EXPORT_OP: {
            int exportPos = uns.readInt();
            int wireCount = uns.readInt();

            execGCExportOp(exportPos, wireCount);
            break;
        }
        case GC_ANSWER_OP: {
            int answerPos = uns.readInt();

            execGCAnswerOp(answerPos);
            break;
        }
        case SHUTDOWN_OP: {
            int receivedCount = uns.readInt();

            execShutdownOp(receivedCount);
            break;
        }
        default: {
            T.fail("Invalid command byte " + cmd);
        }
        }
    }

    /**
     * Pretty print the args of a message send, for debugging purposes.
     */
    static private String argsString(Object[] args) {
        if (0 == args.length) {
            return "[]";
        }
        String result = "[\n   " + args[0];
        for (int i = 1; i < args.length; ++i) {
            result += ",\n   " + args[i];
        }
        return result + "]";
    }

    /**
     *
     */
    private void execDeliverOnlyOp(int recipPos, String verb, Object[] args) {
        if (Trace.causality.debug && Trace.ON) {
            CommEvent commEvent = new CommEvent(remoteVatID(),
                                                localVatID(),
                                                myReceiveCount,
                                                "SC_execDeliverOnlyOp");
            Trace.causality.debugm("", commEvent);
        }
        if (debug(recipPos)) {
            debugm(recipPos,
                   "exec DeliverOnlyOp(" + recipPos + ", " + verb + ", " +
                     argsString(args) + ")");
        }

        Object recip = getIncoming(recipPos);
        E.sendAllOnly(recip, verb, args);
        //The return result of sendAllOnly is correctly ignored, since, if
        //the message got this far, there was no failure to immediately queue
        //the message. A problem now is the kind of problem that a sendOnly
        //is supposed to ignore -- a problem at the receiving end.
    }

    /**
     *
     */
    private void whyNoDeliverOnlyOp(int recipPos,
                                    String verb,
                                    Throwable problem) {
        String msg = "whyNoDeliverOnlyOp(" + recipPos + ", " + verb + ", " +
          problem + ")";
        //Note: trace level is warning
        if (Trace.captp.warning && Trace.ON) {
            Trace.captp.warningm(msg, problem);
        }
        if (0 != recipPos || !"traceRemote".equals(verb)) {
            //Just locally report a failure to remotely report,
            //in order to avoid a potential bounce cycle. All others
            //are remotely reported as well.
            E.sendOnly(myRemoteNonceLocator, "traceRemote", msg);
        }
    }

    /**
     *
     */
    private void execDeliverOp(int answerPos, Object rdr,

                               int recipPos, String verb, Object[] args) {
        if (Trace.causality.debug && Trace.ON) {
            CommEvent commEvent = new CommEvent(remoteVatID(),
                                                localVatID(),
                                                myReceiveCount,
                                                "SC_execDeliverOp");
            Trace.causality.debugm("", commEvent);
        }
        if (debug(recipPos)) {
            debugm(recipPos,
                   "exec DeliverOp(" + answerPos + ", " + rdr + ",\n  " +
                     recipPos + ", " + verb + ", " + argsString(args) + ")");
        }

        Object recip = getIncoming(recipPos);
        Ref answer = E.sendAll(recip, verb, args);
        myAnswers.put(-answerPos, answer, true);
        E.sendOnly(answer, "__whenMoreResolved", rdr);
    }

    /**
     *
     */
    private void whyNoDeliverOp(int answerPos, Object rdr,

                                int recipPos, String verb, Throwable problem) {
        Ref broke = Ref.broken(problem);
        //Note: trace level is warning
        if (Trace.captp.warning && Trace.ON) {
            Trace.captp
              .warningm("whyNoDeliverOp(" + answerPos + ", " + rdr + ",\n  " +
                recipPos + ", " + verb + ", ???)", problem);
        }
        myAnswers.put(-answerPos, broke, true);
        E.sendOnly(broke, "__whenMoreResolved", rdr);
    }

    /**
     *
     */
    private void execGCExportOp(int exportPos, int wireCount) {
        if (Trace.causality.debug && Trace.ON) {
            CommEvent commEvent = new CommEvent(remoteVatID(),
                                                localVatID(),
                                                myReceiveCount,
                                                "SC_execGCExportOp");
            Trace.causality.debugm("", commEvent);
        }
        if (debug(exportPos)) {
            debugm(exportPos,
                   "exec GCExportOp(" + exportPos + ", " + wireCount + ")");
        }
        myExports.decr(exportPos, wireCount);
    }

    /**
     *
     */
    private void execGCAnswerOp(int answerPos) {
        if (Trace.causality.debug && Trace.ON) {
            CommEvent commEvent = new CommEvent(remoteVatID(),
                                                localVatID(),
                                                myReceiveCount,
                                                "SC_execGCAnswerOp");
            Trace.causality.debugm("", commEvent);
        }
        if (debug(answerPos)) {
            debugm(answerPos, "exec GCAnswerOp(" + answerPos + ")");
        }

        myAnswers.free(-answerPos);
    }

    /**
     * Receive a shutdown message from the other end. If we don't have any
     * messages in flight, we shutdown the connection.
     */
    private void execShutdownOp(int receivedCount) {
        if (Trace.causality.debug && Trace.ON) {
            CommEvent commEvent = new CommEvent(remoteVatID(),
                                                localVatID(),
                                                myReceiveCount,
                                                "SC_execShutdownOp");
            Trace.causality.debugm("", commEvent);
        }
        if (Trace.captp.debug && Trace.ON) {
            Trace.captp.debugm("exec ShutdownOp(" + receivedCount + ")");
        }

        if (mySendCount <= receivedCount) {
            killConnection(new IOException("received shutdown request"), true);
        }
    }

    /************************** sending ************************/

    /**
     *
     */
    private SerializationStream makeMsg(ByteArrayOutputStream bos, byte cmd)
      throws IOException {
        bos.write(Msg.E_MSG);
        bos.write(cmd);
        Replacer replacer = new CapTPReplacer(this);
        return replacer.getSerializationStream(bos);
    }

    /**
     * Send a message via our VatTPConnection.
     */
    private void sendMsg(ByteArrayOutputStream bos, SerializationStream ser)
      throws IOException {
        ser.flush();
        byte[] msg = bos.toByteArray();
        if (Trace.captp.debug && Trace.ON) {
            Trace.captp
              .debugm("CapTPConnection " + this + " sendMsg cmd=" + msg[1]);
        }
        ++mySendCount;
        myDataConnection.sendMsg(msg);
    }

    /**
     *
     */
    void sendDeliverOnlyOp(int recipPos, String verb, Object[] args) {
        if (Trace.causality.debug && Trace.ON) {
            CommEvent commEvent = new CommEvent(localVatID(),
                                                remoteVatID(),
                                                mySendCount + 1,
                                                "SC_sendDeliverOnlyOp");
            Trace.causality.debugm("", commEvent);
        }
        if (debug(recipPos)) {
            debugm(recipPos,
                   "send DeliverOnlyOp(" + recipPos + ", " + verb + ", " +
                     argsString(args) + ")");
        }

        if (null != myOptProblem) {
            throw ExceptionMgr.asSafe(myOptProblem);
        }
        ByteArrayOutputStream bos;
        SerializationStream ser;
        try {
            bos = new ByteArrayOutputStream();
            ser = makeMsg(bos, DELIVER_ONLY_OP);
            ser.writeInt(recipPos);
            ser.writeUTF(verb);
        } catch (Throwable problem) {
            killConnection(problem, false);
            throw ExceptionMgr.asSafe(problem);
        }
        try {
            ser.writeObject(args);
        } catch (Throwable problem) {
            //don't kill the connection for a serialization problem
            throw ExceptionMgr.asSafe(problem);
        }
        try {
            sendMsg(bos, ser);
        } catch (Throwable problem) {
            killConnection(problem, false);
            throw ExceptionMgr.asSafe(problem);
        }
    }

    /**
     *
     */
    void sendDeliverOp(int answerPos, Object rdr,

                       int recipPos, String verb, Object[] args) {
        if (Trace.causality.debug && Trace.ON) {
            CommEvent commEvent = new CommEvent(localVatID(),
                                                remoteVatID(),
                                                mySendCount + 1,
                                                "SC_sendDeliverOp");
            Trace.causality.debugm("", commEvent);
        }
        if (debug(recipPos)) {
            debugm(recipPos,
                   "send DeliverOp(" + answerPos + ", " + rdr + ",\n  " +
                     recipPos + ", " + verb + ", " + argsString(args) + ")");
        }

        if (null != myOptProblem) {
            throw ExceptionMgr.asSafe(myOptProblem);
        }
        ByteArrayOutputStream bos;
        SerializationStream ser;
        try {
            bos = new ByteArrayOutputStream();
            ser = makeMsg(bos, DELIVER_OP);
            ser.writeInt(answerPos);
            ser.writeObject(rdr);
            ser.writeInt(recipPos);
            ser.writeUTF(verb);
        } catch (Throwable problem) {
            killConnection(problem, false);
            throw ExceptionMgr.asSafe(problem);
        }
        try {
            ser.writeObject(args);
        } catch (Throwable problem) {
            //don't kill the connection for a serialization problem
            throw ExceptionMgr.asSafe(problem);
        }
        try {
            sendMsg(bos, ser);
        } catch (Throwable problem) {
            killConnection(problem, false);
            throw ExceptionMgr.asSafe(problem);
        }
    }

    /**
     *
     */
    void sendGCExportOp(int exportPos, int wireCount) {
        if (Trace.causality.debug && Trace.ON) {
            CommEvent commEvent = new CommEvent(localVatID(),
                                                remoteVatID(),
                                                mySendCount + 1,
                                                "SC_sendGCExportOp");
            Trace.causality.debugm("", commEvent);
        }
        if (debug(exportPos)) {
            debugm(exportPos,
                   "send GCExportOp(" + exportPos + ", " + wireCount + ")");
        }

        if (null != myOptProblem) {
            throw ExceptionMgr.asSafe(myOptProblem);
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            SerializationStream ser = makeMsg(bos, GC_EXPORT_OP);
            ser.writeInt(exportPos);
            ser.writeInt(wireCount);
            sendMsg(bos, ser);
        } catch (Throwable problem) {
            killConnection(problem, false);
            throw ExceptionMgr.asSafe(problem);
        }
    }

    /**
     *
     */
    private void sendGCAnswerOp(int answerPos) {
        if (Trace.causality.debug && Trace.ON) {
            CommEvent commEvent = new CommEvent(localVatID(),
                                                remoteVatID(),
                                                mySendCount + 1,
                                                "SC_sendGCAnswerOp");
            Trace.causality.debugm("", commEvent);
        }
        if (debug(answerPos)) {
            debugm(answerPos, "send GCAnswerOp(" + answerPos + ")");
        }

        if (null != myOptProblem) {
            throw ExceptionMgr.asSafe(myOptProblem);
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            SerializationStream ser = makeMsg(bos, GC_ANSWER_OP);
            ser.writeInt(answerPos);
            sendMsg(bos, ser);
        } catch (Throwable problem) {
            killConnection(problem, false);
            throw ExceptionMgr.asSafe(problem);
        }
    }

    /**
     *
     */
    private void sendShutdownOp(int receivedCount) {
        if (Trace.causality.debug && Trace.ON) {
            CommEvent commEvent = new CommEvent(localVatID(),
                                                remoteVatID(),
                                                mySendCount + 1,
                                                "SC_sendShutdownOp");
            Trace.causality.debugm("", commEvent);
        }
        if (Trace.captp.debug && Trace.ON) {
            Trace.captp.debugm("send ShutdownOp(" + receivedCount + ")");
        }

        if (null != myOptProblem) {
            throw ExceptionMgr.asSafe(myOptProblem);
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            SerializationStream ser = makeMsg(bos, SHUTDOWN_OP);
            ser.writeInt(receivedCount);
            sendMsg(bos, ser);
        } catch (Throwable problem) {
            killConnection(problem, false);
            throw ExceptionMgr.asSafe(problem);
        }
    }

    /**************** lookup, gc, & shutdown *****************/

    /**
     *
     */
    void submitLookups(Resolver bufferedLookups) {
        T.fail("XXX submitLookups not yet implemented");
    }

    /**
     * Decrement the use count. If it reaches 0, we have no references active
     * over this connection and the connection can (and should) be shut down.
     */
    private void decrementUseCount() {
        --myUseCount;
        if (0 >= myUseCount) {
            myShuttingDownFlag = true;
            sendShutdownOp(myReceiveCount);
        }
    }

    /**
     * Receive notification that our VatTPConnection has died.
     *
     * @param dataConn The VatTPConnection object which has just died.
     * @param problem  The cause-of-death to report.
     * @see net.vattp.data.MsgHandler
     */
    public void connectionDead(VatTPConnection dataConn, Throwable problem) {
        T.require(dataConn == myDataConnection,
                  "dead VatTPConnection doesn't match");
        killConnection(new EBacktraceException(problem, "# lost " + dataConn),
                       myShuttingDownFlag);
    }

    /**
     * Terminate this connection in the case where something unrecoverable has
     * gone wrong. <p>
     * <p/>
     * "A coward dies a thousand deaths, a hero dies but once." <p>
     * <p/>
     * Our error handling is cowardly -- if anything goes wrong during I/O we
     * assume we are dead. Thus we can be caused to die more than once, but we
     * need to actually die only once...
     *
     * @param problem What went wrong
     */
    private void killConnection(Throwable problem, boolean deliberate) {
        T.notNull(problem, "dying with a null problem");
        String prefix = deliberate ? "shutdown " : "lost ";
        if (Trace.captp.debug && Trace.ON) {
            Trace.captp.debugm(prefix + this, problem);
        }
        if (null == myOptProblem) { // If this is the first time we've died...
            myOptProblem = problem;
            myDataConnection.shutDownConnection(problem);

            myQuestions.smash(problem);
            myAnswers.smash(problem);
            myImports.smash(problem);
            myExports.smash(problem);
            myPGifts.smash(problem);
            myNGifts.smash(problem);

            myQuestions = null;
            myAnswers = null;
            myImports = null;
            myExports = null;
            myPGifts = null;
            myNGifts = null;

            if (deliberate) {
                //transfer buffered lookups to a newly spawned connection
                myCapTPMgr.connectionDead(myDataConnection,
                                          myOptBufferedLookups);
            } else {
                //XXX smash buffered lookups
            }
            myOptBufferedLookups = null;
        }
    }
}
