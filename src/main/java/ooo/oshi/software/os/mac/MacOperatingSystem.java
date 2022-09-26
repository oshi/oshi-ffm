/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi.software.os.mac;

import static java.lang.foreign.MemoryAddress.NULL;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static ooo.oshi.foreign.mac.SystemLibrary.INT_SIZE;
import static ooo.oshi.foreign.mac.SystemLibrary.PROC_ALL_PIDS;
import static ooo.oshi.foreign.mac.SystemLibrary.getpid;
import static ooo.oshi.foreign.mac.SystemLibrary.proc_listpids;
import static ooo.oshi.software.os.OSProcess.State.INVALID;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ooo.oshi.software.os.OSProcess;
import ooo.oshi.software.os.common.AbstractOperatingSystem;
import ooo.oshi.util.ExecutingCommand;
import ooo.oshi.util.ParseUtil;
import ooo.oshi.util.platform.mac.SysctlUtil;

public class MacOperatingSystem extends AbstractOperatingSystem {

    private final int major;
    private final int minor;

    public MacOperatingSystem() {
        String version = System.getProperty("os.version");
        int verMajor = ParseUtil.getFirstIntValue(version);
        int verMinor = ParseUtil.getNthIntValue(version, 2);
        // Big Sur (11.x) may return 10.16
        if (verMajor == 10 && verMinor > 15) {
            String swVers = ExecutingCommand.getFirstAnswer("sw_vers -productVersion");
            if (!swVers.isEmpty()) {
                version = swVers;
            }
            verMajor = ParseUtil.getFirstIntValue(version);
            verMinor = ParseUtil.getNthIntValue(version, 2);
        }
        // this.osXVersion = version;
        this.major = verMajor;
        this.minor = verMinor;
        SysctlUtil.sysctl("kern.maxproc", 0x1000);
    }

    @Override
    protected List<OSProcess> queryAllProcesses() {
        int numberOfProcesses = proc_listpids(PROC_ALL_PIDS, 0, NULL, 0) / INT_SIZE;
        int[] pids = new int[numberOfProcesses];
        SegmentAllocator allocator = SegmentAllocator.implicitAllocator();
        MemorySegment cPids = allocator.allocateArray(JAVA_INT, pids);
        numberOfProcesses = proc_listpids(PROC_ALL_PIDS, 0, cPids, pids.length * INT_SIZE) / INT_SIZE;
        pids = cPids.toArray(JAVA_INT);
        return Arrays.stream(pids).distinct().parallel().mapToObj(this::getProcess).filter(Objects::nonNull)
                .filter(ProcessFiltering.VALID_PROCESS).collect(Collectors.toList());
    }

    @Override
    public OSProcess getProcess(int pid) {
        OSProcess proc = new MacOSProcess(pid, this.major, this.minor);
        return proc.getState().equals(INVALID) ? null : proc;
    }

    @Override
    public int getProcessId() {
        return getpid();
    }
}
