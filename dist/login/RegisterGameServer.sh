#!/bin/sh
java -Dlogback.configurationFile=./configuration/logback.xml -cp ./../libs/*:l2jlogin.jar com.l2jserver.loginserver.gsregistering.GameServerRegister