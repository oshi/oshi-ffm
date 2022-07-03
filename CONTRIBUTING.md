# Contributing to OSHI-FFM

Welcome!  This is a community maintained project and we need YOU to join our community!

## First Things First

Respect the license of any code you submit. You may only submit code that is compatible with the Apache 2.0 license. If you have copied any code from any other source, ensure you comply with the restrictions of its license, and do not impose any additional restrictions on this project.

## Code Format

This section will eventually contain a lot more detail, but for now:
 - There's an Eclipse formatting convention file in the config directory
 - If you don't have eclipse or an IDE that can import its configs, don't worry, just run `./mvnw spotless:apply` before committing

## This is a new project with an old history.

The intent of this project is to eventually contain all the same features of the OSHI project, but using core Java features only, without JNA. The OSHI project has plenty of examples to get you started, but:
 - Do not feel constrained to use the same API.  If the community desires to change the API, that can be done.
 - Do not feel constrained to use the same implementations.  If you know a better way to do things, do it!

## Developer Certificate of Origin

OSHI-FFM is an open source product released under the Apache 2.0 license (see either [the Apache site](https://www.apache.org/licenses/LICENSE-2.0) or the [LICENSE file](./LICENSE)). The Apache 2.0 license allows you to freely use, modify, distribute, and sell your own products that include Apache 2.0 licensed software.

We respect intellectual property rights of others and we want to make sure all incoming contributions are correctly attributed and licensed. A Developer Certificate of Origin (DCO) is a lightweight mechanism to do that.

The DCO is a declaration attached to every contribution made by every developer. In the commit message of the contribution, the developer simply adds a `Signed-off-by` statement and thereby agrees to the DCO, which you can find below or at [DeveloperCertificate.org](http://developercertificate.org/).

Each commit must include a DCO which looks like this

```
Signed-off-by: Charles Babbage <difference.engine@email.com>
```
You may type this line on your own when writing your commit messages. However, if your user.name and user.email are set in your git configs, you can use `-s` or `--signoff` to add the `Signed-off-by` line to the end of the commit message.

Please use your real name. The git history is documentation of your copyright in the work. Anonymous contributions are not permitted.

Please use an email address uniquely identifying you.  A [GitHub noreply address](https://docs.github.com/en/account-and-profile/setting-up-and-managing-your-personal-account-on-github/managing-email-preferences/setting-your-commit-email-address) associated with your username is acceptable but discouraged.

```
Developer's Certificate of Origin 1.1
By making a contribution to this project, I certify that:
(a) The contribution was created in whole or in part by me and I
    have the right to submit it under the open source license
    indicated in the file; or
(b) The contribution is based upon previous work that, to the
    best of my knowledge, is covered under an appropriate open
    source license and I have the right under that license to
    submit that work with modifications, whether created in whole
    or in part by me, under the same open source license (unless
    I am permitted to submit under a different license), as
    Indicated in the file; or
(c) The contribution was provided directly to me by some other
    person who certified (a), (b) or (c) and I have not modified
    it.
(d) I understand and agree that this project and the contribution
    are public and that a record of the contribution (including
    all personal information I submit with it, including my
    sign-off) is maintained indefinitely and may be redistributed
    consistent with this project or the open source license(s)
    involved.
 ```
