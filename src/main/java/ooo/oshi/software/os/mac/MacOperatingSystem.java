/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi.software.os.mac;

import static ooo.oshi.foreign.mac.SystemLibrary.getpid;

import ooo.oshi.software.os.OperatingSystem;

public class MacOperatingSystem implements OperatingSystem {

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
