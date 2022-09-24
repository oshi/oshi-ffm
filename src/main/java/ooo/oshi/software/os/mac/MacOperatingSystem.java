/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi.software.os.mac;

import static ooo.oshi.foreign.mac.SystemLibrary.getpid;
import static ooo.oshi.foreign.mac.SystemLibrary.proc_listpids;
import static ooo.oshi.software.os.OSProcess.State.INVALID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ooo.oshi.foreign.mac.SystemLibrary;
import ooo.oshi.software.os.OSProcess;
import ooo.oshi.software.os.common.AbstractOperatingSystem;
import ooo.oshi.util.ExecutingCommand;
import ooo.oshi.util.ParseUtil;
import ooo.oshi.util.platform.mac.SysctlUtil;

public class MacOperatingSystem extends AbstractOperatingSystem {

    private int maxProc = 1024;
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
        // Set max processes
        this.maxProc = SysctlUtil.sysctl("kern.maxproc", 0x1000);
    }

    @Override
    protected List<OSProcess> queryAllProcesses() {
        List<OSProcess> procs = new ArrayList<>();
        int[] pids = new int[this.maxProc];
        Arrays.fill(pids, -1);
        int numberOfProcesses;
        try {
            numberOfProcesses = (int) proc_listpids.invoke(SystemLibrary.PROC_ALL_PIDS, 0, pids,
                    pids.length * SystemLibrary.INT_SIZE) / SystemLibrary.INT_SIZE;
        } catch (Throwable e) {
            // TODO Custom exception? Return an Optional?
            return Collections.emptyList();
        }
        for (int i = 0; i < numberOfProcesses; i++) {
            if (pids[i] >= 0) {
                OSProcess proc = getProcess(pids[i]);
                if (proc != null) {
                    procs.add(proc);
                }
            }
        }
        return procs;
    }

    @Override
    public OSProcess getProcess(int pid) {
        OSProcess proc = new MacOSProcess(pid, this.major, this.minor);
        return proc.getState().equals(INVALID) ? null : proc;
    }

    @Override
    public int getProcessId() {
        try {
            return (int) getpid.invoke();
        } catch (Throwable e) {
            // TODO Custom exception? Return an Optional?
            return 0;
        }
    }
}
