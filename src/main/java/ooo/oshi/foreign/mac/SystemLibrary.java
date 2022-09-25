/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi.foreign.mac;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

import java.lang.foreign.Addressable;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryAddress;
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
    private static final SymbolLookup SYSTEM = Linker.nativeLinker().defaultLookup();

    /*
     * Util
     */

    /**
     * Gets the last error value ({@code errno}).
     * 
     * @return the value of the native errno variable.
     */
    public static int errno() {
        try {
            MemoryAddress addr = (MemoryAddress) errno.invokeExact();
            return addr.get(JAVA_INT, 0);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodHandle errno = Linker.nativeLinker().downcallHandle(SYSTEM.lookup("__error").orElseThrow(),
            FunctionDescriptor.of(ADDRESS));

    /*
     * Process
     */

    /**
     * Returns the process ID of the calling process. The ID is guaranteed to be
     * unique and is useful for constructing temporary file names.
     *
     * @return the process ID of the calling process.
     */
    public static int getpid() {
        try {
            return (int) getpid.invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getpid = Linker.nativeLinker()
            .downcallHandle(SYSTEM.lookup("getpid").orElseThrow(), FunctionDescriptor.of(JAVA_INT));

    /**
     * Search through the current processes
     *
     * @param type
     *            types of processes to be searched
     * @param typeinfo
     *            adjunct information for type
     * @param pids
     *            a C array of int-sized values to be filled with process
     *            identifiers that hold an open file reference matching the
     *            specified path or volume. Pass NULL to obtain the minimum buffer
     *            size needed to hold the currently active processes.
     * @param bufferSize
     *            the size (in bytes) of the provided buffer.
     * @return the number of bytes of data returned in the provided buffer; -1 if an
     *         error was encountered;
     */
    public static int proc_listpids(int type, int typeinfo, Addressable pids, int bufferSize) {
        try {
            return (int) proc_listpids.invokeExact(type, typeinfo, pids, bufferSize);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle proc_listpids = Linker.nativeLinker().downcallHandle(
            SYSTEM.lookup("proc_listpids").orElseThrow(),
            FunctionDescriptor.of(JAVA_INT, JAVA_INT, JAVA_INT, ADDRESS, JAVA_INT));

    /**
     * The sysctl() function retrieves system information and allows processes with
     * appropriate privileges to set system information. The information available
     * from sysctl() consists of integers, strings, and tables.
     * <p>
     * The state is described using a "Management Information Base" (MIB) style
     * name, listed in name, which is a namelen length array of integers.
     * <p>
     * The information is copied into the buffer specified by oldp. The size of the
     * buffer is given by the location specified by oldlenp before the call, and
     * that location gives the amount of data copied after a successful call and
     * after a call that returns with the error code ENOMEM. If the amount of data
     * available is greater than the size of the buffer supplied, the call supplies
     * as much data as fits in the buffer provided and returns with the error code
     * ENOMEM. If the old value is not desired, oldp and oldlenp should be set to
     * NULL.
     * <p>
     * The size of the available data can be determined by calling sysctl() with the
     * NULL argument for oldp. The size of the available data will be returned in
     * the location pointed to by oldlenp. For some operations, the amount of space
     * may change often. For these operations, the system attempts to round up so
     * that the returned size is large enough for a call to return the data shortly
     * thereafter.
     * <p>
     * To set a new value, newp is set to point to a buffer of length newlen from
     * which the requested value is to be taken. If a new value is not to be set,
     * newp should be set to NULL and newlen set to 0.
     *
     * @param name
     *            a Management Information Base (MIB) array of integers
     * @param namelen
     *            the length of the array in {@code name}
     * @param oldp
     *            A buffer to hold the information retrieved
     * @param oldlenp
     *            Size of the buffer, a pointer to a
     *            {@link com.sun.jna.platform.unix.LibCAPI.size_t} value
     * @param newp
     *            To set a new value, a buffer of information to be written. May be
     *            null if no value is to be set.
     * @param newlen
     *            Size of the information to be written. May be 0 if no value is to
     *            be set.
     * @return 0 on success; sets errno on failure
     */
    public static int sysctl(Addressable name, int namelen, Addressable oldp, Addressable oldlenp, Addressable newp,
            long newlen) {
        try {
            return (int) sysctl.invokeExact(name, namelen, oldp, oldlenp, newp, newlen);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle sysctl = Linker.nativeLinker().downcallHandle(
            SYSTEM.lookup("sysctl").orElseThrow(),
            FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT, ADDRESS, ADDRESS, ADDRESS, JAVA_LONG));

    /**
     * The sysctlbyname() function accepts an ASCII representation of the name and
     * internally looks up the integer name vector. Apart from that, it behaves the
     * same as the standard sysctl() function.
     *
     * @param name
     *            ASCII representation of the MIB name
     * @param oldp
     *            A buffer to hold the information retrieved
     * @param oldlenp
     *            Size of the buffer, a pointer to a
     *            {@link com.sun.jna.platform.unix.LibCAPI.size_t} value
     * @param newp
     *            To set a new value, a buffer of information to be written. May be
     *            null if no value is to be set.
     * @param newlen
     *            Size of the information to be written. May be 0 if no value is to
     *            be set.
     * @return 0 on success; sets errno on failure
     */
    public static int sysctlbyname(Addressable name, Addressable oldp, Addressable oldlenp, Addressable newp,
            long newlen) {
        try {
            return (int) sysctlbyname.invokeExact(name, oldp, oldlenp, newp, newlen);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle sysctlbyname = Linker.nativeLinker().downcallHandle(
            SYSTEM.lookup("sysctlbyname").orElseThrow(),
            FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS, ADDRESS, ADDRESS, JAVA_LONG));
}
