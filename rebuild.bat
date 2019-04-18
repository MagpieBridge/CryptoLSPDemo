@echo off 
set sootRepo=E:\Git\Github\magpie\soot-reloaded
set magpieRepo=E:\Git\Github\magpie\MagpieBridge
set cryptoRepo=E:\Git\Github\magpie\crypto-lsp-demo
set tomacatBin=D:\apache-tomcat-9.0.13\bin
set tomacatWebapps=D:\apache-tomcat-9.0.13\webapps

echo build soot-reloaded
set "cmdf=mvn -f %sootRepo% com.coveo:fmt-maven-plugin:format"
call %cmdf%
set "cmd1=mvn -f %sootRepo% install -DskipTests"
call %cmd1%

set "cmdf=mvn -f %magpieRepo% com.coveo:fmt-maven-plugin:format"
call %cmdf%
echo build magpieBridge
set "cmd2=mvn -f %magpieRepo% install -DskipTests"
call %cmd2%

echo build cryptoLSPdemo
set "cmdf2=mvn -f %cryptoRepo% com.coveo:fmt-maven-plugin:format"
call %cmdf2%
set "cmd3=mvn -f %cryptoRepo% install -DskipTests"
call %cmd3%


set "cmd4=cd vscode"
call %cmd4%
set "cmd4=vsce package"
call %cmd4%
set "cmd4=code --install-extension crypto-lsp-demo-0.0.1.vsix"
call %cmd4%