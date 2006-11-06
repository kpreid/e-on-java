#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def traceline(str) :void { stderr.println(`$\nevalServerPool $str`) }

def makeVatHolder :=
  <elang:cmd.vatHolderMakerAuthor>(<unsafe>, introducer, traceline)

# Sturdy refs of all eval servers that have joined this pool
var members := [].diverge()

# Sturdy refs of members that have become inaccessible
var offline := [].diverge()

# List of rcvrs for accessible eval servers
var active := [].diverge()

# List of rcvrs for available (not busy) subset of active list
var available := [].diverge()

# Lookup table for finding the eval server that provided a particular maker
# service mapping from a maker SturdyRef to eval server rcvr
var busy := [].asMap().diverge()

# Queue of functions waiting to satisfy a request for evaluation services
var waiting := [].diverge()

def objToURI(obj) :String {
    introducer.sturdyToURI(makeSturdyRef(obj))
}

def checkWaiting() :void {
    if (waiting.size() >= 1) {
        def isSatisfied := waiting[0]
        if (isSatisfied()) {
            waiting.removeRun(0, 1)
        }
    }
}

/**
 * The <tt>connectorFacet</tt> provides access to pool membership.
 * <p>
 * To join, an eval server must have the URI for this pool's
 * <tt>connectorFacet</tt>. Once it is a member, if its reference
 * ever becomes broken, it is moved to a list of inaccessible
 * members and will not be polled for a change of status.
 * <p>
 * Note: Soon, the <tt>serviceFacet</tt> will support actions
 * on individual eval servers.
 */
def connectorFacet {

    /**
     * <tt>connect</tt> adds an eval server as a member of this pool.
     *
     * @param evalServer SturdyRef for the eval server.
     */
    to connect(evalServer :SturdyRef) :void {
        traceline("eval server joins")
        members.push(evalServer)
        def es := evalServer.getRcvr()
        active.push(es)
        available.push(es)
        checkWaiting()

        Ref.whenBroken(es, def cleanup(_) :void {
            # remove from active
            var i := active.indexOf1(es)
            if (i != -1) {
                active.removeRun(i, i + 1)
            }
            # remove from available
            i := available.indexOf1(es)
            if (i != -1) {
                available.removeRun(i, i + 1)
            }
            # remove from busy
            for key => value in busy {
                if (value == es) {
                    busy.removeKey(key)
                }
            }
            # add to offline
            offline.push(evalServer)
        })
    }
}

/**
 * The <tt>serviceFacet</tt> provides access to the internal state of the pool
 * and various maintenance operations.
 * <p>
 * An <tt>evalServerPool</tt> manages a pool of eval servers to provide
 * evaluation services to clients. The simplest, smallest pool is a
 * single, local server. Pools containing multiple servers -- some local,
 * some remote -- are supported as well.
 * <p>
 * Three different facets are available for three different types of
 * client access.
 * <ul>
 *    <li>To join the pool, eval servers use the <tt>connectorFacet</tt>.
 *    <li>To request and release evaluation services, clients use the
 *        <tt>clientFacet</tt>.
 *    <li>To add a service panel user interface, use the <tt>serviceFacet</tt>.
 * </ul>
 *
 * @author Terry Stanley
 * @author Mark S. Miller
 */
def serviceFacet {

    to describePoolState() :String {
        var result := ` members: ${members.size()}
offline: ${offline.size()}
active: ${active.size()}
available: ${available.size()}
waiting: ${waiting.size()}
busy: `
        for sr => _ in busy {
            def uri := introducer.sturdyToURI(sr)
            result += `$uri$\n       `
        }
        result
    }

    /**
     * <tt>shutdownNow</tt> tells each active eval server to shutdown now,
     * then it shuts itself down.
     * <p>
     * It ignores the state of the pool and the status of the eval servers.
     * <p>
     * Note: Soon, smarter shutdown methods that wait for busy eval servers
     * to be released will be added. This simple approach is good for now.
     */
    to shutdownNow() :void {
        for es in active {
            es <- shutdownNow()
        }
        interp.continueAtTop()
    }
}

/**
 * The <tt>clientFacet</tt> provides access to evaluation services.
 * <p>
 * Clients request access to an eval server and receive a maker object
 * for its service. The maker object makes an evaluator which runs the
 * client's job. The client is responsible for releasing the service
 * when the job is complete.
 */
