@echo off
REM gumbo --help
cd %APPDATA%
REM appdate doesn't have the gumbo executable
where gumbo
echo %ERRORLEVEL%
set E=%ERRORLEVEL%
echo %E%
if %E% NEQ 0 echo Running!

if %E% NEQ 0 echo %PATH%
if %E% NEQ 0 echo %PATH% > %APPDATA%\Gumboscript\backups\%RANDOM%.backup.path
if %E% NEQ 0 setx PATH "%PATH%;%APPDATA%\Gumboscript"
