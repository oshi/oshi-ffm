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
package ooo.oshi.foreign.mac;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;

public class SystemLibrary {

    private static final SymbolLookup SYSTEM_LIBRARY = Linker.nativeLinker().defaultLookup();

    public static MethodHandle getpid = Linker.nativeLinker()
            .downcallHandle(SYSTEM_LIBRARY.lookup("getpid").orElseThrow(), FunctionDescriptor.of(JAVA_INT));

}
