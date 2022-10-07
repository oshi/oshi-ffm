/*
 * Copyright 2022 the OSHI-FFM project contributors.
 * SPDX-License-Identifier: Apache-2.0
 */
package ooo.oshi.software.os;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ooo.oshi.software.os.OSProcess.State;

public interface OperatingSystem {

    /**
     * Constants which may be used to filter Process lists in {@link #getProcesses(Predicate, Comparator, int)},
     * {@link #getChildProcesses(int, Predicate, Comparator, int)}, and
     * {@link #getDescendantProcesses(int, Predicate, Comparator, int)}.
     */
    final class ProcessFiltering {
        private ProcessFiltering() {
        }

        /**
         * No filtering.
         */
        public static final Predicate<OSProcess> ALL_PROCESSES = p -> true;
        /**
         * Exclude processes with {@link State#INVALID} process state.
         */
        public static final Predicate<OSProcess> VALID_PROCESS = p -> !p.getState().equals(State.INVALID);
        /**
         * Exclude child processes. Only include processes which are their own parent.
         */
        public static final Predicate<OSProcess> NO_PARENT = p -> p.getParentProcessID() == p.getProcessID();
        /**
         * Only incude 64-bit processes.
         */
        public static final Predicate<OSProcess> BITNESS_64 = p -> p.getBitness() == 64;
        /**
         * Only include 32-bit processes.
         */
        public static final Predicate<OSProcess> BITNESS_32 = p -> p.getBitness() == 32;
    }

    /**
     * Constants which may be used to sort Process lists in {@link #getProcesses(Predicate, Comparator, int)},
     * {@link #getChildProcesses(int, Predicate, Comparator, int)}, and
     * {@link #getDescendantProcesses(int, Predicate, Comparator, int)}.
     */
    final class ProcessSorting {
        private ProcessSorting() {
        }

        /**
         * No sorting
         */
        public static final Comparator<OSProcess> NO_SORTING = (p1, p2) -> 0;
        /**
         * Sort by decreasing cumulative CPU percentage
         */
        public static final Comparator<OSProcess> CPU_DESC = Comparator
                .comparingDouble(OSProcess::getProcessCpuLoadCumulative).reversed();
        /**
         * Sort by decreasing Resident Set Size (RSS)
         */
        public static final Comparator<OSProcess> RSS_DESC = Comparator.comparingLong(OSProcess::getResidentSetSize)
                .reversed();
        /**
         * Sort by up time, newest processes first
         */
        public static final Comparator<OSProcess> UPTIME_ASC = Comparator.comparingLong(OSProcess::getUpTime);
        /**
         * Sort by up time, oldest processes first
         */
        public static final Comparator<OSProcess> UPTIME_DESC = UPTIME_ASC.reversed();
        /**
         * Sort by Process Id
         */
        public static final Comparator<OSProcess> PID_ASC = Comparator.comparingInt(OSProcess::getProcessID);
        /**
         * Sort by Parent Process Id
         */
        public static final Comparator<OSProcess> PARENTPID_ASC = Comparator
                .comparingInt(OSProcess::getParentProcessID);
        /**
         * Sort by Process Name (case insensitive)
         */
        public static final Comparator<OSProcess> NAME_ASC = Comparator.comparing(OSProcess::getName,
                String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Gets currently running processes. No order is guaranteed.
     *
     * @return A list of {@link oshi.software.os.OSProcess} objects for the specified number (or all) of currently
     *         running processes, sorted as specified. The list may contain null elements or processes with a state of
     *         {@link OSProcess.State#INVALID} if a process terminates during iteration.
     */
    default List<OSProcess> getProcesses() {
        return getProcesses(null, null, 0);
    }

    /**
     * Gets currently running processes, optionally filtering, sorting, and limited to the top "N".
     *
     * @param filter An optional {@link Predicate} limiting the results to the specified filter. Some common predicates
     *               are available in {@link ProcessSorting}. May be {@code null} for no filtering.
     * @param sort   An optional {@link Comparator} specifying the sorting order. Some common comparators are available
     *               in {@link ProcessSorting}. May be {@code null} for no sorting.
     * @param limit  Max number of results to return, or 0 to return all results
     * @return A list of {@link oshi.software.os.OSProcess} objects, optionally filtered, sorted, and limited to the
     *         specified number.
     *         <p>
     *         The list may contain processes with a state of {@link OSProcess.State#INVALID} if a process terminates
     *         during iteration.
     */
    List<OSProcess> getProcesses(Predicate<OSProcess> filter, Comparator<OSProcess> sort, int limit);

    /**
     * Gets information on a {@link Collection} of currently running processes. This has potentially improved
     * performance vs. iterating individual processes.
     *
     * @param pids A collection of process IDs
     * @return A list of {@link oshi.software.os.OSProcess} objects for the specified process ids if it is running
     */
    default List<OSProcess> getProcesses(Collection<Integer> pids) {
        return pids.stream().distinct().parallel().map(this::getProcess).filter(Objects::nonNull)
                .filter(ProcessFiltering.VALID_PROCESS).collect(Collectors.toList());
    }

    /**
     * Gets information on a currently running process
     *
     * @param pid A process ID
     * @return An {@link oshi.software.os.OSProcess} object for the specified process id if it is running; null
     *         otherwise
     */
    OSProcess getProcess(int pid);

    /**
     * Gets the current process ID
     *
     * @return the Process ID of the current process
     */
    int getProcessId();
}
