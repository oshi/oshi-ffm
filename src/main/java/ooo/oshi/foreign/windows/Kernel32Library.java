/*
 * Copyright 2022 the OSHI-FFM project contributors.
 * SPDX-License-Identifier: Apache-2.0
 */
package ooo.oshi.foreign.windows;

import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

public class Kernel32Library {

    private static final SymbolLookup K32;
    private static final Linker LINKER;

    static {
        LINKER = Linker.nativeLinker();
        System.loadLibrary("Kernel32");
        K32 = SymbolLookup.loaderLookup();
    }

    private static final MethodHandle methodHandle(String methodName, FunctionDescriptor fd) {
        return LINKER.downcallHandle(K32.lookup(methodName).orElseThrow(), fd);
    }

    /**
     * Retrieves the calling thread's last-error code value. The last-error code is maintained on a per-thread basis.
     * Multiple threads do not overwrite each other's last-error code.
     * 
     * @return the calling thread's last-error code.
     */
    public static int getLastError() {
        try {
            return (int) getLastError.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getLastError = methodHandle("GetLastError",
            FunctionDescriptor.of(ValueLayout.JAVA_INT));

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

    private static final MethodHandle getCurrentProcessId = methodHandle("GetCurrentProcessId",
            FunctionDescriptor.of(JAVA_INT));

}
