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

    private static final GroupLayout _WTS_PROCESS_INFOA = MemoryLayout.structLayout(
        JAVA_INT.withName("SessionId"),
        JAVA_INT.withName("ProcessId"),
        JAVA_CHAR.withName("pProcessName"),
        ADDRESS.withName("pUserSid")
    ).withName("WTS_PROCESS_INFOA");

    /**
     * Refer to <a href="https://learn.microsoft.com/en-us/windows/win32/api/wtsapi32/nf-wtsapi32-wtsenumerateprocessesa">Win32API</a>
     */
    public static void enumerateProcesses() {
        try {
            try (MemorySession session = MemorySession.openConfined()) {
                int hServer = 0; // represents null handle
                int version = 1; // version of enumerated request, must be 1
                // out variables
                var processArrayLayout = MemoryLayout.sequenceLayout(1024, _WTS_PROCESS_INFOA);
                Addressable ppProcessInfo = session.allocate(processArrayLayout); // array of WTS_PROCESS_INFO
                Addressable pCount = session.allocate(JAVA_INT); // process count

                if (!(boolean) WTSEnumerateProcessEx.invokeExact(hServer, 0, version, ppProcessInfo, pCount))
                    throw new Exception("error calling WTSEnumerateProcessesA");

                int processCount = pCount.address().get(JAVA_INT, 0);
                System.out.println("Total processes found: " + processCount);

//                var sequenceVH = processArrayLayout.varHandle(PathElement.sequenceElement(), PathElement.groupElement("ProcessId"));
//                System.out.println(sequenceVH.get(ppProcessInfo, 0));
                var vh = processArrayLayout.varHandle(PathElement.sequenceElement(2), PathElement.groupElement("ProcessId"));
                System.out.println(vh.get(ppProcessInfo));
//                System.out.println(ppProcessInfo.address().get(JAVA_INT,0));
//                System.out.println(ppProcessInfo.address().get(JAVA_INT,32));
//                System.out.println(ppProcessInfo.address().get(JAVA_CHAR,64));
//                for (int i = 0; i < processCount; i++) {
//                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

    }


}
