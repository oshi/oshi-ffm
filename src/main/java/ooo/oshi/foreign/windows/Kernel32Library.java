/*
 * Copyright 2022 the OSHI-FFM project contributors.
 * SPDX-License-Identifier: Apache-2.0
 */
package ooo.oshi.foreign.windows;

import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_BOOLEAN;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static ooo.oshi.foreign.windows.WinBase.TH32CS_SNAPPROCESS;

import java.lang.foreign.Addressable;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

import ooo.oshi.util.ParseUtil;

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


    public static Addressable createToolHelp32Snapshot() {
        try {
            // Output returns open handle to specified snapshot
            Addressable openHandle = (MemoryAddress) createToolHelp32Snapshot.invoke(TH32CS_SNAPPROCESS, 0);
            if (openHandle == null)
                throw new Exception("GetLastError() returned " + getLastError());
            return openHandle;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static final MethodHandle createToolHelp32Snapshot = methodHandle("CreateToolhelp32Snapshot", FunctionDescriptor.of(ADDRESS, JAVA_INT, JAVA_INT));

    /**
     * Retrieves the NetBIOS name of the local computer. This name is established at system startup, when the system
     * reads it from the registry.
     *
     * @return the NetBIOS name of the local computer.
     */
    public static String getComputerName() {
    	try {
        	SegmentAllocator allocator = SegmentAllocator.implicitAllocator();
    		Addressable buffer = allocator.allocate(2 * (WinBase.MAX_COMPUTERNAME_LENGTH + 1));
    		Addressable size = allocator.allocate(JAVA_INT, 2 * (WinBase.MAX_COMPUTERNAME_LENGTH + 1));
            if(!(boolean) getComputerName.invokeExact(buffer, size))	{
            	throw new Exception("GetLastError() returned " + getLastError());
            }
            byte [] bytes = ((MemorySegment)buffer).toArray(ValueLayout.JAVA_BYTE);
            return ParseUtil.parseByteArrayToUtf16(bytes);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getComputerName = methodHandle("GetComputerNameW",
            FunctionDescriptor.of(JAVA_BOOLEAN, ADDRESS, ADDRESS));

	/**
	 * Retrieves the path of the directory designated for temporary files.
	 *
	 * @return the temporary file path.
	 */
	public static String getTempPath() {
		try {
			SegmentAllocator allocator = SegmentAllocator.implicitAllocator();
			// TODO: Handle case if Temp path length exceeds predefined buffer size
			Addressable buffer = allocator.allocate(WinBase.MAX_PATH);
			int nBufferLength = WinBase.MAX_PATH;
			if ((int) getTempPath.invoke(nBufferLength, buffer) == 0) {
				throw new Exception("GetLastError() returned " + getLastError());
			}
			byte[] bytes = ((MemorySegment) buffer).toArray(ValueLayout.JAVA_BYTE);
			return ParseUtil.parseByteArrayToUtf16(bytes);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private static final MethodHandle getTempPath = methodHandle("GetTempPathW",
			FunctionDescriptor.of(JAVA_INT, JAVA_INT, ADDRESS));

	/**
	 * Closes an open object handle.
	 *
	 * @param h The handle to be closed
	 */
	public static void closeHandle(Addressable h) {
		if (h == null) {
			return;
		}

		try {
			if (!(boolean) closeHandle.invokeExact(h)) {
				throw new Exception("GetLastError() returned " + getLastError());
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private static final MethodHandle closeHandle = methodHandle("CloseHandle",
			FunctionDescriptor.of(JAVA_BOOLEAN, ADDRESS));


	/**
	 * Opens an existing local process object.
	 *
	 * @param desiredAccess The access to the process object.
	 * @param inheritHandle Processes created by this process should inherit the handle
	 * @param processId The identifier of the local process to be opened.
	 * @return the open handle to the specified process.
	 */
	public static Addressable openProcess(int desiredAccess, boolean inheritHandle, int processId) {
		try {
			Addressable hProcess = (MemoryAddress) openProcess.invokeExact(desiredAccess, inheritHandle, processId);
			if (hProcess == null) {
				throw new Exception("GetLastError() returned " + getLastError());
			}
			return hProcess;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private static final MethodHandle openProcess = methodHandle("OpenProcess",
			FunctionDescriptor.of(ADDRESS, JAVA_INT, JAVA_BOOLEAN, JAVA_INT));

	private static final String queryFullProcessImageName(Addressable hProcess, int dwFlags) {
		try {
			SegmentAllocator allocator = SegmentAllocator.implicitAllocator();
			int size = WinBase.MAX_PATH; // Start with MAX_PATH, then increment with 1024 each iteration
			// TODO: Handle case if process image length exceeds predefined buffer size
			Addressable lpExeName = allocator.allocate(size);
			Addressable lpdwSize = allocator.allocate(JAVA_INT, size);
			if (!(boolean) queryFullProcessImageName.invokeExact(hProcess, dwFlags, lpExeName, lpdwSize)) {
				throw new Exception("GetLastError() returned " + getLastError());
			}
			byte[] bytes = ((MemorySegment) lpExeName).toArray(ValueLayout.JAVA_BYTE);
			return ParseUtil.parseByteArrayToUtf16(bytes);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
    }

	private static final MethodHandle queryFullProcessImageName = methodHandle("QueryFullProcessImageNameW",
            FunctionDescriptor.of(JAVA_BOOLEAN, ADDRESS, JAVA_INT, ADDRESS, ADDRESS));

	/**
	 * Retrieves the full name of the executable image for the specified process.
	 *
	 * @param pid The identifier of the local process to be opened.
	 * @param dwFlags flags for path format of returned path.
	 * @return the full name of the executable image for the specified process.
	 */
	public static final String queryFullProcessImageName(int pid, int dwFlags) {
		Addressable hProcess = null;
		RuntimeException re = null;

		try {
			hProcess = (Addressable) openProcess(WinBase.PROCESS_QUERY_INFORMATION | WinBase.PROCESS_VM_READ, false,
					pid);
			if (hProcess == null) {
				throw new RuntimeException("GetLastError() returned " + getLastError());
			}
			return queryFullProcessImageName(hProcess, dwFlags);
		} catch (RuntimeException e) {
			re = e;
			throw re; // re-throw to invoke finally block
		} finally {
			try {
				closeHandle(hProcess);
			} catch (RuntimeException e) {
				if (re == null) {
					re = e;
				} else {
					// Suppress Runtime Exception for closeHandle
				}
			}
			if (re != null) {
				throw re;
			}
		}
	}
}
