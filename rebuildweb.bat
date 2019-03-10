@echo off 
set sootRepo=E:\Git\Github\magpie\soot-reloaded
set magpieRepo=E:\Git\Github\magpie\MagpieBridge
set cryptoRepo=E:\Git\Github\magpie\crypto-lsp-demo
set tomacatBin=D:\apache-tomcat-9.0.13\bin
set tomacatWebapps=D:\apache-tomcat-9.0.13\webapps

rem echo build soot-reloaded
rem set "cmd1=mvn -f %sootRepo% install"
rem call %cmd1%

echo build magpieBridge
set "cmd2=mvn -f %magpieRepo% install"
call %cmd2%


echo build cryptoLSPdemo
rem set "cmd31=mvn -f %cryptoRepo% install"
rem call %cmd31%

set "cmd3=mvn -f %cryptoRepo% war:war"
call %cmd3%

set cryptowarPath=%cryptoRepo%\target\crypto-lsp-demo-0.0.1-SNAPSHOT.war
set goalPath=%tomacatWebapps%\crypto-lsp-demo.war

echo copy snapshot crytoLSPdemo.war to tomcat
cp %cryptowarPath% %goalPath%

echo start tomcat
set "uptomcat=%tomacatBin%\startup.bat"
call %uptomcat%

