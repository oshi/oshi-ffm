/*
 * Copyright 2022 the OSHI-FFM project contributors.
 * SPDX-License-Identifier: Apache-2.0
 */
package ooo.oshi.software.os.common;

import static ooo.oshi.software.os.OperatingSystem.ProcessFiltering.ALL_PROCESSES;
import static ooo.oshi.software.os.OperatingSystem.ProcessSorting.NO_SORTING;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ooo.oshi.software.os.OSProcess;
import ooo.oshi.software.os.OperatingSystem;

public abstract class AbstractOperatingSystem implements OperatingSystem {

    @Override
    public List<OSProcess> getProcesses(Predicate<OSProcess> filter, Comparator<OSProcess> sort, int limit) {
        return queryAllProcesses().stream().filter(filter == null ? ALL_PROCESSES : filter)
                .sorted(sort == null ? NO_SORTING : sort).limit(limit > 0 ? limit : Long.MAX_VALUE)
                .collect(Collectors.toList());
    }

    protected abstract List<OSProcess> queryAllProcesses();

}
