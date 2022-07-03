/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi.software.os;

public interface OperatingSystem {
    /**
     * Gets the current process ID
     *
     * @return the Process ID of the current process
     */
    int getProcessId();
}
