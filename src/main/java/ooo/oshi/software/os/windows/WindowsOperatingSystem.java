/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi.software.os.windows;

import java.util.List;

import ooo.oshi.foreign.windows.Kernel32Library;
import ooo.oshi.software.os.OSProcess;
import ooo.oshi.software.os.common.AbstractOperatingSystem;

public class WindowsOperatingSystem extends AbstractOperatingSystem {

    @Override
    protected List<OSProcess> queryAllProcesses() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OSProcess getProcess(int pid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getProcessId() {
        return Kernel32Library.getpid();
    }
}
