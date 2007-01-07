#!/usr/bin/env rune

# XXX Need copyright notice

pragma.syntax("0.8")

/**
 * A <a href="http://ciphersaber.gurus.com/">CipherSaber</a>
 * implementation in <a href="http://www.erights.org/">E</a>.
 * <p>
 * Note that CipherSaber is <a href=
 * "http://www.crypto.com/papers/others/rc4_ksaproc.ps"
 * >not secure</a>, so this implementation is provided simply as an
 * example of using E for one of the tasks for which E is least well
 * suited. (Use FORTRAN or C for such tasks, and call them from E.)
 * <p>
 * XXX Bug: MarkM apparently broke this when he edited it. A round
 * trip resulted in gibberish.
 *
 * @author Ka-Ping Yee 8 May 2000.
 * @author with some changes by MarkM who apparently broke it
 */
def arcfour(input, key) : any {
    # Perform the ARCFOUR algorithm on a given input string with a given
    # key string, and return the output as a string.
    def [var i, var j, state, var output] := [0, 0, [].diverge(int), ""]
    for i in 0..255 { state.push(i) }
    for i in 0..255 {
        j := (j + state[i] + key[i % key.size()].asInteger()) % 256
        def [a, b] := [state[i], state[j]]
        state[i] := b
        state[j] := a
    }
    i := j := 0
    for ch in input {
        i := (i + 1) % 256
        j := (j + state[i]) % 256
        def [a, b] := [state[i], state[j]]
        state[i] := b
        state[j] := a
        def n := (a + b) % 256
        output += (ch.asInteger() ^ state[n]).asChar()
    }
    output
}

def encipher(plaintext, key) : any {
    # Given a plaintext string and key, return an enciphered string.
    def [rng, var iv] := [<unsafe:java.util.makeRandom>(), ""]
    for i in (1..10) { iv += (rng.nextInt() % 256).asChar() }
    iv + arcfour(plaintext, key + iv)
}

def decipher(ciphertext, key) : any {
    # Given a ciphertext string and key, return the deciphered string.
    arcfour(ciphertext(10, ciphertext.size()), key + ciphertext(0, 10))
}

switch (interp.getArgs()) {
    match [`-d`, key, infile, outfile] {
        <file>[outfile].setText(decipher(<file>[infile].getText(), key))
    }
    match [`-e`, key, infile, outfile] {
        <file>[outfile].setText(encipher(<file>[infile].getText(), key))
    }
    match _ {
        println("usage: ciphersaber.e [-d | -e] <key> <infile> <outfile>")
    }
}
