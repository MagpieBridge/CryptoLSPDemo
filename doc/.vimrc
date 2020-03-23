call plug#begin('~/.vim/plugged')

Plug 'prabirshrestha/async.vim'
Plug 'prabirshrestha/vim-lsp'

call plug#end()


au User lsp_setup call lsp#register_server({
        \ 'name': 'CogniCryptLSP',
        \ 'cmd': {server_info->['/Library/Java/JavaVirtualMachines/jdk1.8.0_201.jdk/Contents/Home/bin/java', '-jar', '/Users/linghuiluo/Downloads/CogniCryptLSP-0.0.1.jar', '-c', '/Users/linghuiluo/Downloads/config']},
        \ 'whitelist': ['java'],
        \ })
	
let g:lsp_log_verbose = 1
let g:lsp_log_file = expand('~/vim-lsp.log') 

