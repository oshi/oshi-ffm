/*
 * Copyright 2022 the OSHI-FFM project contributors.
 * SPDX-License-Identifier: Apache-2.0
 */
package ooo.oshi.foreign.windows;

import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.JAVA_INT;

public class Kernel32Library {

    private static final SymbolLookup Kernel32;
    private static final Linker linker;

    static {
        linker = Linker.nativeLinker();
        System.loadLibrary("Kernel32");
        Kernel32 = SymbolLookup.loaderLookup();
    }

    private static final MethodHandle getLastError = linker
            .downcallHandle(Kernel32.lookup("GetLastError").orElseThrow(), FunctionDescriptor.of(ValueLayout.JAVA_INT));

    public static int getLastError() {
        try {
            return (int) getLastError.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getCurrentProcessId = linker
            .downcallHandle(Kernel32.lookup("GetCurrentProcessId").orElseThrow(), FunctionDescriptor.of(JAVA_INT));

    /**
     * Returns the process ID of the calling process. The ID is guaranteed to be unique and is useful for constructing
     * temporary file names.
     *
     * @return the process ID of the calling process.
     */
    public static int getCurrentProcessId() {
        try {
            return (int) getCurrentProcessId.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
