@echo off
setlocal

echo ========================================================
echo       Starting HariMart on Apache Tomcat 9
echo ========================================================

REM Set JAVA_HOME if not already set
if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot" (
    set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
)

if not exist "C:\tools\apache-tomcat-9.0.122\bin\startup.bat" (
    echo [ERROR] Tomcat startup script not found at C:\tools\apache-tomcat-9.0.122\bin\startup.bat!
    exit /b 1
)

echo Starting Tomcat...
call "C:\tools\apache-tomcat-9.0.122\bin\startup.bat"

echo.
echo HariMart is launching at:
echo http://localhost:8080/HariMart/
echo.
