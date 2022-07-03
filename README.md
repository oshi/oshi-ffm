![OSHI](https://dl.dropboxusercontent.com/s/c82qboyvvudpvdp/oshilogo.png)

[![MIT License](https://img.shields.io/badge/license-Apache_2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

OSHI is a free Operating System and Hardware Information library for Java.

OSHI-FFM intends to leverage the efficiency and performance improvements of [JEP 424](https://openjdk.org/jeps/424)
(Foreign Function & Memory API) to provide the same capabilities as the original [OSHI](https://github.com/oshi/oshi)
project, without the overhead of JNA.

It does not require the installation of any additional native libraries and aims to provide a
cross-platform implementation to retrieve system information, such as OS version, processes,
memory and CPU usage, disks and partitions, devices, sensors, etc.

## Contributors are (more than) welcome!

This project is soliciting community involvement with the release of JDK 19, with JEP 424 as a Preview feature.
The goal is for a 1.0.0 release in about one year, with the release of JDK 21 LTS.

Recreating (and modifying as needed) the OSHI API is a Herculean task that will require a community effort.

Contributors who just want to learn about the new Foreign Function and Memory API are welcome to use this as a practical training ground!  What better way to sharpen your skills than to solve an actual, practical problem instead of repeating a Hello World tutorial!

Long-time OSHI users who want to have a voice in the future of this project are encouraged to contribute more and join the maintainer team.

Ideally this will become a community-maintained project with a team of maintainers, with project direction determined by consensus rather than a benevolent dictator.

## Non-code contributions are (more than) welcome!

The initial commits have brought over a minimum number of features and minimal documentation. There are tons of non-code things you can do to help:
 - Help define and implement a CI workflow
 - Add more documentation
 - Organize and triage issues and "where can I help next" guidance
 - Create cool graphics and branding
 - Solicit contributions from your friends, colleagues, and corporations

## This is a new project with an old history.

The intent of this project is to eventually contain all the same features of the OSHI project, but using core Java features only, without JNA. The OSHI project has plenty of examples to get you started, but:
 - Do not feel constrained to use the same API.  If the community desires to change the API, that can be done.
 - Do not feel constrained to use the same implementations.  If you know a better way to do things, do it!

## FAQ

**Q: Why all this effort? What's wrong with JNA-based OSHI?**

**A:** Nothing is wrong with JNA! It's a capable program that many projects have used to access native functions in a standard Java-based format.  However, [benchmark](https://github.com/zakgof/java-native-benchmark#results) have shown that JEP 424 (Project Panama)-based implementations are about 12 times faster.  An order of magnitude improvement is worth the effort.

**Q: Will this keep the same API as OSHI?**

**A:** OSHI's API has evolved over 12 years and seems reasonably consistent, so the general structure will likely stay the same. There will possibly be changes in how unavailable or unsupported data is handled, however, including custom exceptions, Optional return types, and possibly better leverage of JPMS modules.

To disambiguate the packages, this project will prepend `ooo` to exsiting `oshi` package names (the reverse dns for the oshi.ooo domain).

**Q: What does OOO stand for? Why is that OSHI's domain extension?**

**A:** All the good extensions were taken. :-)  OOO seemed the most neutral, non-localized generic domain.

There is an opportunity to use the triple-O in some sort of branding:
 - Zero additional software required
 - Zero dependencies beyond the JDK
 - Zero restrictions with a permissive license

Perhaps you've got better ideas?
