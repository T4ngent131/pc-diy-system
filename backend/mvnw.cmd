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
set "APP_HOME=%DIRNAME:~0,-1%"
set "MAVEN_HOME=%APP_HOME%\.mvn\wrapper\maven-wrapper.jar"
set "MAVEN_PROPERTIES=%APP_HOME%\.mvn\wrapper\maven-wrapper.properties"

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
if exist "%MAVEN_HOME%" goto runWrapper
for /f "tokens=1,* delims==" %%A in ('findstr /b wrapperUrl "%MAVEN_PROPERTIES%"') do set WRAPPER_URL=%%B
powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing '%WRAPPER_URL%' -OutFile '%MAVEN_HOME%'"
if not exist "%MAVEN_HOME%" goto fail

:runWrapper
"%JAVA_EXE%" "-Dmaven.multiModuleProjectDirectory=%APP_HOME%" -classpath "%MAVEN_HOME%" org.apache.maven.wrapper.MavenWrapperMain %*
if %ERRORLEVEL% equ 0 goto end
goto fail

:fail
endlocal
echo ERROR: Maven wrapper failed.
exit /b 1

:end
endlocal
exit /b 0
