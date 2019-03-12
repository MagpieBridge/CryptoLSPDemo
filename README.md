# CryptoLSPDemo[![Build Status](https://travis-ci.com/MagpieBridge/CryptoLSPDemo.svg?branch=master)](https://travis-ci.com/MagpieBridge/CryptoLSPDemo)
**You can skip step 1 and 2 to get the jars from the mvn branch https://github.com/MagpieBridge/CryptoLSPDemo/tree/mvn/repository**
1. check out "websockets" branch from lsp4j https://github.com/MagpieBridge/lsp4j.git 
- install with: 
	-- ``gradlew build -x test``
	-- ``gradlew install -Dmaven.repo.local="path\to\your\local\m2\repository"``
2. install CryptoAnalysis.
- check out "master" branch from WPDPS https://github.com/MagpieBridge/WPDS.git (used by CryptoAnalysis)
- install with  ``mvn install -DskipTests`` 
- check out "master" branch from CryptoAnalysis https://github.com/MagpieBridge/CryptoAnalysis.git
- install with  ``mvn install -DskipTests`` 
3. check out "master" branch from MagpieBridge https://github.com/MagpieBridge/MagpieBridge.git
- install with ``mvn install -DskipTests`` 
4. check out "master" branch from CryptoLSPDemo https://github.com/MagpieBridge/CryptoLSPDemo.git
- install with ``mvn install -DskipTests`` 
5. Configure ``crypto-lsp-demo-0.0.1-SNAPSHOT.jar`` als language server in IDEs/editors (Eclipse, IntellIJ)

## Run Eclipse demo
- install lsp4e - http://download.eclipse.org/lsp4e/releases/latest/ 
- create new launch configuratio called crytoLSP: 

<img src="https://github.com/MagpieBridge/CryptoLSPDemo/blob/master/doc/eclipseconfig0.PNG" width="500">

- set up in Main ->Location ``PATH\TO\Java8JDK\bin\java.exe``
- set up in Main ->Arguments for java: 
``-Duser.project=PATH\TO\crypto-lsp-demo -jar PATH\TO\crypto-lsp-demo\target\crypto-lsp-demo-0.0.1-SNAPSHOT.jar``

<img src="https://github.com/MagpieBridge/CryptoLSPDemo/blob/master/doc/eclipseconfig.PNG" width="500">

- open Eclipse->Window->Preferences->Language Servers

<img src="https://github.com/MagpieBridge/CryptoLSPDemo/blob/master/doc/setupLS0.PNG" width="500">

- click add -> Text -> Java Source File -> Program -> crytoLSP ->OK

<img src="https://github.com/MagpieBridge/CryptoLSPDemo/blob/master/doc/setupLS1.PNG" width="500">


**Insecure crypto warning in Eclipse**
<img src="https://github.com/MagpieBridge/CryptoLSPDemo/blob/master/doc/EclipseDemo.png" width="800">

## Run monaco demo
- install the JavaScript package manager npm, ``cd monaco-example`` and ``npm install`` to build the example  
- build crypto server war file with ``mvn war:war``
- copy ``crypto-lsp-demo-0.0.1-SNAPSHOT.war`` to ``tomcat\webapps`` and change the name to ``crypto-lsp-demo.war`` 
- configure JVM option for tomcat: for windows, add this line ``set JAVA_OPTS="-Duser.project=PATH\TO\crypto-lsp-demo"`` to ``tomcat\bin\catalina.bat``.
for linux, add ``JAVA_OPTS="-Duser.project=PATH\TO\crypto-lsp-demo"`` to ``tomcat\bin\catalina.sh``(tested for tomcat-9.0.13)
- start ``tomcat\bin\startup.bat`` or ``tomcat\bin\startup.sh``
- copy ``monaco-example`` to ``minweb\htdocs`` of [minweb](https://sourceforge.net/projects/miniweb/) and start minweb.exe
- open http://localhost:8000/monaco-example/lib/ in browser

**Insecure crypto warning in Monaco web editor**
<img src="https://github.com/MagpieBridge/CryptoLSPDemo/blob/master/doc/MonacoDemo.png" width="800">

## Run Sublime demo
- install ``Package Control`` in ``Command Palette`` (windows)
- select and intall package LSP in Package Control (windows)
- paste the following configuration into ``Preferences/Package Settings/LSP/Setting`` 
	
```
{
  "log_debug": true,

  "clients": {
    "cognicrypt": {
    "command": ["C:\\PROGRA~1\\Java\\JDK18~1.0_1\\bin\\java", "-Duser.project=PATH\\TO\\crypto-lsp-demo", "-jar", "PATH\\TO\\crypto-lsp-demo\\target\\crypto-lsp-demo-0.0.1-SNAPSHOT.jar"], 
    "enabled": true,
    "languageId": "java",
    "scopes": ["source.java"],
    "syntaxes": ["Packages/Java/Java.sublime-syntax"], 
    }
  }
}
	
```

- select ``Setup Language Server`` in ``Command Palette`` and enable ``Globally``
- restart Sublime and open an example 

**Insecure crypto warning in Sublime Text**
<img src="https://github.com/MagpieBridge/CryptoLSPDemo/blob/master/doc/SublimeDemo.png" width="800">

## Run IntelliJ demo
- install Plugin "LSP Support":(Settings > Plugins > Search for "LSP Support" 
- add Server definition: Settings > Language & Frameworks > Language Server Protocol > Server Definitions
	- Raw command -> Extension: java -> Command: java -Duser.project=PATH\TO\crypto-lsp-demo -jar PATH\TO\crypto-lsp-demo\target\crypto-lsp-demo-0.0.1-SNAPSHOT.jar
	

<img src="https://github.com/MagpieBridge/CryptoLSPDemo/blob/master/doc/IntelliJConfig.PNG" width="800">

- add LSP server to inspections: (Analyze > Inspect Code > Inspection profile ... > LSP )

- click OK, crypto warnings will be shown in the View "Inspection Results".


