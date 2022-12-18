package ooo.oshi.foreign.windows;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.*;

public class WtsApi32 {

    private static final SymbolLookup WTSAPI32;
    private static final Linker LINKER;

    static {
        LINKER = Linker.nativeLinker();
        System.loadLibrary("wtsapi32");
        WTSAPI32 = SymbolLookup.loaderLookup();
    }

    private static MethodHandle methodHandle(String methodName, FunctionDescriptor fd) {
        return LINKER.downcallHandle(WTSAPI32.lookup(methodName).orElseThrow(), fd);
    }

    private static final MethodHandle WTSEnumerateProcessEx = methodHandle("WTSEnumerateProcessesA", FunctionDescriptor.of(JAVA_BOOLEAN, JAVA_INT, JAVA_INT, JAVA_INT, ADDRESS, ADDRESS));

    private static final MemoryLayout _WTS_PROCESS_INFOA = MemoryLayout.structLayout(
        JAVA_INT.withName("SessionId"),
        JAVA_INT.withName("ProcessId"),
        JAVA_CHAR.withName("pProcessName"),
        ADDRESS.withName("pUserSid")
    ).withName("WTS_PROCESS_INFOA");

    /**
     * Refer to <a href="https://github.com/VFPX/Win32API/blob/master/libraries/wtsapi32/WTSEnumerateProcesses.md">Win32API</a>
     */
    public static void enumerateProcesses() {
        try {
            try (MemorySession session = MemorySession.openConfined()) {
                int hServer = 0; // represents null handle
                int version = 1; // version of enumerated request, must be 1
                // out variables
                Addressable ppProcessInfo = session.allocate(_WTS_PROCESS_INFOA); // array of WTS_PROCESS_INFO
                Addressable pCount = session.allocate(JAVA_INT); // process count
                var ret = (boolean) WTSEnumerateProcessEx.invokeExact(hServer, 0, version, ppProcessInfo, pCount);

                if (!ret) throw new Exception("error calling WTSEnumerateProcessesA");

                int processCount = ppProcessInfo.address().get(JAVA_INT, 0) - 1;
                System.out.println("Total processes found: " + processCount);

//                for (int i = 0; i < processCount; i++) {
//
//                }

                System.out.println(pCount.address().get(JAVA_INT, 0));
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

    }


}
