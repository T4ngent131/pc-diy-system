@REM ----------------------------------------------------------------------------
@REM Maven Wrapper - Windows Batch Script
@REM ----------------------------------------------------------------------------
@REM Licensed under the Apache License, Version 2.0
@REM ----------------------------------------------------------------------------

@if "%DEBUG%"=="" @echo off
@REM Set local scope
setlocal enabledelayedexpansion

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..
set MAVEN_HOME=%APP_HOME%\.mvn\wrapper\maven-wrapper.jar

@REM Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome
set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute
goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe
if exist "%JAVA_EXE%" goto execute
goto fail

:execute
"%JAVA_EXE%" -jar "%MAVEN_HOME%" %*

:fail
endlocal
echo ERROR: Maven wrapper failed.
exit /b 1
