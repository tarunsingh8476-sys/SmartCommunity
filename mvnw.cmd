@echo off
setlocal
set "MAVEN_VERSION=3.9.9"
if not defined MAVEN_USER_HOME set "MAVEN_USER_HOME=%USERPROFILE%\.m2"
set "CACHE_DIR=%MAVEN_USER_HOME%\wrapper\dists\apache-maven-%MAVEN_VERSION%"
set "MAVEN_HOME=%CACHE_DIR%\apache-maven-%MAVEN_VERSION%"
set "MAVEN_BIN=%MAVEN_HOME%\bin\mvn.cmd"
if exist "%MAVEN_BIN%" goto runmaven
set "ZIP=%CACHE_DIR%\apache-maven-%MAVEN_VERSION%-bin.zip"
set "URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"
if not exist "%CACHE_DIR%" mkdir "%CACHE_DIR%"
powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri '%URL%' -OutFile '%ZIP%'"
if errorlevel 1 exit /b 1
powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -LiteralPath '%ZIP%' -DestinationPath '%CACHE_DIR%' -Force"
if errorlevel 1 exit /b 1
del "%ZIP%"
:runmaven
call "%MAVEN_BIN%" %*
exit /b %ERRORLEVEL%
