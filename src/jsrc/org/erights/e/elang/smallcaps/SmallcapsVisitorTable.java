package org.erights.e.elang.smallcaps;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * The different types of SmallcapsEncoderVisitor emitting onto a
 * SmallcapsEmitter need to refer to each other. This collects them
 * into one place.
 *
 * @author Mark S. Miller
 * @author Darius Bacon
 */
class SmallcapsVisitorTable {

    /**
     * For effects and value
     */
    final SmallcapsEncoderVisitor forValue;

    /**
     * For effects and control
     */
    final SmallcapsEncoderVisitor forControl;

    /**
     * For effects only
     */
    final SmallcapsEncoderVisitor forFxOnly;

    /**
     *
     */
    SmallcapsVisitorTable(SmallcapsEmitter emitter) {
        forValue = new SmallcapsEncoderVisitor(emitter, "FOR_VALUE", this);
        forControl = new SmallcapsEncoderVisitor(emitter, "FOR_CONTROL", this);
        forFxOnly = new SmallcapsEncoderVisitor(emitter, "FOR_FX_ONLY", this);
    }


    /**
     *
     */
//     static public byte[] encodeEExpr(EExpr eExpr, Emitter emitter) {
//         SmallcapsVisitorTable visitors = new SmallcapsVisitorTable(emitter);
//         visitors.forValue.run(eExpr);
//         emitter.emitContourTop();
//         return emitter.copyBytes();
//     }
}