def clientFacet {

    /**
     * <tt>requestEvalServerSet</tt> tries to satisfy a request for access to
     * multiple eval servers.
     * <p>
     * It returns a vow for the set and if the request is successful, the vow
     * resolves to a list of SturdyRefs for maker objects for the eval
     * servers' services. This is an all-or-nothing request. If a problem
     * occurs the vow becomes broken with the problem and the client is
     * responsible for rescheduling the request.
     *
     * @param n Number of eval servers requested.
     * @return A vow for the set of eval servers requested. If the request is
     *         successful the vow resolves to a list of maker object SturdyRefs
     *         for the eval servers' services. If the request cannot be
     *         satisfied completely the vow becomes broken with the problem.
     */
    to requestEvalServerSet(vatNames :List[String]) :vow {
        def n := vatNames.size()
        if (active.size() < n) {
            # make the pool at least n in size
            for i in 0 ..! (n - active.size()) {
                # the local eval server will join this pool through the
                # connectorFacet
                def vatHolder := makeVatHolder(["--putFrontFacet",
                                                objToURI(connectorFacet)],
                                               [].asMap(),
                                               "local vat for server pool")
            }
        }
        def [result, resolver] := Ref.promise()
        def isSatisfied() :boolean {
            if (available.size() < n) {
                # Stay (or be added) to the waiting queue and try again later.
                false
            } else {
                # SturdyRefs for evaluator makers
                var optMakers := [].diverge()
                for i => vatName in vatNames {
                    def runnerKind := switch (vatName) {
                        match `@_.e-awt` { "awt" }
                        match `@_.e-swt` { "swt" }
                        match _          { "headless" }
                    }
                    if (runnerKind != "headless") {
                        throw(`evalServerPool doesn't support $runnerKind yet`)
                    }

                    # Grab all n servers from available list during this turn.
                    # Notice that between this turn and the done
                    # functions' turns, these servers are neither
                    # available nor busy.
                    var es := available[0]
                    available.removeRun(0, 1)

                    # This when-catch queues a done function for each maker and
                    # each function will be executed in its own turn.
                    when (es <- getAccessToService()) ->
                      done(maker :SturdyRef) :void {

                        busy[maker] := es
                        if (optMakers == null) {
                            # caught earlier problem, so back out of request
                            clientFacet.releaseEvalServer(maker)
                        } else {
                            optMakers.push(maker)
                            if (optMakers.size() == n) {
                                # all promises fulfilled
                                resolver.resolve(optMakers.snapshot())
                            }
                        }
                    } catch problem {
                        # Must back out of all-or-nothing request.
                        # Notice that if the client reschedules this request,
                        # it goes to the back of the queue, giving
                        # others a chance.

                        if (optMakers != null) {
                            for maker in optMakers {
                                # back out by releasing previously
                                # acquired resources
                                clientFacet.releaseEvalServer(maker)
                            }
                            resolver.smash(problem)
                            # tell any pending queued done functions
                            # to back out
                            optMakers := null
                        }
                    }
                }
                # Return true if there are enough available servers
                true
            }
        }
        if (waiting.size() >= 1 || ! (isSatisfied())) {
            # If others are waiting, just get in line.
            # If waiting queue is empty, try to satisfy request.
            waiting.push(isSatisfied)
        }
        result
    }

    /**
     * <tt>requestEvalServer</tt> tries to satisfy a request for access to
     * a single eval server.
     * <p>
     * It returns a vow which, if the request is successful, resolves to a
     * single maker object rcvr for the eval server's services. If a problem
     * occurs the vow is broken with the problem.
     *
     * @return A vow for access to a single eval server. If the request is
     *         successful the vow resolves to a SturdyRef for a maker for the
     *         eval server's services. If the request cannot be satisfied
     *         the vow becomes broken with the problem.
     */
    to requestEvalServer(vatName :String) :vow[SturdyRef] {
        clientFacet.requestEvalServerSet([vatName]) <- get(0)
    }

    /**
     * <tt>releaseEvalServer</tt> releases the service provided by
     * the maker object rcvr.
     * <p>
     * The eval server that granted access to the service is now available
     * for requests.
     *
     * @param maker The maker object rcvr returned by the
     *              request for access to an eval server.
     */
    to releaseEvalServer(maker :SturdyRef) :void {
        def es := busy[maker]
        busy.removeKey(maker)
        es <- releaseService(maker)
        available.push(es)
        checkWaiting()
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
    "putConnectorFacet" =>
      term`option("putConnectorFacet", "capTarget", zeroOrOne,
                  "Where should the connectorFacet be exported to?")`,
    "putServiceFacet"   =>
      term`option("putServiceFacet", "capTarget", zeroOrOne,
                  "Where should the serviceFacet be exported to?")`,
    "putClientFacet"    =>
      term`option("putClientFacet", "capTarget", zeroOrOne,
                  "Where should the clientFacet be exported to?")`
]

def [optionsMap, args] := interimArgParser(optionsDesc, interp.getArgs())

traceline("args parsed")

def exportFacet(optionName, face) :void {
    for arg in optionsMap.fetch(optionName, thunk{[]}) {
        when (exportCap(makeSturdyRef(face), arg)) -> done(_) :void {
            traceline("arg connected")
        } catch problem {
            traceline(`couldn't connect to $arg: $problem`)
            interp.exitAtTop(problem)
        }
    }
}

exportFacet("putConnectorFacet", connectorFacet)
exportFacet("putServiceFacet", serviceFacet)
exportFacet("putClientFacet", clientFacet)


interp.blockAtTop()
