/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi.foreign.mac;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

public class SystemLibrary {

    private static final SymbolLookup SYSTEM_LIBRARY = Linker.nativeLinker().defaultLookup();

    public static MethodHandle getpid = Linker.nativeLinker()
            .downcallHandle(SYSTEM_LIBRARY.lookup("getpid").orElseThrow(), FunctionDescriptor.of(ValueLayout.JAVA_INT));

}
