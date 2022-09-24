/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi.foreign.mac;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;

public class SystemLibrary {

    // Data size
    public static final int INT_SIZE = (int) JAVA_INT.byteSize();
    public static final int LONG_SIZE = (int) JAVA_LONG.byteSize();

    // proc_info.h
    public static final int PROC_ALL_PIDS = 1;

    /*
     * Library loading
     */
    private static final SymbolLookup SYSTEM_LIBRARY = Linker.nativeLinker().defaultLookup();

    /*
     * Util
     */
    public static MethodHandle perror = Linker.nativeLinker()
            .downcallHandle(SYSTEM_LIBRARY.lookup("perror").orElseThrow(), FunctionDescriptor.of(ADDRESS, ADDRESS));

    /*
     * Process
     */
    public static MethodHandle getpid = Linker.nativeLinker()
            .downcallHandle(SYSTEM_LIBRARY.lookup("getpid").orElseThrow(), FunctionDescriptor.of(JAVA_INT));

    public static MethodHandle proc_listpids = Linker.nativeLinker().downcallHandle(
            SYSTEM_LIBRARY.lookup("proc_listpids").orElseThrow(),
            FunctionDescriptor.of(JAVA_INT, JAVA_INT, JAVA_INT, ADDRESS, JAVA_INT));

    /*
     * Sysctl
     */
    public static MethodHandle sysctl = Linker.nativeLinker().downcallHandle(
            SYSTEM_LIBRARY.lookup("sysctl").orElseThrow(),
            FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT, ADDRESS, ADDRESS, ADDRESS, JAVA_LONG));

    public static MethodHandle sysctlbyname = Linker.nativeLinker().downcallHandle(
            SYSTEM_LIBRARY.lookup("sysctlbyname").orElseThrow(),
            FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS, ADDRESS, ADDRESS, JAVA_LONG));
}
