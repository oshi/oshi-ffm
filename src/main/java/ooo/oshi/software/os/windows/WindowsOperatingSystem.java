/*
 * Copyright 2022 the OSHI-FFM project contributors.
 * SPDX-License-Identifier: Apache-2.0
 */
package ooo.oshi.software.os.windows;

import java.util.List;

import ooo.oshi.software.os.OSProcess;
import ooo.oshi.software.os.common.AbstractOperatingSystem;

import static ooo.oshi.foreign.windows.Kernel32Library.getCurrentProcessId;

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
        return getCurrentProcessId();
    }
}
