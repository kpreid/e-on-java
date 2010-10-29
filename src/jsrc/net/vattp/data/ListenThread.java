package net.vattp.data;

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

import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.vat.Vat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A separate thread in which to actually listen for incoming connections,
 * since that is most certainly an operation which blocks.
 *
 * @author Bill Frantz Based on work by Chip Morningstar, 25-March-1997 which
 *         is based on earlier work by Eric Messick and Gordie Freedman
 *         modified by MarkM to resolve the listenAddress during construction
 *         rather than in the new thread, so it can be synchronously added to
 *         the ConnectionMgr's searchPath.
 */
class ListenThread extends Thread {

    private boolean myTerminateFlag = false;

    private InetAddress myOptIP = null;

    private final ServerSocket myListenServerSocket;

    /**
     * The VatTPMgr to report new Sockets to
     */
    private VatTPMgr myConnMgr;

    /**
     * A vat for synchronization when calling the VatTPMgr
     */
    private final Vat myVat;

    /**
     * An object to synchronize communication with the user thread
     */
    private final Object myUserThreadLock = new Object();

    private final boolean mySuspended = false;

    /**
     * Construct a new object to run in its own thread and listen on a
     * ServerSocket for new incoming connections.
     *
     * @param optLocalAddr The local address we will listen on.
     * @param connMgr      the VatTPMgr to be notified about interesting events
     *                     (new connections and errors) that happen while
     *                     listening.
     */
    ListenThread(String optLocalAddr, VatTPMgr connMgr, Vat vat)
      throws UnknownHostException, IOException {
        setDaemon(true);
        myConnMgr = connMgr;
        myVat = vat;
        //Create the listen thread
        NetAddr netAddr = new NetAddr(optLocalAddr);
        myOptIP = netAddr.optInetAddress();
        int localPort = netAddr.getPort();
        myListenServerSocket = new ServerSocket(localPort, 50, myOptIP);
        start();

        if (Trace.comm.verbose && Trace.ON) {
            Trace.comm
              .verbosem("ListenThread started because...",
                        new Error("where?"));
        }
    }

    /**
     * Call a method in the VatTPMgr
     *
     * @param thunk a Thunk that will perform the call. The thunk will be
     *              called after the Vat lock is obtained.
     */
    private void callMgr(DataCommThunk thunk) {
        try {
            myVat.now(thunk);
        } catch (Throwable t) {
            Trace.comm.errorm("Error while calling " + thunk, t);
            if (t instanceof VirtualMachineError) {
                throw (VirtualMachineError)t;
            }
            if (t instanceof ThreadDeath) {
                throw (ThreadDeath)t;
            }
            if (t instanceof LinkageError) {
                throw (LinkageError)t;
            }
        }
    }

    private void noticeProblem(Throwable t) {
        callMgr(new DataCommThunk(myConnMgr, t));
    }

    /**
     * The address on which this thread is listening, or null if there was a
     * problem
     */
    NetAddr listenAddress() {
        return new NetAddr(myOptIP, myListenServerSocket.getLocalPort());
    }

    /**
     * The actual body of the listener thread. <p>
     * <p/>
     * Opens a ServerSocket and accepts connections on it. Each connection
     * spawns a new ByteConnection object that is handed off to the
     * innerListener to be dealt with. <p>
     * <p/>
     * Loops until told to stop by somebody calling 'shutdown()' (which sets an
     * internal flag).
     */
    public void run() {
        try {
            String name = listenAddress().toString();
            setName("ListenThread-" + name);
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm.debugm("listening at " + name);
            }
            while (!myTerminateFlag) {
                Socket clientSocket;
                try {
                    clientSocket = myListenServerSocket.accept();
                } catch (IOException e) {
                    Trace.comm.errorm("exception in ListenThread accept()", e);
                    //XXX do we really want to "continue" ???
                    continue;
                }
                if (myTerminateFlag) {
                    clientSocket.close();
                    break;
                }
                if (mySuspended) {
                    //XXX what's mySuspended supposed to be about?
                    clientSocket.close();
                } else {
                    setupNewConnection(clientSocket);
                }
            }
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm.debugm("I've been asked to shutdown");
            }
            myListenServerSocket.close();
        } catch (IOException e) {
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm.debugm("caught exception", e);
            }
            noticeProblem(e);
        } finally {
            myConnMgr = null;
        }

        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("terminated");
        }
    }

    /**
     * Create a new connection to handle the new socket.
     * <p/>
     * Tell the VatTPMgr about the new Socket.
     */
    private void setupNewConnection(Socket clientSocket) {
        callMgr(new DataCommThunk(myConnMgr, clientSocket));

    }

    /**
     * Shutdown the thread. <p>
     * <p/>
     * The listener thread itself will die the next time it returns from
     * 'accept' and notices that the terminate flag is set. What is more
     * likely, however, is that the process will quiesce and we will go gently
     * into the night, taking the daemon listener thread away with us when we
     * go. We kill the listener user thread here so that that can happen.
     */
    void shutdown() {
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("shutdown");
        }
        myTerminateFlag = true;
        synchronized (myUserThreadLock) {
            myUserThreadLock.notify();
        }
    }

    /**
     * Start the listener thread.<p>
     * <p/>
     * HACK: The listener thread needs to be a daemon thread, because if it
     * were a user thread it would be uninteruptible while it was off waiting
     * on an accept() (this is due to a flaw in Solaris, actually). However, if
     * it's a daemon thread the app can exit even if the thread is still
     * running. However, if we're just sitting there waiting for connections to
     * arrive over the network, we don't want to exit, we want to keep running.
     * Thus we have the net.vattp.data.UserThread, which does NOTHING but wait.
     * Since it's a user thread it keeps the app from exiting (and thus allows
     * the listener thread to keep running waiting for a connection) and since
     * it's not waiting on an accept we can kill it. When we tell the listener
     * thread to shutdown (which we now can do since it's a daemon thread), it
     * sends a notify() to the listener user thread whereupon *it* shuts down
     * too. Hallelujah, amen.
     */
    void startup() {
        super.start();
    }
}
