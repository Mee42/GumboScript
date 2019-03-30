::[Bat To Exe Converter]
::
::YAwzoRdxOk+EWAnk
::fBw5plQjdG8=
::YAwzuBVtJxjWCl3EqQJgSA==
::ZR4luwNxJguZRRnk
::Yhs/ulQjdF+5
::cxAkpRVqdFKZSzk=
::cBs/ulQjdF+5
::ZR41oxFsdFKZSDk=
::eBoioBt6dFKZSDk=
::cRo6pxp7LAbNWATEpCI=
::egkzugNsPRvcWATEpCI=
::dAsiuh18IRvcCxnZtBJQ
::cRYluBh/LU+EWAnk
::YxY4rhs+aU+JeA==
::cxY6rQJ7JhzQF1fEqQJQ
::ZQ05rAF9IBncCkqN+0xwdVs0
::ZQ05rAF9IAHYFVzEqQJQ
::eg0/rx1wNQPfEVWB+kM9LVsJDGQ=
::fBEirQZwNQPfEVWB+kM9LVsJDGQ=
::cRolqwZ3JBvQF1fEqQJQ
::dhA7uBVwLU+EWDk=
::YQ03rBFzNR3SWATElA==
::dhAmsQZ3MwfNWATElA==
::ZQ0/vhVqMQ3MEVWAtB9wSA==
::Zg8zqx1/OA3MEVWAtB9wSA==
::dhA7pRFwIByZRRnk
::Zh4grVQjdCyDJGyX8VAjFDhVXheNMleeA6YX/Ofr0+2Sr08Sa+sxa5va1riLMq4W8kCE
::YB416Ek+ZG8=
::
::
::978f952a14a936cc963da21a135fa983
@echo off

mkdir "%APPDATA%\Gumboscript"
mkdir "%APPDATA%\Gumboscript\backups"
curl -o "%APPDATA%\Gumboscript\gumbo.jar" "http://72.66.54.109:8000/quick/GumboScript/build/libs/GumboScript-1.0-SNAPSHOT-all.jar"
curl -o "%APPDATA%\Gumboscript\gumbo_path.bat" "http://72.66.54.109:8000/quick/GumboScript/windows/gumbo_path.bat"
curl -o "%APPDATA%\Gumboscript\gumbo.bat" "http://72.66.54.109:8000/quick/GumboScript/windows/gumbo.bat"
%APPDATA%\Gumboscript\gumbo_path.bat
