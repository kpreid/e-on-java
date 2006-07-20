#!/usr/bin/env rune

# Copyright 2005 Mark S. Miller under the terms of the MIT X license found at
# http://www.opensource.org/licenses/mit-license.html .........................

# Assumes uml_mconsole (from the user-mode-linux utilities) is on the path.

pragma.enable("easy-return")
pragma.enable("explicit-result-guard")
pragma.enable("lambda-args")

def Nat := (int >= 0)
def byte := <import:java.lang.makeByte>.getTYPE()
def Word implements Guard {
    to coerce(specimen, optEjector) :any {
        def str := String.coerce(specimen, optEjector)
        for forbidden in " \t=/" {
            if (str.contains(forbidden)) {
                throw.eject(optEjector, `A Word can't contain $forbidden`)
            }
        }
        return str
    }
    to __printOn(out :TextWriter) :void { out.print("Word") }
}

def env := makeCommand("/usr/bin/env")

def makeMConsole(umid :Word) :any {

    def userConsole
    def mConsole {
        to send(command :String) :String {
            def cmdResult := env.doNow(["uml_mconsole", umid],
                                       null,
                                       null,
                                       `$command$\n`)
            def [==0, `($umid) @result$\n($umid) $\n`, ``] := cmdResult
            return result
        }
        to setConfig(devName :Word, config :String) :String {
            return mConsole.send(`config $devName=$config`)
        }
        to getUserConsole() :any { return userConsole }
    }
    bind userConsole {
        to getVersion() :String {
            return mConsole.send("version")
        }
        to haltNoSync() :String {
            return mConsole.send("halt")
        }
        to rebootNoSync() :String {
            return mConsole.send("reboot")
        }
        to getConfig(devName :Word) :String {
            return mConsole.send(`config $devName`)
        }
        to remove(devName :Word) :String {
            return mConsole.send(`remove $devName`)
        }
        to sysrq(key :char) :String {
            return mConsole.send(`sysrq $key`)
        }
        to sync() :String {
            return mConsole.sysrq('s')
        }
        to cad() :String {
            return mConsole.send("cad")
        }
        to stop() :String {
            return mConsole.send("stop")
        }
        to go() :String {
            return mConsole.send("go")
        }
        to log(logMsg :String) :String {
            return mConsole.send(`log $logMsg`)
        }
        to getProc(filename :String) :String {
            return mConsole.send(`proc $filename`)
        }
    }
    return mConsole
}

var umidCount := 0

# Maps from umid => sliverMgr
def sliverMgrTable := [].asMap().diverge()

def jRuntime := <unsafe:java.lang.makeRuntime>.getRuntime()

# XXX Should find by searching PATH
def SSH_PATH := "/usr/bin/ssh"

def makeSliverMgr(umid :Word) :any {
    def sliverDir := <file>[umid]
    sliverDir.mkdir(null)
    var optProc := null
    def mConsole := makeMConsole(umid)
    def userConsole := mConsole.getUserConsole()

    # XXX Should be potentially different per guest
    def guestIP := "192.168.1.2"

    def sliverController {
        to launch() :any {
            if (optProc != null) {
                # Will throw if it hasn't exited
                optProc.exitValue()
            }
            optProc := jRuntime.exec(["./run-sliver", umid],
                                     null,
                                     null)
            return optProc
        }
        to getUmid() :String { return umid }
        to getOptProc() :any { return optProc }
        to getUserConsole() :any { return userConsole }
        to shutdown() :String {
            return (userConsole.stop() +
                      "\n--------\n"+
                      userConsole.sync()+
                      "\n--------\n"+
                      userConsole.haltNoSync())
        }
        to destroy() :String {
            def result := sliverController.shutdown()
            sliverMgrTable.remove(umid)
            return result
        }
        to command(shellCommand :String) :any {
            return jRuntime.exec([SSH_PATH, `root@@$guestIP`, shellCommand],
                                 null,
                                 null)
        }
        to writeGuestFile(src :rcvr, dest :String) :any {
            def catProc := sliverController.command(`cat > $dest`)
            return when (src <- getBytes()) -> done(bytes :List[byte]) :void {
                def outs := catProc.getOutputStream()
                outs.write(bytes)
                outs.close()
                return catProc
            } catch ex {
                throw(ex)
            }
        }
    }
    def sliverMgr {
        to getController() :any { return sliverController }
        to getMConsole() :any { return mConsole }
    }
    sliverMgrTable[umid] := sliverMgr
    return sliverMgr
}

def acctMgr
var sliverPrice := 10

def sliverServer {
    to getSliverPrice() :Nat { return sliverPrice }
    to getMoneyBrand() :any { return acctMgr <- getBrand() }
    to buyNewSliver(payment) :vow[SturdyRef] {
        def doneVow := acctMgr <- deposit(payment, "sliverSale", sliverPrice)
        return when(doneVow) -> done(_) :SturdyRef {
            def umid := `prod$umidCount`
            umidCount += 1
            def sliverMgr := makeSliverMgr(umid)
            def result := sliverMgr.getController()
            return makeSturdyRef.temp(result)
        } catch ex {
            throw(ex)
        }
    }
}

def sliverServerMgr {
    to bindAccountMgr(accountMgr) :void { bind acctMgr := accountMgr }
    to setSliverPrice(newPrice :Nat) :void { sliverPrice := newPrice }
    to getSliverServer() :any { return sliverServer }
    to getUmids() :List { return sliverMgrTable.getKeys() }
    to getSliverMgr(umid :Word) :any { return sliverMgrTable[umid] }
    to shutdownServer() :void {
        for sliverMgr in sliverMgrTable {
            sliverMgr.getController().shutdown()
        }
        interp.exitAtTop()
    }
}

introducer.onTheAir()

def ssmURI := introducer.sturdyToURI(makeSturdyRef.temp(sliverServerMgr))
<file:sliverServerMgr.cap>.setText(ssmURI)
println("sliverServerMgr.cap:\n")
println(ssmURI)

println("\nready")
interp.blockAtTop()
