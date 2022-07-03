/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi;

/**
 * An enumeration of supported operating systems.
 */
public enum PlatformEnum {
    /**
     * macOS
     */
    MACOS("macOS"),
    /**
     * A flavor of Linux
     */
    LINUX("Linux"),
    /**
     * Microsoft Windows
     */
    WINDOWS("Windows"),
    /**
     * Unsupported OS
     */
    UNSUPPORTED("Unsupported Operating System");

    private final String name;

    PlatformEnum(String name) {
        this.name = name;
    }

    /**
     * Gets the friendly name of the platform
     *
     * @return the friendly name of the platform
     */
    public String getName() {
        return this.name;
    }
}
