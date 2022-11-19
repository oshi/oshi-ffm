package ooo.oshi.foreign.windows;


public class WinBase {

	public static final int MAX_COMPUTERNAME_LENGTH = 31;

	public static final int MAX_PATH = 260;

	public static final int PROCESS_QUERY_INFORMATION = 0x0400;

	public static final int PROCESS_VM_READ = 0x0010;

	public static final int ERROR_INSUFFICIENT_BUFFER = 122;

	public static final int PROCESS_NAME_NATIVE = 1;

    /**
     * Includes all processes in the system in the snapshot. To enumerate the processes.
     *
     * @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/tlhelp32/nf-tlhelp32-createtoolhelp32snapshot">CreateToolHelp32Snapshot</a>
     */
    public static final int TH32CS_SNAPPROCESS = 0x00000002;

}
