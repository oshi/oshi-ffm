/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi;

import ooo.oshi.software.os.OSProcess;
import ooo.oshi.software.os.OperatingSystem;
import ooo.oshi.software.os.mac.MacOperatingSystem;
import ooo.oshi.util.platform.mac.SysctlUtil;

public class Test {

    public static void main(String[] args) {
        System.out.println("The operating system is: " + SystemInfo.getCurrentPlatform().getName());
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        System.out.println("The current Process ID is: " + os.getProcessId());

        int maxproc = SysctlUtil.sysctl("kern.maxproc", 123);
        System.out.println("kern.maxproc=" + maxproc);

        MacOperatingSystem mos = new MacOperatingSystem();
        for (OSProcess p : mos.getProcesses()) {
            System.out.println(p.getProcessID() + ": " + p.getCommandLine());
        }
    }

}
