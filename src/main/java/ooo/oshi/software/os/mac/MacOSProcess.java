/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributions to this file must be licensed under the Apache 2.0 license or a compatible open source license.
 */
package ooo.oshi.software.os.mac;

import static java.lang.foreign.MemoryAddress.NULL;
import static java.lang.foreign.MemoryLayout.PathElement.groupElement;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;
import static ooo.oshi.foreign.mac.SystemLibrary.GROUP;
import static ooo.oshi.foreign.mac.SystemLibrary.MAXCOMLEN;
import static ooo.oshi.foreign.mac.SystemLibrary.MAXPATHLEN;
import static ooo.oshi.foreign.mac.SystemLibrary.PASSWD;
import static ooo.oshi.foreign.mac.SystemLibrary.PROC_PIDPATHINFO_MAXSIZE;
import static ooo.oshi.foreign.mac.SystemLibrary.PROC_PIDTASKALLINFO;
import static ooo.oshi.foreign.mac.SystemLibrary.PROC_PIDVNODEPATHINFO;
import static ooo.oshi.foreign.mac.SystemLibrary.PROC_TASK_ALL_INFO;
import static ooo.oshi.foreign.mac.SystemLibrary.RUSAGEINFOV2;
import static ooo.oshi.foreign.mac.SystemLibrary.RUSAGE_INFO_V2;
import static ooo.oshi.foreign.mac.SystemLibrary.VNODE_PATH_INFO;
import static ooo.oshi.foreign.mac.SystemLibrary.errno;
import static ooo.oshi.foreign.mac.SystemLibrary.getgrgid;
import static ooo.oshi.foreign.mac.SystemLibrary.getpwuid;
import static ooo.oshi.foreign.mac.SystemLibrary.proc_pid_rusage;
import static ooo.oshi.foreign.mac.SystemLibrary.proc_pidinfo;
import static ooo.oshi.foreign.mac.SystemLibrary.proc_pidpath;
import static ooo.oshi.software.os.OSProcess.State.INVALID;
import static ooo.oshi.software.os.OSProcess.State.NEW;
import static ooo.oshi.software.os.OSProcess.State.OTHER;
import static ooo.oshi.software.os.OSProcess.State.RUNNING;
import static ooo.oshi.software.os.OSProcess.State.SLEEPING;
import static ooo.oshi.software.os.OSProcess.State.STOPPED;
import static ooo.oshi.software.os.OSProcess.State.WAITING;
import static ooo.oshi.software.os.OSProcess.State.ZOMBIE;
import static ooo.oshi.util.Memoizer.memoize;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import ooo.oshi.util.Tuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ooo.oshi.annotation.concurrent.ThreadSafe;
import ooo.oshi.foreign.mac.SystemLibrary;
import ooo.oshi.software.os.OSThread;
import ooo.oshi.software.os.common.AbstractOSProcess;
import ooo.oshi.util.platform.mac.SysctlUtil;

/**
 * OSProcess implementation
 */
@ThreadSafe
public class MacOSProcess extends AbstractOSProcess {

    private static final Logger LOG = LoggerFactory.getLogger(MacOSProcess.class);

    private static final int ARGMAX = SysctlUtil.sysctl("kern.argmax", 0);

    // 64-bit flag
    private static final int P_LP64 = 0x4;
    /*
     * macOS States:
     */
    private static final int SSLEEP = 1; // sleeping on high priority
    private static final int SWAIT = 2; // sleeping on low priority
    private static final int SRUN = 3; // running
    private static final int SIDL = 4; // intermediate state in process creation
    private static final int SZOMB = 5; // intermediate state in process termination
    private static final int SSTOP = 6; // process being traced

