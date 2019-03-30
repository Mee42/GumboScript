---
layout: page
title: Installation
permalink: /install/
---

Gumboscript needs to be installed before it can be used.
Luckily, this isn't a hard process.

## Step 1: Downloading the project

Run this command in command prompt:
```
curl http://72.66.54.109:8000/quick/GumboScript/install.bat | cmd
```
This will download `install.bat` and run it. If you don't trust me,
the contents of this file are on [github](https://github.com/mee42/Gumboscript).

It's also right here:
```
{% include install.bat %}
```

Running it will do a couple things

- Create directory `%APPDATA%/Gumboscript`

- Download the jar file to `%APPDATA%/Gumboscript/gumbo.jar`

- Download a batch file to `%APPDATA%/Gumboscript/gumbo.bat`

- Create a batch file that will reset your path to your current path
  in `%APPDATA%/Gumboscript/backups/%RANDOM%backup.bat`

- Append `;%APPDATA%/Gumboscript` to your PATH variable


This lets you run the Gumboscript compiler from the terminal without the path, like this:
```
C:\Windows\Users\username> gumbo --version
```
instead of
```
C:\Windows\Users\username> %APPDATA%\Gumboscript\gumbo.bat --version
```

### How to uninstall

*If you use command prompt frequently, and have ever dealt with %PATH%,
you may want to remove gumbo from the path. Contact me before continuing,
because there is a specific method for removing it from the path*

Delete the directory at `Gumboscript` in the `%APPDATA%` folder.
You can reach the Appdata folder by typing `%APPDATA%` in the Windows menu.

### How to update

Just run `gumbo --update`.

### Basic command prompt tutorial

When you are using Gumboscript, you will need to use command prompt.
Open this by pressing the `Windows Key` and typing `cmd`.

Your current directory is shown on the left side of the terminal

```
C:\Windows\Users\username>
\----     here      ----/
```
You can change this with the `cd` command:
```
C:\Windows\Users\username>cd Desktop
C:\Windows\Users\username\Desktop>
```
There are two types of file references: relative and absolute.
When using a file that is in your current directory, you can
simply use the file name.
```
C:\Windows\Users\username\Desktop>notepad file.txt
```
In this case, we open `file.txt` with notepad.

This also works through directories. Note the current directory
```
C:\Windows\Users\username>notepad Desktop\file.txt
```
This works the exact same

Absolute directories are all based around the `C` drive. They must start with `C:/` and it will be the same regardless of current directory
```
C:\Windows\Users\other_user>notepad C:\Windows\Users\username\Desktop\file.txt
```
This is used a lot less then relative.

To list all files in the directory, use `dir`.

To use the Gumbo compiler, just use the word `gumbo`. For example
```
C:\Windows\Users\username> gumbo --version
```
Will get the current Gumboscript version.
