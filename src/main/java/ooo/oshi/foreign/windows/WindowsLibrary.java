package ooo.oshi.foreign.windows;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.JAVA_INT;

public class WindowsLibrary {

    private static final SymbolLookup SYSTEM = Linker.nativeLinker().defaultLookup();

    /**
     * Returns the process ID of the calling process. The ID is guaranteed to be
     * unique and is useful for constructing temporary file names.
     *
     * @return the process ID of the calling process.
     */
    public static int getpid() {
        try {
            return (int) getpid.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getpid = Linker.nativeLinker()
        .downcallHandle(SYSTEM.lookup("_getpid").orElseThrow(), FunctionDescriptor.of(JAVA_INT));


}
