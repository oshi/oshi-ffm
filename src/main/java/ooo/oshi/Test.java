/*
 * Copyright 2022 Daniel Widdis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ooo.oshi;

import ooo.oshi.software.os.OperatingSystem;

public class Test {

    public static void main(String[] args) {
        System.out.println("The operating system is: " + SystemInfo.getCurrentPlatform().getName());
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        System.out.println("The current Process ID is: " + os.getProcessId());
    }

}
