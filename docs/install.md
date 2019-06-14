---
layout: page
title: Installation
permalink: /install/
---

Gumboscript is run on the JVM (Java Virtual Machine),
so it will run everywhere if you set it up properly.
These instructions are about how to set up a Gumboscript environment via WSL


[WSL](https://docs.microsoft.com/en-us/windows/wsl/install-win10),
 or `Windows Subsystem for Linux`, is a way to simulate a [Linux kernel](https://en.wikipedia.org/wiki/Linux_kernel)
under a Windows one. This is similar to 
[Virtual Machines](https://en.wikipedia.org/wiki/Virtual_machine) but
without many of the performance issues.

*If you're interesting in [Linux](https://en.wikipedia.org/wiki/Linux),
feel free to shoot me a dm for more information.*

Linux has many different distributions, called *distros*.
You may have heard of some popular ones, ie Redhat, Ubuntu, Debian, Arch, Gentoo, etc.
I personally use [Arch Linux](https://www.archlinux.org/), a distro that excels in
user flexibility, package-management, and a user-centric design.

You can get started with WSL by enabling it on your computer.
Windows has a short guide available [here](https://docs.microsoft.com/en-us/windows/wsl/install-win10).
To run powershell as admin, click the `Windows Button`, search `"Powershell"`, Right click and select `Run as administrator`

Once you have that enabled, restart your computer.

Install the Arch Linux package from the window's store.
Launch it like you would launch any other program. 
A terminal window should pop up

### Configuring Linux

When you first start the terminal, you will be logged in as `root`.
Think of `root` like the admin user,
except with Linux there is actually no permission limits.

It's a good practice to not use the `root` user, as anything launched
from the `root` user also has `root` privileges.
To de-escalate into a *normal user*, use these commands, 
replacing `{USERNAME}` with the username of the account you want to add

*protip - lines starting with `#` mean it's in a shell with `root` permission.
lines starting with `$` mean it's running as a normal user.*
```
# useradd -m {USERNAME}
# echo "{USERNAME} ALL=(ALL) ALL" >> /etc/sudoers
```
This adds the user, and gives them full-root permission,
which they can access with `sudo`.

Normally, editing `/etc/sudoers` is a very carefully executed action.
However, because this is a new install, it's very easy to delete and
restart if you screw up this step.

#### Entering your user account
```
# su - {USERNAME}
```
Easy as that. Your shell should now have a `$` prefix
```
$
```
If you run `whoami`, it should print your username
```
$ whoami
{USERNAME}
```

#### Simple linux commands

If you look to the left of the `$`, you should see a *directory name*.
By default this is `~`, which is at `/home/{USERNAME}`.

This is your *current directory*. 
All this means is that you can access files by there relative location.

A file called `A.txt` in `~` can be referred to by the name `A.txt` when the `~` directory.
However, from `/home/` you would need to refer to it like `./{USERNAME}/A.txt`

You can change directory with the `cd` command.
Each directory has two special files:
- `.` is the same directory
- `..` is the parent directory

You can use this to navigate backwards: `cd ..`.
You can always return to the home directory with `cd`.

You can list all the files with `ls`.
Files that start with `.` are hidden from `ls`, so use `ls -a` to show them as well.
You can print a file with cat, ie `cat A.txt`
You can edit a file with nano, ie `nano A.txt`

#### Setting up dev environment

To install things on Arch, use the `pacman` tool.

Before doing anything, you should run a full update. 
Do this with
```
$ sudo pacman -Syu
```
You also need to install some tools
```
$ sudo pacman -S nano git
```
*This may be already installed*

#### Installing `pacaur`

The Arch repo contains a lot of neat programs. 
However, it takes a lot of support to get there.

`Gumboscript` in in the [AUR](https://wiki.archlinux.org/index.php/Arch_User_Repository),
which has no auditing.

You can install programs manually from the aur, but it's a pain to do updates.
There are helpers, which take care of this process for you!

I use `pacaur`. It's really easy to install:
```
$ cd
$ git clone https://aur.archlinux.org/pacaur.git
$ cd pacaur
$ makepkg -si
```
If `makepkg` complains about `auracle-git`, install it as well, and then try pacaur again
```
$ cd
$ git clone https://aur.archlinux.org/auracle-git.git
$ cd auracle-git
$ makepkg -si
```
If this works, try executing `pacaur`.
If you don't get a `pacaur not found` error, you can go ahead and delete
the install directory
```
$ cd
$ rm -rf pacaur
$ rm -rf auracle-git
```
Now, you should update `pacaur` and `auracle-git`, so they will get
new updates. Do this with
```
$ pacaur -S pacaur auracle-git
```
*Notice how you don't need `sudo`*

If you get dropped into vim (a sort of text editor),
quit by typing `:q`.

#### Installing gumboscript

```
$ pacaur -S gumboscript
```

#### Updating `gumboscript`

```
$ pacaur -S gumboscript
```

#### Removing `gumboscript`

```
$ pacaur -Rs gumboscript
```