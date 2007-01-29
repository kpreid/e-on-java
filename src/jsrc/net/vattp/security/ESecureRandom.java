package net.vattp.security;

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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * Cryptographically Strong Random Number Generator
 * <p/>
 * This class provides a cryptographically strong random number generator based
 * on user provided sources of entropy and the MD5 hash algorithm. It maintains
 * an estimate of the amount of entropy supplied. If there is a request for
 * secure random data, a call on nextBytes(), when there is less than 80 bits
 * of randomness available, it will use super.getSeed() to bring the level up
 * to 80 bits.
 * <p/>
 * The calls inherited from Random and SecureRandom are implemented in terms of
 * the strengthened functionality.
 * <p/>
 * Note by msm: Beware, ESecureRandom inheits from SecureRandom, which
 * synchronizes on itself, so ESecureRandom also syncronizes on itself.
 * <p/>
 * Although this class isn't declared Persistent, the relevant instance is
 * expected to be an unscope-key.
 *
 * @author Bill Frantz
 * @see java.util.Random
 * @see java.security.SecureRandom
 */
public class ESecureRandom extends SecureRandom {

    static private final long serialVersionUID = -950065053792620147L;

    //Constants

    static private final int SWISSDATA_SIZE = 20;

    /**
     * static entropy generator reference to start the entropy generation
     * thread
     */
    static private TimerJitterEntropy theTimeJitterEntropy =
      new TimerJitterEntropy("TimerJitterEntropy");

    /**
     * The instance of this singleton class
     */
    static private ESecureRandom theESecureRandom = null;

    // Static state for gathering mouse and keyboard entropy
    /**
     * Last keys seen
     */
    private final transient static int[] theLastKeys = new int[5];

    private transient static int theLastX; // The last mouse x value

    private transient static int theLastY; // The last mouse y value

    private transient static int theLastDX;// The last change in mouse x value

    private transient static int theLastDY;// The last change in mouse y value


    //Output size of the hash function used
    static private final int HASH_SIZE = 16;

    /**
     * Size of the entropy pool in bits.
     * <p/>
     * This constant limits the amount of actual entropy that goes into
     * building the secure random output. N.B. must be a multiple of
     * HASH_SIZE.
     */
    static private final int MAX_ENTROPY = 16 * HASH_SIZE * 8;

    static private final int MIN_ENTROPY = 160;     //Bits

    //State
    private final transient byte[] myEntropyPool =
      new byte[MAX_ENTROPY / 8]; //Where we keep acculminated entropy

    private transient int myPoolCursor = 0;    //Next place in the pool

    private int myAvailableEntropy = 0; // Bits of entropy introduced into pool

    private MessageDigest myMD = null;  //Distilling function

    private long myDigestNumber = 0;    //Increment for each use of myMD

    /**
     * The amount of entropy in the buffer we keep before adding it to the
     * pool. This pool protects against the State Compromise Extension Attack
     * David Wagner identified as a weakness in the previous design. See
     * http://www.cs.berkeley.edu/~daw/prngs-fse98.ps
     */
    private int myBufferedEntropy = 0;

    /**
     * The buffer we keep for entropy before adding it to the pool
     */
    private byte[] myEntropyBuffer = new byte[HASH_SIZE];

    //Constructors

    /**
     * This constructor takes a user-provided seed and entropy estimate. it is
     * the preferred constructor call, if there is any initial entropy
     * available.
     *
     * @param seed    the seed.
     * @param entropy an estimate of the amount of entropy in seed in bits.
     * @throws IllegalArgumentException entropy estimate is less than 1 bit.
     * @throws IllegalArgumentException entropy estimate is greater than 8 bits
     *                                  for every byte of the seed.
     */
    private ESecureRandom(byte[] seed, int entropy) {
        //The super(seed) call here calls our super class constructor and
        //avoids
        //its no parameter constructor's slow self-seeding algorithm. Note
        //that
        //the constructor will call setSeed, which comes back to the setSeed
        //method below. setSeed will be called before the initializer code for
        //this instance has been run, so variables such as myMD will
        //have their default (null) value.
        //
        //All this nonsense just goes to show what a mess this hierarchy is.
        //java.security.SecureRandom should really be an interface, and not a
        //class.
        super(seed);    //Our setSeed will ignore the seed
        try {
            myMD = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new InternalError("MD5 not available.");
        }
        setSeed(seed, entropy); //Now that the world is build, process the seed
        if (Trace.entropy.debug && Trace.ON) {
            Trace.entropy.$("SecureRandomCrew: constructor");
        }
    }
    //Accessors

    /**
     * Get the amount of entropy the generator now holds.
     *
     * @return the number of bits of entropy in the generator.
     */
    public int availableEntropy() {
        return myAvailableEntropy;
    }

    /**
     * Return the singular instance of a SecureRandomCrew. Creates it (using
     * the thread yeald seeding) if necessary.
     *
     * @param innerRandom nullOK an EntropyHolder to keep entropy in the vat.
     */
    static public ESecureRandom getESecureRandom() {
        return getESecureRandom(null, 0);
    }

