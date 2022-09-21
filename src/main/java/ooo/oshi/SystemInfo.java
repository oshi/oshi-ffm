/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache-2.0 license or a compatible open source license.
 */
package ooo.oshi;

import static ooo.oshi.PlatformEnum.LINUX;
import static ooo.oshi.PlatformEnum.MACOS;
import static ooo.oshi.PlatformEnum.UNSUPPORTED;
import static ooo.oshi.PlatformEnum.WINDOWS;
import static ooo.oshi.util.Memoizer.memoize;

import java.util.function.Supplier;

import ooo.oshi.hardware.HardwareAbstractionLayer;
import ooo.oshi.hardware.linux.LinuxHardwareAbstractionLayer;
import ooo.oshi.hardware.mac.MacHardwareAbstractionLayer;
import ooo.oshi.hardware.windows.WindowsHardwareAbstractionLayer;
import ooo.oshi.software.os.OperatingSystem;
import ooo.oshi.software.os.linux.LinuxOperatingSystem;
import ooo.oshi.software.os.mac.MacOperatingSystem;
import ooo.oshi.software.os.windows.WindowsOperatingSystem;

/**
 * System information. This is the main entry point to OSHI.
 * <p>
 * This object provides getters which instantiate the appropriate
 * platform-specific implementations of {@link oshi.software.os.OperatingSystem}
 * (software) and {@link oshi.hardware.HardwareAbstractionLayer} (hardware).
 */
public class SystemInfo {

    private static final PlatformEnum CURRENT_PLATFORM;
    static {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Linux")) {
            CURRENT_PLATFORM = LINUX;
        } else if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
            CURRENT_PLATFORM = MACOS;
        } else if (osName.startsWith("Windows")) {
            CURRENT_PLATFORM = WINDOWS;
        } else {
            CURRENT_PLATFORM = UNSUPPORTED;
        }
    }

    private static final String NOT_SUPPORTED = "Operating system not supported: ";

    private final Supplier<OperatingSystem> os = memoize(SystemInfo::createOperatingSystem);

    private final Supplier<HardwareAbstractionLayer> hardware = memoize(SystemInfo::createHardware);

    /**
     * Create a new instance of {@link SystemInfo}. This is the main entry point to
     * OSHI and provides access to cross-platform code.
     * <p>
     * Platform-specific Hardware and Software objects are retrieved via memoized
     * suppliers. To conserve memory at the cost of additional processing time,
     * create a new instance of SystemInfo for subsequent calls. To conserve
     * processing time at the cost of additional memory usage, re-use the same
     * {@link SystemInfo} object for future queries.
     */
    public SystemInfo() {
        // Intentionally empty, here to enable the constructor javadoc.
    }

    /**
     * Gets the {@link PlatformEnum} value representing this system.
     *
     * @return Returns the current platform
     */
    public static PlatformEnum getCurrentPlatform() {
        return CURRENT_PLATFORM;
    }

    /**
     * Creates a new instance of the appropriate platform-specific
     * {@link OperatingSystem}.
     *
     * @return A new platform-specific instance implementing
     *         {@link OperatingSystem}.
     */
    public OperatingSystem getOperatingSystem() {
        return os.get();
    }

    private static OperatingSystem createOperatingSystem() {
        switch (CURRENT_PLATFORM) {
        case WINDOWS:
            return new WindowsOperatingSystem();
        case LINUX:
            return new LinuxOperatingSystem();
        case MACOS:
            return new MacOperatingSystem();
        default:
            throw new UnsupportedOperationException(NOT_SUPPORTED);
        }
    }

    /**
     * Creates a new instance of the appropriate platform-specific
     * {@link HardwareAbstractionLayer}.
     *
     * @return A new platform-specific instance implementing
     *         {@link HardwareAbstractionLayer}.
     */
    public HardwareAbstractionLayer getHardware() {
        return hardware.get();
    }

    private static HardwareAbstractionLayer createHardware() {
        switch (CURRENT_PLATFORM) {
        case WINDOWS:
            return new WindowsHardwareAbstractionLayer();
        case LINUX:
            return new LinuxHardwareAbstractionLayer();
        case MACOS:
            return new MacHardwareAbstractionLayer();
        default:
            throw new UnsupportedOperationException(NOT_SUPPORTED);
        }
    }
}
