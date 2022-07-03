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
package ooo.oshi.annotation.concurrent;

import java.lang.annotation.Documented;

/**
 * The presence of this annotation indicates that the author believes the class
 * is not thread-safe. The absence of this annotation does not indicate that the
 * class is thread-safe, instead this annotation is for cases where a naïve
 * assumption could be easily made that the class is thread-safe. In general, it
 * is a bad plan to assume a class is thread safe without good reason.
 * <p>
 * This annotation is intended for internal use in OSHI as a temporary
 * workaround until it is available in {@code jakarta.annotations}.
 */
@Documented
public @interface NotThreadSafe {
}
