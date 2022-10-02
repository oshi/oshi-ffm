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
 * @param <A>
 *            Type of the first element
 * @param <B>
 *            Type of the second element
 */
@ThreadSafe
public record Pair<A, B> (A a, B b) {
}