    /**
     * Return the singular instance of a SecureRandomCrew. Creates it (using
     * the thread yeald seeding) if necessary.
     *
     * @param innerRandom nullOK an EntropyHolder to keep entropy in the vat.
     */
    static public ESecureRandom getESecureRandom(byte[] /*nilok*/ entropy,
                                                 int bitEstimate) {
        // provideEntropy will set theESecureRandom
        if (null == entropy) {
            // One bit from MicroTime.queryTimer()
            byte[] dummySeed = new byte[1];
            provideEntropy(dummySeed, 1);
        } else {
            provideEntropy(entropy, bitEstimate);
        }
        return theESecureRandom;
    }

    /**
     *
     */
    private byte[] long2bytes(long number) {
        byte[] ans = new byte[8];
        for (int i = 7; 0 <= i; i--) {
            ans[i] = (byte)number;
            number >>= 8;
        }
        return ans;
    }

    /**
     * This method provides the secure random output.
     *
     * @param bytes the byte array which will receive the secure random
     *              output.
     */
    public void nextBytes(byte[] bytes) {
        if (Trace.entropy.debug && Trace.ON) {
            Trace.entropy.$("SecureRandomCrew: nextBytes " + bytes.length);
        }
        int cursor = 0;

        if (MIN_ENTROPY > myAvailableEntropy) { // Need to gather entropy
            long genSeedTime = 0;
            long waitSeedTime = 0;
            int seedLength = 0;
            while (MIN_ENTROPY > myAvailableEntropy) { // Wait for seeding
                long startTime =
                  Trace.entropy.event ? System.currentTimeMillis() : 0;
                TimerJitterEntropy tje = theTimeJitterEntropy;
                while (null != tje && !tje.isStarted() && tje.isAlive()) {
                    Thread.yield(); // Wait until it's state has stablized
                }
                // Is there entropy coming?
                if (null != tje && tje.isStarted()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // Ignore it
                    }
                    waitSeedTime += (System.currentTimeMillis() - startTime);
                } else {
                    theTimeJitterEntropy = null;  // Allow GC of TJE thread
                    // Do setSeed(getSeed(1), 8); in separate thread with
                    // timeout
                    Thread generator = new SecureRandomCrewSeedIt(this);
                    generator.start();
                    try {
                        generator.join(10000);
                    } catch (InterruptedException e) {
                        Trace.entropy.errorm("Seed generator interrupted" + e);
                        // Ignore it
                    }
                    if (generator.isAlive()) {
                        //XXX Thread.stop() is now deprecated
                        generator.stop();
                        Trace.entropy.eventm("Seed generator timeout");
                    } else {
                        seedLength++;
                    }
                    genSeedTime += (System.currentTimeMillis() - startTime);
                }
            }
            if (Trace.entropy.event && 450 < waitSeedTime) {
                Trace.entropy
                  .eventm("TimerJitterEntropy Seeding delay " +
                    (waitSeedTime) + " milliseconds");
            }
            if (Trace.entropy.event && 0 < genSeedTime) {
                Trace.entropy
                  .eventm("SecureRandomCrewSeedIt getSeed(" + seedLength +
                    "), time " + genSeedTime + " milliseconds");
            }
        }

