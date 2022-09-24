/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;

import ooo.oshi.foreign.mac.SystemLibrary;
import ooo.oshi.software.os.OperatingSystem;
import ooo.oshi.util.platform.mac.SysctlUtil;

public class Test {

    public static void main(String[] args) {
        System.out.println("The operating system is: " + SystemInfo.getCurrentPlatform().getName());
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        System.out.println("The current Process ID is: " + os.getProcessId());
        int maxproc = SysctlUtil.sysctl("kern.maxproc", 123);
        System.out.println("kern.maxproc=" + maxproc);
        int[] mib = new int[3];
        mib[0] = 1; // CTL_KERN
        mib[1] = 49; // KERN_PROCARGS2
        mib[2] = os.getProcessId();
        MemorySegment m = SysctlUtil.sysctl(mib, maxproc);

        int argc = m.get(ValueLayout.JAVA_INT, 0);
        System.out.println("# of args=" + argc);
        int offset = SystemLibrary.INT_SIZE;
        String cmdLine = m.getUtf8String(offset);
        System.out.println("cmdLine=" + cmdLine);
        offset++;
        offset += cmdLine.getBytes(StandardCharsets.UTF_8).length;

        while (argc-- > 0) {
            String arg = m.getUtf8String(offset);
            System.out.println("arg=" + arg);
            offset++;
            offset += arg.getBytes(StandardCharsets.UTF_8).length;
        }

        while (offset < maxproc) {
            String env = m.getUtf8String(offset);
            System.out.println("env=" + env);
            if (env.isBlank()) {
                break;
            }
            offset++;
            offset += env.getBytes(StandardCharsets.UTF_8).length;
        }
    }

}
