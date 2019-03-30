@echo off

mkdir "%APPDATA%\Gumboscript"
mkdir "%APPDATA%\Gumboscript\backups"
curl -o "%APPDATA%\Gumboscript\gumbo.jar" "http://72.66.54.109:8000/quick/GumboScript/build/libs/GumboScript-1.0-SNAPSHOT-all.jar"
curl -o "%APPDATA%\Gumboscript\gumbo_path.bat" "http://72.66.54.109:8000/quick/GumboScript/windows/gumbo_path.bat"
curl -o "%APPDATA%\Gumboscript\gumbo.bat" "http://72.66.54.109:8000/quick/GumboScript/windows/gumbo.bat"
%APPDATA%\Gumboscript\gumbo_path.bat
