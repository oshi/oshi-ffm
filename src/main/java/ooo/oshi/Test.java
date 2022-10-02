/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi;

import java.util.List;
import java.util.Map.Entry;

import ooo.oshi.software.os.OSProcess;
import ooo.oshi.software.os.OperatingSystem;
import ooo.oshi.util.FormatUtil;

public class Test {

    public static void main(String[] args) {
        System.out.println("The operating system is: " + SystemInfo.getCurrentPlatform().getName());
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        System.out.println("The current Process ID is: " + os.getProcessId());

        OSProcess myProc = os.getProcess(os.getProcessId());

        // current process will never be null. Other code should check for null here
        System.out.println(
                "My PID: " + myProc.getProcessID() + " with affinity " + Long.toBinaryString(myProc.getAffinityMask()));
        System.out.println("Name: " + myProc.getUser() + ", Group: " + myProc.getGroup());
        System.out.println("Read: " + myProc.getBytesRead() + ", Written: " + myProc.getBytesWritten());
        System.out.println("CWD: " + myProc.getCurrentWorkingDirectory());
        List<OSProcess> procs = os.getProcesses(OperatingSystem.ProcessFiltering.ALL_PROCESSES,
                OperatingSystem.ProcessSorting.CPU_DESC, 5);
        System.out.println("   PID  %CPU %MEM       VSZ       RSS Name");
        for (OSProcess p : procs) {
            System.out.printf(" %5d %5.1f %4.1f %9s %9s %s%n", p.getProcessID(),
                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
                    100d * p.getResidentSetSize() / (32L << 30), FormatUtil.formatBytes(p.getVirtualSize()),
                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName());
        }
        OSProcess p = os.getProcess(os.getProcessId());
        System.out.println("Current process arguments: ");
        for (String s : p.getArguments()) {
            System.out.println("  " + s);
        }
        System.out.println("Current process environment: ");
        for (Entry<String, String> e : p.getEnvironmentVariables().entrySet()) {
            System.out.println("  " + e.getKey() + "=" + e.getValue());
        }
    }

}
