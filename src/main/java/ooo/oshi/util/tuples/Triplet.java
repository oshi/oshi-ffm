/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi.util.tuples;

import ooo.oshi.annotation.concurrent.ThreadSafe;

/**
 * Convenience class for returning multiple objects from methods.
 *
 * @param <A> Type of the first element
 * @param <B> Type of the second element
 * @param <C> Type of the third element
 */
@ThreadSafe
public record Triplet<A, B, C>(A a, B b, C c) {

    /**
     * Create a triplet and store three objects.
     *
     * @param a the first object to store
     * @param b the second object to store
     * @param c the third object to store
     */
    public Triplet {
    }

    /**
     * Returns the first stored object.
     *
     * @return first object stored
     */
    public final A getA() {
        return a;
    }

    /**
     * Returns the second stored object.
     *
     * @return second object stored
     */
    public final B getB() {
        return b;
    }

    /**
     * Returns the third stored object.
     *
     * @return third object stored
     */
    public final C getC() {
        return c;
    }
}
