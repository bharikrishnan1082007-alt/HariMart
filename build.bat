@echo off
setlocal

echo ========================================================
echo       Building HariMart Multi-Vendor Application
echo ========================================================

REM Set JAVA_HOME if not already set or pointing to Java 17
if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot" (
    set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
)

REM Use local tools path for Maven if available
set "MVN_CMD=mvn"
if exist "C:\tools\apache-maven-3.9.6\bin\mvn.cmd" (
    set "MVN_CMD=C:\tools\apache-maven-3.9.6\bin\mvn.cmd"
)

echo [1/3] Compiling and packaging WAR with Maven...
call "%MVN_CMD%" clean package
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Maven build failed!
    exit /b %ERRORLEVEL%
)

echo [2/3] Verifying generated WAR package...
if not exist "target\HariMart.war" (
    echo [ERROR] target\HariMart.war was not found!
    exit /b 1
)

echo [3/3] Deploying to Apache Tomcat webapps...
if exist "C:\tools\apache-tomcat-9.0.122\webapps" (
    copy /Y "target\HariMart.war" "C:\tools\apache-tomcat-9.0.122\webapps\HariMart.war"
    echo Successfully deployed to C:\tools\apache-tomcat-9.0.122\webapps\HariMart.war
) else (
    echo Note: Apache Tomcat webapps directory not detected at C:\tools\apache-tomcat-9.0.122\webapps.
    echo WAR package is ready at target\HariMart.war
)

echo ========================================================
echo       HariMart Build and Deployment Complete!
echo ========================================================
