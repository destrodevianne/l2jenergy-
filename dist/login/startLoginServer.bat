@echo off
COLOR 0b
title L2jEnergy Login Console
:start
java -Dfile.encoding=UTF-8 -Xmx256m -Dlogback.configurationFile=./configuration/logback.xml -cp ./../libs/*;l2jlogin.jar com.l2jserver.loginserver.L2LoginServer
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin Restarted Login Server.
echo.
goto start
:error
echo.
echo Login Server terminated abnormally!
echo.
:end
echo.
echo Login Server Terminated.
echo.
pause