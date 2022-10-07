/*
 * Copyright 2022 the OSHI-FFM project contributors.
 * SPDX-License-Identifier: Apache-2.0
 */
package ooo.oshi.software.os.linux;

import java.util.List;

import ooo.oshi.software.os.OSProcess;
import ooo.oshi.software.os.common.AbstractOperatingSystem;

public class LinuxOperatingSystem extends AbstractOperatingSystem {

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
        // TODO Auto-generated method stub
        return 0;
    }
}
