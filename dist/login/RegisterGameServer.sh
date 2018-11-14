#!/bin/sh
java -Dlogback.configurationFile=./config/logback.xml -cp ./../libs/*:l2jlogin.jar com.l2jserver.tools.gsregistering.GameServerRegister -c