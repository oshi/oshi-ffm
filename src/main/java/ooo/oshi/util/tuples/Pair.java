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
 */
@ThreadSafe
public record Pair<A, B>(A a, B b) {

    /**
     * Create a pair and store two objects.
     *
     * @param a the first object to store
     * @param b the second object to store
     */
    public Pair {
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
}
