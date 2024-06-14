package ooo.oshi;

import ooo.oshi.foreign.windows.Kernel32Library;
import ooo.oshi.foreign.windows.WtsApi32;
import ooo.oshi.software.os.OperatingSystem;

public class Kernel32Test {
	public static void main(String[] args) {


        WtsApi32.enumerateProcesses();

//		System.out.println("The operating system is: " + SystemInfo.getCurrentPlatform().getName());
//        SystemInfo si = new SystemInfo();
//        OperatingSystem os = si.getOperatingSystem();
//        int currentProcessId = os.getProcessId();
//        System.out.println("The current Process ID is: " + currentProcessId);
//
//		String computerName = Kernel32Library.getComputerName();
//		System.out.println("Computer Name: " + computerName);
//
//		String tempPath = Kernel32Library.getTempPath();
//		System.out.println("Temp Path: " + tempPath);
//
//		String processName = Kernel32Library.queryFullProcessImageName(currentProcessId, 0);
//		System.out.println("Current Process: " + processName);
	}
}
