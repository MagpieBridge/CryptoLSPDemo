# How to run CogniCryptLSP in Vim?
1. Make sure you have installed [Vim](https://www.vim.org/download.php). Tested version for this tutorial is Vim 8.0.

2. Download `CogniCryptLSP-0.0.1.jar` and `config.zip` from https://github.com/MagpieBridge/CryptoLSPDemo/releases, unzip `config.zip`.

3. Make sure you have intalled `vim-plug` for Vim following the instructions from https://github.com/junegunn/vim-plug. Install `async.vim` and `vim-lsp` with the command `PlugInstall`.


4. Edit Vim setting file `.vimrc` by adding the following lines. 
Change the paths `PATH_TO_JAVA_HOME/bin/java`,  `PATH_TO/CogniCryptLSP-0.0.1.jar`, `PATH_TO/config` to your local paths. 

The following lines (1) define a vim plugin section with two plugins `async.vim` and `vim-lsp`. (2)launch the CogniCryptLSP with Java 1.8, so make sure you have Java 1.8 installed.

```
call plug#begin('~/.vim/plugged')

Plug 'prabirshrestha/async.vim'
Plug 'prabirshrestha/vim-lsp'

call plug#end()


au User lsp_setup call lsp#register_server({
        \ 'name': 'CogniCryptLSP',
        \ 'cmd': {server_info->['PATH_TO_JAVA_HOME/bin/java', '-jar', 'PATH_TO/CogniCryptLSP-0.0.1.jar', '-c', 'PATH_TO/config']},
        \ 'whitelist': ['java'],
        \ })
	
let g:lsp_log_verbose = 1
let g:lsp_log_file = expand('~/vim-lsp.log') 
```

An example for `.vimrc` file can be downloaded from [here](https://github.com/MagpieBridge/CryptoLSPDemo/blob/master/doc/.vimrc)

5. Open a Java File with Vim will trigger CogniCryptLSP to run. You can use [RSA.java](https://github.com/MagpieBridge/CryptoLSPDemo/blob/master/doc/RSA.java) to test.

6. In Vim call ``:LspDocumentDiagnostics`` to display all the crypto errors. More LSP commands for Vim can be found here https://github.com/prabirshrestha/vim-lsp
<img src="vimDemo.png" width="800">


7. To rerun CogniCryptLSP in a changed Java File, simply save the file.



Questions? Ask [Linghui Luo](https://github.com/linghuiluo)
