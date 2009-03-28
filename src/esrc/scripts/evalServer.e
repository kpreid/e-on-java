#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def traceline(str) :void { stderr.println(`$\nevalServer $str`) }
#def traceline(str) {}
traceline("started")

def makeScopeSetup := <unsafe:org.erights.e.elang.interp.makeScopeSetup>
def makeVat := <unsafe:org.erights.e.elib.vat.makeVat>
def runeAuthor := <elang:cmd.runeAuthor>

def makeIntroducer := <unsafe:net.captp.jcomm.makeIntroducer>

def <updoc> := <import:org.erights.e.tools.updoc.*>

# If null, this eval server is not busy.
var optEvaluatorMaker :nullOk[rcvr[SturdyRef]] := null
var optVat := null

/**
 * An <tt>evalServer</tt> provides access to its evaluation service to
 * one client at a time.
 * <p>
 * The client uses the service to run a job then
 * releases the service back to the eval server.
 * <p>
 * The <tt>evalServer</tt> must be a member of an <tt>evalServerPool</tt> and
 * it expects the URI of the pool's <tt>connectorFacet</tt> to be passed in as
 * an argument. It will join the pool through this facet.
 *
 * @author Terry Stanley
 * @author Mark S. Miller
 */
def evalServer {

    /**
     * <tt>getAccessToService</tt> returns a maker object for the service
     * provided, making this eval server busy.
     * <p>
     * An exception is thrown if it is already busy.
     *
     * @return A SturdyRef for an <tt>evaluatorMaker</tt> that can make an
     *         evaluator for the client.
     */
    to getAccessToService() :rcvr[SturdyRef] {
        require(optEvaluatorMaker == null,
                fn{"eval server must not be busy"})
        optVat := makeVat.make("headless", "Service vat")
        optEvaluatorMaker := optVat.seed(fn{
            def [introducer2, identityMgr2] :=
              makeIntroducer.makePair(interp.getProps(),
                                      entropy,
                                      timer)
            introducer2.onTheAir()
            def systemGC {
                to gc() :void {
                    <unsafe:java.lang.makeSystem>.gc()
                }
            }
            def auths := runeAuthor.defaultAuths(<unsafe>)
            def maker := <updoc:makeEvaluatorAuthor>(systemGC,
                                                     auths,
                                                     [].asMap(),
                                                     optVat)
            def [result, _, _] := identityMgr2.makeKnown(maker)
            result
        })
    }

    /**
     * <tt>releaseService</tt> makes this eval server available for another
     * client request for access to its service.
     * <p>
     * An exception is thrown if the service was not provided by this eval
     * server.
     *
     * @param evaluatorMaker The SturdyRef for a maker object returned by
     *                       <tt>getAccessToService</tt>.
     */
    to releaseService(evaluatorMaker :rcvr[SturdyRef]) :void {
        require(optEvaluatorMaker == evaluatorMaker,
                fn{"Service must have been provided by this eval server."})
        # service is no longer in use
        optEvaluatorMaker := null
        optVat.orderlyShutdown("Service released.")
        optVat := null
    }

    /**
     * <tt>shutdownNow</tt> does an immediate shutdown.
     * <p>
     * It ignores the eval server's busy status.
     */
    to shutdownNow() :void {
        interp.continueAtTop()
    }
}

introducer.onTheAir()

def makeCapExporter := <import:org.erights.e.tools.args.makeCapExporter>

def uriGetters := [
    "file" => <file>,
    "http" => <http>,
    "ftp"  => <ftp>]

def exportCap := makeCapExporter(introducer,
                                 <file>,
                                 uriGetters,
                                 stdout)

def interimArgParser := <import:org.erights.e.tools.args.interimArgParser>

def optionsDesc := [
    "putFrontFacet" => term`option("putFrontFacet", "capTarget", one,
                                "Where should the evalServer be exported to?")`
]

def [optionsMap, args] := interimArgParser(optionsDesc, interp.getArgs())

def joinArg := optionsMap["putFrontFacet"][0]

traceline("args parsed")

# XXX review: does it make sense to add persistence support?
def ack := exportCap(makeSturdyRef.temp(evalServer), joinArg)

when (ack) -> done(_) :void {
    traceline("args connected")
} catch problem {
    traceline(`couldn't connect to $joinArg: $problem`)
    interp.exitAtTop(problem)
}

interp.blockAtTop()
