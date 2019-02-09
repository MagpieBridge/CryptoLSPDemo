# CryptoLSPDemo

- check out "master" branch from MagpieBridge https://github.com/MagpieBridge/MagpieBridge.git
- check out "master" branch from CryptoLSPDemo https://github.com/MagpieBridge/CryptoLSPDemo.git
- install them with ``mvn install -DskipTests`` 
- Configure ``crypto-lsp-demo-0.0.1-SNAPSHOT.jar`` als language server in IDEs/editors. 


Run monaco demo
- install the JavaScript package manager npm, ``cd monaco-example`` and ``npm install`` to build the example  
- build crypto server war file with ``mvn war:war``
- copy ``crypto-lsp-demo-0.0.1-SNAPSHOT.war`` to ``tomcat\webapps`` and change the name to ``crypto-lsp-demo.war`` 
- start ``tomcat\bin\startup.bat`` or ``tomcat\bin\startup.sh``
- copy ``monaco-example`` to ``minweb\htdocs`` of [minweb](https://sourceforge.net/projects/miniweb/) and start minweb.exe
- open http://localhost:8000/monaco-example/lib/ in browser

Run Sublime demo
- Install ``Package Control`` in ``Command Palette`` (windows)
- Select and intall package LSP in Package Control (windows)
- Paste the following configuration into ``Preferences/Package Settings/LSP/Setting`` 
	
~~~~
{
  "log_debug": true,

  "clients": {
    "cognicrypt": {
    "command": ["C:\\PROGRA~1\\Java\\JDK18~1.0_1\\bin\\java", "-jar", "E:\\Git\\Github\\crypto-lsp-demo\\target\\crypto-lsp-demo-0.0.1-SNAPSHOT.jar"], 
    "enabled": true,
    "languageId": "java",
    "scopes": ["source.java"],
    "syntaxes": ["Packages/Java/Java.sublime-syntax"], 
    }
  }
}
	
~~~~

- Select ``Setup Language Server`` in ``Command Palette`` and enable ``Globally``
- Restart Sublime and open an example 



