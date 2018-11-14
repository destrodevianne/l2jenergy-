@echo off
COLOR 0b
title L2jEnergy - Register Game Server
@java -Dlogback.configurationFile=./config/logback.xml -cp ./../libs/*;l2jlogin.jar com.l2jserver.gsregistering.GameServerRegister
@pause