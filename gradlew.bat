@echo off
setlocal
set "GRADLE_VERSION=8.9"
set "GRADLE_HOME=%USERPROFILE%\.gradle\manual-wrapper\gradle-%GRADLE_VERSION%"
set "GRADLE_BIN=%GRADLE_HOME%\bin\gradle.bat"
if not exist "%GRADLE_BIN%" (
  echo Download Gradle %GRADLE_VERSION% from https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip and extract it to %GRADLE_HOME%
  exit /b 1
)
call "%GRADLE_BIN%" %*
