/*
 * Copyright 2022 Daniel Widdis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ooo.oshi.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import ooo.oshi.annotation.concurrent.ThreadSafe;

/**
 * A memoized function stores the output corresponding to some set of specific
 * inputs. Subsequent calls with remembered inputs return the remembered result
 * rather than recalculating it.
 */
@ThreadSafe
public final class Memoizer {

    private static final Supplier<Long> DEFAULT_EXPIRATION_NANOS = memoize(Memoizer::queryExpirationConfig,
            TimeUnit.MINUTES.toNanos(1));

    private Memoizer() {
    }

    private static long queryExpirationConfig() {
        return TimeUnit.MILLISECONDS.toNanos(300);
    }

    /**
     * Default exipiration of memoized values in nanoseconds, which will refresh
     * after this time elapses. Update by setting {@link GlobalConfig} property
     * <code>oshi.util.memoizer.expiration</code> to a value in milliseconds.
     *
     * @return The number of nanoseconds to keep memoized values before refreshing
     */
    public static long defaultExpiration() {
        return DEFAULT_EXPIRATION_NANOS.get();
    }

    /**
     * Store a supplier in a delegate function to be computed once, and only again
     * after time to live (ttl) has expired.
     *
     * @param <T>
     *            The type of object supplied
     * @param original
     *            The {@link java.util.function.Supplier} to memoize
     * @param ttlNanos
     *            Time in nanoseconds to retain calculation. If negative, retain
     *            indefinitely.
     * @return A memoized version of the supplier
     */
    public static <T> Supplier<T> memoize(Supplier<T> original, long ttlNanos) {
        // Adapted from Guava's ExpiringMemoizingSupplier
        return new Supplier<T>() {
            final Supplier<T> delegate = original;
            volatile T value; // NOSONAR squid:S3077
            volatile long expirationNanos;

            @Override
            public T get() {
                long nanos = expirationNanos;
                long now = System.nanoTime();
                if (nanos == 0 || (ttlNanos >= 0 && now - nanos >= 0)) {
                    synchronized (this) {
                        if (nanos == expirationNanos) { // recheck for lost race
                            T t = delegate.get();
                            value = t;
                            nanos = now + ttlNanos;
                            expirationNanos = (nanos == 0) ? 1 : nanos;
                            return t;
                        }
                    }
                }
                return value;
            }
        };
    }

    /**
     * Store a supplier in a delegate function to be computed only once.
     *
     * @param <T>
     *            The type of object supplied
     * @param original
     *            The {@link java.util.function.Supplier} to memoize
     * @return A memoized version of the supplier
     */
    public static <T> Supplier<T> memoize(Supplier<T> original) {
        return memoize(original, -1L);
    }
}