        synchronized (this) {
            while (cursor < bytes.length) {
                myMD.update(long2bytes(myDigestNumber));
                myDigestNumber++;
                myMD.update(long2bytes(MicroTime.queryTimer()));
                byte[] rand = myMD.digest(myEntropyPool);
                int len =
                  java.lang.StrictMath.min(rand.length, bytes.length - cursor);
                System.arraycopy(rand, 0, bytes, cursor, len);
                cursor += len;
            }
        }
    }

    /**
     * This method accepts entropy and adds it to the pool. If necessary, it
     * creates the singleton instance of ESecureRandom.
     *
     * @param entropy     a byte array containing the entropy.
     * @param bitEstimate an estimate of the amount of entropy in entropy in
     *                    bits.
     * @throws IllegalArgumentException entropy estimate is less than 1 bit.
     * @throws IllegalArgumentException entropy estimate is greater than 8 bits
     *                                  for every byte of the seed.
     */
    static public void provideEntropy(byte[] entropy, int bitEstimate) {
        if (theESecureRandom == null) {
            theESecureRandom = new ESecureRandom(entropy, bitEstimate);
        } else {
            theESecureRandom.setSeed(entropy, bitEstimate);
        }
    }

    /**
     * This method accepts a keyboard event to input entropy.
     *
     * @param key       the keyboard code.
     * @param modifiers the modifier keys pressed.
     * @param type      the mouse event type code.
     */
    static public void setKeySeed(int key, int modifiers, int type) {
        boolean entropy = true;

        for (int i = 1; i < theLastKeys.length; i++) {
            if (theLastKeys[i] == key) {
                entropy = false;   // No entropy
            }
            theLastKeys[i - 1] = theLastKeys[i];  // Push down our memory
        }
        theLastKeys[theLastKeys.length - 1] = key;
        if (!entropy) {
            return;
        }

        byte[] seed = {(byte)((key >> 8) & 0xff),
          (byte)((key) & 0xff),
          (byte)((modifiers >> 8) & 0xff),
          (byte)((modifiers) & 0xff),
          (byte)((type >> 8) & 0xff),
          (byte)((type) & 0xff)};

        provideEntropy(seed, 1);
        if (Trace.entropy.verbose && Trace.ON) {
            Trace.entropy
              .verbosem("setKeySeed: key=" + key + " modifiers=" + modifiers +
                " type=" + type);
        }
    }

    /**
     * This method accepts a mouse event to input entropy.
     *
     * @param x    the mouse event x value.
     * @param y    the mouse event y value.
     * @param type the mouse event type code.
     */
    static public void setMouseSeed(int x, int y, int type) {
        int dx = theLastX - x;
        int dy = theLastY - y;
        if (0 == dx && 0 == dy) {
            return; // No entropy
        }
        theLastX = x;
        theLastY = y;

        int ddx = theLastDX - dx;
        int ddy = theLastDY - dy;
        if (0 == ddx && 0 == ddy) {
            return; // No entropy
        }
        theLastDX = ddx;
        theLastDY = ddy;

        byte[] seed = {(byte)((x >> 24) & 0xff),
          (byte)((x >> 16) & 0xff),
          (byte)((x >> 8) & 0xff),
          (byte)((x) & 0xff),
          (byte)((y >> 24) & 0xff),
          (byte)((y >> 16) & 0xff),
          (byte)((y >> 8) & 0xff),
          (byte)((y) & 0xff),
          (byte)((type >> 8) & 0xff),
          (byte)((type) & 0xff)};

        provideEntropy(seed, 1);
        if (Trace.entropy.verbose && Trace.ON) {
            Trace.entropy
              .verbosem("setMouseSeed: x=" + x + " y=" + y + " type=" + type);
        }
    }
    //Manipulators

    /**
     * This method is included for compatibility with the super class. As a
     * general statement, the setSeed(seed, entropy) method should be used in
     * its place. This method assumes 1 bit of entropy for each byte of the
     * seed. It is almost certain that this estimate will be wrong.
     *
     * @param seed the seed.
     */
    public void setSeed(byte[] seed) {
        synchronized (this) {
            setSeed(seed, seed.length);     //Assume 1 bit of entropy/byte
        }
    }

    /**
     * This method is the preferred way to provide entropy for later use. In
     * practical systems, any source of entropy available can be mixed in using
     * this method. Examples include: User interface events, disk I/O timings,
     * and random data from quantum-mechanically based hardware.
     *
     * @param seed    the seed.
     * @param entropy an estimate of the amount of entropy in seed in bits.
     * @throws IllegalArgumentException entropy estimate is less than 0 bits.
     * @throws IllegalArgumentException entropy estimate is greater than 8 bits
     *                                  for every byte of the seed.
     */
    public void setSeed(byte[] seed, int entropy) {

        synchronized (this) {
            //Super. stinks. Since the superclass constructor calls setSeed,
            //we
            //must be prepared to handle receiving entropy before the places to
            //hold it are set up through automatic initialization. We will
            //detect
            //the situation by noting that myMD is null and quickly
            //bail. We will then set things up in our own constructor when the
            //state of "this" is a bit more sane.
            if (null == myMD) {
                return;
            }
            if (0 > entropy) {
                throw new IllegalArgumentException(
                  "Less than zero bits of entropy");
            }
            if (entropy > 8 * seed.length) {
                throw new IllegalArgumentException("More entropy than data");
            }
            myBufferedEntropy += entropy;
            byte[] hashBlock = new byte[HASH_SIZE];

            while (true) {
                myMD.update(myEntropyBuffer);
                // Stir in the time
                myMD.update(long2bytes(MicroTime.queryTimer()));
                System.arraycopy(myEntropyPool,
                                 myPoolCursor,
                                 hashBlock,
                                 0,
                                 HASH_SIZE); //current pool block
                myMD.update(hashBlock);
                myEntropyBuffer = myMD.digest(seed);
                if (80 > myBufferedEntropy) {
                    break;
                }

                // We have enough to update the main pool
                myAvailableEntropy += 80;
                myBufferedEntropy -= 80;
                System.arraycopy(myEntropyBuffer,
                                 0,
                                 myEntropyPool,
                                 myPoolCursor,
                                 HASH_SIZE);
                myPoolCursor += HASH_SIZE;
                if (myEntropyPool.length == myPoolCursor) {
                    myPoolCursor = 0;
                }
            }

            //myAvailableEntropy += entropy;
            if (myAvailableEntropy > 4 * myEntropyPool.length) {
                //Never more than 1/2 full
                myAvailableEntropy = 4 * myEntropyPool.length;
            }
        }
    }

    /**
     * MSM: Added this method, so this object acts as a capability for making
     * new unguessable BigIntegers that have new entropy.
     */
    public BigInteger nextSwiss() {
        byte[] swissData = new byte[SWISSDATA_SIZE];
        nextBytes(swissData);
        return new BigInteger(1, swissData);
    }
}
