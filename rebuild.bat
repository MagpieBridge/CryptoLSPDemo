@echo off 
set sootRepo=E:\Git\Github\magpie\soot-reloaded
set magpieRepo=E:\Git\Github\magpie\MagpieBridge
set cryptoRepo=E:\Git\Github\magpie\crypto-lsp-demo
set tomacatBin=D:\apache-tomcat-9.0.13\bin
set tomacatWebapps=D:\apache-tomcat-9.0.13\webapps

rem echo build soot-reloaded
rem set "cmd1=mvn -f %sootRepo% install -DskipTests"
rem call %cmd1%

echo build magpieBridge
set "cmdf=mvn -f %magpieRepo% com.coveo:fmt-maven-plugin:format"
call %cmdf%
set "cmd2=mvn -f %magpieRepo% install -DskipTests"
call %cmd2%


echo build cryptoLSPdemo
set "cmdf2=mvn -f %cryptoRepo% com.coveo:fmt-maven-plugin:format"
call %cmdf2%
set "cmd3=mvn -f %cryptoRepo% install -DskipTests"
call %cmd3%

