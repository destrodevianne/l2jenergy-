@echo off
COLOR 0b
title L2jEnergy - Register Game Server

@java -Djava.util.logging.config.file=console.cfg -cp ./../libs/*;l2jlogin.jar com.l2jserver.gsregistering.GameServerRegister
@pause