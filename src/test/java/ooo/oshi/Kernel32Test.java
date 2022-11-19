package ooo.oshi;

import ooo.oshi.foreign.windows.Kernel32Library;
import ooo.oshi.software.os.OperatingSystem;

import java.util.Arrays;

public class Kernel32Test {
	public static void main(String[] args) {

		System.out.println("The operating system is: " + SystemInfo.getCurrentPlatform().getName());
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        int currentProcessId = os.getProcessId();
        System.out.println("The current Process ID is: " + currentProcessId);

		String computerName = Kernel32Library.getComputerName();
		System.out.println("Computer Name: " + computerName);

		String tempPath = Kernel32Library.getTempPath();
		System.out.println("Temp Path: " + tempPath);

		String processName = Kernel32Library.queryFullProcessImageName(currentProcessId, 0);
		System.out.println("Current Process: " + processName);

        var a = Kernel32Library.createToolHelp32Snapshot();
        Kernel32Library.debug(a);
    }
}