    /*
     * For process info structures
     */
    private static final PathElement PBSD = groupElement("pbsd");
    private static final long COMM_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PBSD, groupElement("pbi_comm"));
    private static final long STATUS_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PBSD, groupElement("pbi_status"));
    private static final long PPID_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PBSD, groupElement("pbi_ppid"));
    private static final long UID_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PBSD, groupElement("pbi_uid"));
    private static final long GID_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PBSD, groupElement("pbi_gid"));
    private static final long START_SEC_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PBSD, groupElement("pbi_start_tvsec"));
    private static final long START_USEC_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PBSD, groupElement("pbi_start_tvusec"));
    private static final long NFILES_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PBSD, groupElement("pbi_nfiles"));
    private static final long FLAGS_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PBSD, groupElement("pbi_flags"));

    private static final PathElement PTINFO = groupElement("ptinfo");
    private static final long TNUM_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PTINFO, groupElement("pti_threadnum"));
    private static final long PRI_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PTINFO, groupElement("pti_priority"));
    private static final long VSZ_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PTINFO, groupElement("pti_virtual_size"));
    private static final long RSS_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PTINFO, groupElement("pti_resident_size"));
    private static final long SYS_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PTINFO, groupElement("pti_total_system"));
    private static final long USR_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PTINFO, groupElement("pti_total_user"));
    private static final long PGIN_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PTINFO, groupElement("pti_pageins"));
    private static final long FLTS_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PTINFO, groupElement("pti_faults"));
    private static final long CSW_OFFSET = PROC_TASK_ALL_INFO.byteOffset(PTINFO, groupElement("pti_csw"));

    private static final long PASSWD_NAME_OFFSET = PASSWD.byteOffset(groupElement("pw_name"));
    private static final long GROUP_NAME_OFFSET = GROUP.byteOffset(groupElement("gr_name"));

    private static final long DISKIO_READ_OFFSET = RUSAGEINFOV2.byteOffset(groupElement("ri_diskio_bytesread"));
    private static final long DISKIO_WRITTEN_OFFSET = RUSAGEINFOV2.byteOffset(groupElement("ri_diskio_byteswritten"));

    private static final long VIP_PATH_OFFSET = VNODE_PATH_INFO.byteOffset(groupElement("pvi_cdir"),
            groupElement("vip_path"));

    private int majorVersion;
    private int minorVersion;

    private Supplier<String> commandLine = memoize(this::queryCommandLine);
    private Supplier<Pair<List<String>, Map<String, String>>> argsEnviron = memoize(this::queryArgsAndEnvironment);

    private String name = "";
    private String path = "";
    private String currentWorkingDirectory;
    private String user;
    private String userID;
    private String group;
    private String groupID;
    private State state = INVALID;
    private int parentProcessID;
    private int threadCount;
    private int priority;
    private long virtualSize;
    private long residentSetSize;
    private long kernelTime;
    private long userTime;
    private long startTime;
    private long upTime;
    private long bytesRead;
    private long bytesWritten;
    private long openFiles;
    private int bitness;
    private long minorFaults;
    private long majorFaults;
    private long contextSwitches;

    public MacOSProcess(int pid, int major, int minor) {
        super(pid);
        this.majorVersion = major;
        this.minorVersion = minor;
        updateAttributes();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getCommandLine() {
        return this.commandLine.get();
    }

    private String queryCommandLine() {
        return String.join(" ", getArguments()).trim();
    }

    @Override
    public List<String> getArguments() {
        return argsEnviron.get().a();
    }

    @Override
    public Map<String, String> getEnvironmentVariables() {
        return argsEnviron.get().b();
    }

    private Pair<List<String>, Map<String, String>> queryArgsAndEnvironment() {
        int pid = getProcessID();
        // Set up return objects
        List<String> args = new ArrayList<>();
        // API does not specify any particular order of entries, but it is reasonable to
        // maintain whatever order the OS provided to the end user
        Map<String, String> env = new LinkedHashMap<>();

        // Get command line via sysctl CTL_KERN, KERN_PROCARGS2
        MemorySegment m = SysctlUtil.sysctl(new int[] { 1, 49, pid }, ARGMAX);
        if (m != null) {
            // Procargs contains an int representing total # of args, followed by a
            // null-terminated execpath string and then the arguments, each
            // null-terminated (possible multiple consecutive nulls),
            // The execpath string is also the first arg.
            // Following this is an int representing total # of env, followed by
            // null-terminated envs in similar format
            int nargs = m.get(ValueLayout.JAVA_INT, 0);
            // Sanity check
            if (nargs > 0 && nargs <= 1024) {
                // Skip first int (containing value of nargs)
                long offset = SystemLibrary.INT_SIZE;
                // Skip exec_command and null terminator, as it's duplicated in first arg
                String cmdLine = m.getUtf8String(offset);
                offset += cmdLine.getBytes(StandardCharsets.UTF_8).length + 1;
                // Build each arg and add to list
                while (offset < ARGMAX) {
                    // Grab a string. This should go until the null terminator
                    String arg = m.getUtf8String(offset);
                    if (nargs-- > 0) {
                        // If we havent found nargs yet, it's an arg
                        args.add(arg);
                    } else {
                        // otherwise it's an env
                        int idx = arg.indexOf('=');
                        if (idx > 0) {
                            env.put(arg.substring(0, idx), arg.substring(idx + 1));
                        }
                    }
                    // Advance offset to next null
                    offset += arg.getBytes(StandardCharsets.UTF_8).length + 1;
                }
            } else {
                // Don't warn for pid 0
                if (pid > 0) {
                    LOG.warn(
                            "Failed sysctl call for process arguments (kern.procargs2), process {} may not exist. Error code: {}",
                            pid, errno());
                }
            }
        }
        return new Pair<>(Collections.unmodifiableList(args), Collections.unmodifiableMap(env));
    }

    @Override
    public String getCurrentWorkingDirectory() {
        return this.currentWorkingDirectory;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public String getUserID() {
        return this.userID;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public String getGroupID() {
        return this.groupID;
    }

    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public int getParentProcessID() {
        return this.parentProcessID;
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    @Override
    public List<OSThread> getThreadDetails() {
        long now = System.currentTimeMillis();
        List<OSThread> details = new ArrayList<>();
        /*-
        List<ThreadStats> stats = ThreadInfo.queryTaskThreads(getProcessID());
        for (ThreadStats stat : stats) {
            // For long running threads the start time calculation can overestimate
            long start = now - stat.getUpTime();
            if (start < this.getStartTime()) {
                start = this.getStartTime();
            }
            details.add(new MacOSThread(getProcessID(), stat.getThreadId(), stat.getState(), stat.getSystemTime(),
                    stat.getUserTime(), start, now - start, stat.getPriority()));
        }
        */
        return details;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public long getVirtualSize() {
        return this.virtualSize;
    }

    @Override
    public long getResidentSetSize() {
        return this.residentSetSize;
    }

    @Override
    public long getKernelTime() {
        return this.kernelTime;
    }

    @Override
    public long getUserTime() {
        return this.userTime;
    }

    @Override
    public long getUpTime() {
        return this.upTime;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public long getBytesRead() {
        return this.bytesRead;
    }

    @Override
    public long getBytesWritten() {
        return this.bytesWritten;
    }

    @Override
    public long getOpenFiles() {
        return this.openFiles;
    }

    @Override
    public int getBitness() {
        return this.bitness;
    }

    @Override
    public long getAffinityMask() {
        // macOS doesn't do affinity. Return a bitmask of the current processors.
        int logicalProcessorCount = SysctlUtil.sysctl("hw.logicalcpu", 1);
        return logicalProcessorCount < 64 ? (1L << logicalProcessorCount) - 1 : -1L;
    }

    @Override
    public long getMinorFaults() {
        return this.minorFaults;
    }

    @Override
    public long getMajorFaults() {
        return this.majorFaults;
    }

    @Override
    public long getContextSwitches() {
        return this.contextSwitches;
    }

    @Override
    public boolean updateAttributes() {
        long now = System.currentTimeMillis();
        SegmentAllocator allocator = SegmentAllocator.implicitAllocator();
        int size = (int) PROC_TASK_ALL_INFO.byteSize();
        MemorySegment m = allocator.allocate(size);
        if (0 > proc_pidinfo(getProcessID(), PROC_PIDTASKALLINFO, 0, m, size)) {
            this.state = INVALID;
            return false;
        }
        // Check threadcount first: 0 is invalid
        this.threadCount = m.get(JAVA_INT, TNUM_OFFSET);
        if (0 == this.threadCount) {
            this.state = INVALID;
            return false;
        }

        MemorySegment buf = allocator.allocate(PROC_PIDPATHINFO_MAXSIZE);
        if (0 < proc_pidpath(getProcessID(), buf, PROC_PIDPATHINFO_MAXSIZE)) {
            this.path = buf.getUtf8String(0);
            // Overwrite name with last part of path
            String[] pathSplit = this.path.split("/");
            if (pathSplit.length > 0) {
                this.name = pathSplit[pathSplit.length - 1];
            }
        }
        if (this.name.isEmpty()) {
            // pbi_comm contains first 16 characters of name
            this.name = m.asSlice(COMM_OFFSET, MAXCOMLEN).getUtf8String(0);
        }

        switch (m.get(JAVA_INT, STATUS_OFFSET)) {
        case SSLEEP:
            this.state = SLEEPING;
            break;
        case SWAIT:
            this.state = WAITING;
            break;
        case SRUN:
            this.state = RUNNING;
            break;
        case SIDL:
            this.state = NEW;
            break;
        case SZOMB:
            this.state = ZOMBIE;
            break;
        case SSTOP:
            this.state = STOPPED;
            break;
        default:
            this.state = OTHER;
            break;
        }
        this.parentProcessID = m.get(JAVA_INT, PPID_OFFSET);

        int uid = m.get(JAVA_INT, UID_OFFSET);
        this.userID = Integer.toString(uid);
        MemoryAddress pwuid = getpwuid(uid);
        if (!pwuid.equals(NULL)) {
            this.user = pwuid.get(ADDRESS, PASSWD_NAME_OFFSET).getUtf8String(0);
        }
        int gid = m.get(JAVA_INT, GID_OFFSET);
        this.groupID = Integer.toString(gid);
        MemoryAddress grgid = getgrgid(gid);
        if (!grgid.equals(NULL)) {
            this.group = grgid.get(ADDRESS, GROUP_NAME_OFFSET).getUtf8String(0);
        }

        this.priority = m.get(JAVA_INT, PRI_OFFSET);
        this.virtualSize = m.get(JAVA_LONG, VSZ_OFFSET);
        this.residentSetSize = m.get(JAVA_LONG, RSS_OFFSET);
        this.kernelTime = m.get(JAVA_LONG, SYS_OFFSET) / 1_000_000L;
        this.userTime = m.get(JAVA_LONG, USR_OFFSET) / 1_000_000L;
        this.startTime = m.get(JAVA_LONG, START_SEC_OFFSET) * 1000L + m.get(JAVA_LONG, START_USEC_OFFSET) / 1000L;
        this.upTime = now - this.startTime;
        this.openFiles = m.get(JAVA_INT, NFILES_OFFSET);
        this.bitness = (m.get(JAVA_INT, FLAGS_OFFSET) & P_LP64) == 0 ? 32 : 64;
        this.majorFaults = m.get(JAVA_INT, PGIN_OFFSET);
        // testing using getrusage confirms pti_faults includes both major and minor
        this.minorFaults = m.get(JAVA_INT, FLTS_OFFSET) - this.majorFaults;
        this.contextSwitches = m.get(JAVA_INT, CSW_OFFSET);

        if (this.majorVersion > 10 || this.minorVersion >= 9) {
            MemorySegment rUsageInfoV2 = allocator.allocate(RUSAGEINFOV2.byteSize());
            if (0 == proc_pid_rusage(getProcessID(), RUSAGE_INFO_V2, rUsageInfoV2)) {
                this.bytesRead = rUsageInfoV2.get(JAVA_LONG, DISKIO_READ_OFFSET);
                this.bytesWritten = rUsageInfoV2.get(JAVA_LONG, DISKIO_WRITTEN_OFFSET);
            }
        }

        size = (int) VNODE_PATH_INFO.byteSize();
        MemorySegment vpi = allocator.allocate(size);
        if (0 < proc_pidinfo(getProcessID(), PROC_PIDVNODEPATHINFO, 0, vpi, size)) {
            this.currentWorkingDirectory = vpi.asSlice(VIP_PATH_OFFSET, MAXPATHLEN).getUtf8String(0);
        }
        return true;
    }
}
