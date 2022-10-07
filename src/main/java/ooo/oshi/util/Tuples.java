/*
 * Copyright 2022 the OSHI-FFM project contributors.
 * SPDX-License-Identifier: Apache-2.0
 */
package ooo.oshi.util;

import ooo.oshi.annotation.concurrent.ThreadSafe;

/**
 * Tuples utility class
 */
@ThreadSafe
public class Tuples {

    /**
     * Convenience class for returning multiple objects from methods.
     *
     * @param <A> Type of the first element
     * @param <B> Type of the second element
     */
    public static record Pair<A, B> (A a, B b) {
    }

    /**
     * Convenience class for returning multiple objects from methods.
     *
     * @param <A> Type of the first element
     * @param <B> Type of the second element
     * @param <C> Type of the third element
     */
    public static record Triplet<A, B, C> (A a, B b, C c) {
    }

    /**
     * Convenience class for returning multiple objects from methods.
     *
     * @param <A> Type of the first element
     * @param <B> Type of the second element
     * @param <C> Type of the third element
     * @param <D> Type of the fourth element
     */
    public static record Quartet<A, B, C, D> (A a, B b, C c, D d) {
    }

    /**
     * Convenience class for returning multiple objects from methods.
     *
     * @param <A> Type of the first element
     * @param <B> Type of the second element
     * @param <C> Type of the third element
     * @param <D> Type of the fourth element
     * @param <E> Type of the fifth element
     */
    public static record Quintet<A, B, C, D, E> (A a, B b, C c, D d, E e) {
    }
}
