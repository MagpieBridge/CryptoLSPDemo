# CryptoLSPDemo

- check out "master" branch from MagpieBridge https://github.com/MagpieBridge/MagpieBridge.git
- check out "master" branch from CryptoLSPDemo https://github.com/MagpieBridge/CryptoLSPDemo.git
- install them with ``mvn install -DskipTests`` 
- Use crypto-lsp-demo-0.0.1-SNAPSHOT.jar als language server in editors. 


Run monaco demo
- install the JavaScript package manager npm, ``cd monaco-example`` and ``npm install`` to build the example  
- Build crypto server war file with ``mvn war:war``
- copy ``crypto-lsp-demo-0.0.1-SNAPSHOT.war`` to ``tomcat\webapps`` and change the name to ``crypto-lsp-demo.war`` 
- start ``tomcat\bin\startup.bat or tomcat\bin\startup.sh``
- copy ``monaco-example`` to ``minweb\htdocs`` of [minweb](https://sourceforge.net/projects/miniweb/) and start minweb.exe
- open http://localhost:8000/monaco-example/lib/ in browser


