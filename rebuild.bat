@echo off 
set sootRepo=E:\Git\Github\magpie\soot-reloaded
set magpieRepo=E:\Git\Github\magpie\MagpieBridge
set cryptoRepo=E:\Git\Github\magpie\crypto-lsp-demo
set tomacatBin=D:\apache-tomcat-9.0.13\bin
set tomacatWebapps=D:\apache-tomcat-9.0.13\webapps

rem echo build soot-reloaded
rem set "cmdf=mvn -f %sootRepo% com.coveo:fmt-maven-plugin:format"
rem  call %cmdf%
rem set "cmd1=mvn -f %sootRepo% install -DskipTests"
rem call %cmd1%

rem set "cmdf=mvn -f %magpieRepo% com.coveo:fmt-maven-plugin:format"
rem call %cmdf%
rem echo build magpieBridge
rem set "cmd2=mvn -f %magpieRepo% install -DskipTests"
rem call %cmd2%

echo build cryptoLSPdemo
set "cmdf2=mvn -f %cryptoRepo% com.coveo:fmt-maven-plugin:format"
call %cmdf2%
set "cmd3=mvn -f %cryptoRepo% install -DskipTests"
call %cmd3%


set "cmd4=cd vscode"
call %cmd4%
set "cmd4=vsce package"
call %cmd4%
set "cmd4=code --install-extension CogniCryptLSP-demo-0.0.1.vsix"
call %cmd4%