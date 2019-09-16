(custom-set-variables
 ;; custom-set-variables was added by Custom.
 ;; If you edit it by hand, you could mess it up, so be careful.
 ;; Your init file should contain only one such instance.
 ;; If there is more than one, they won't work right.
 '(package-selected-packages (quote (lsp-java ## javaimp eglot))))
(custom-set-faces
 ;; custom-set-faces was added by Custom.
 ;; If you edit it by hand, you could mess it up, so be careful.
 ;; Your init file should contain only one such instance.
 ;; If there is more than one, they won't work right.
 )
;; load emacs 24's package system. Add MELPA repository.
(when (>= emacs-major-version 24)
  (require 'package)
  (add-to-list
   'package-archives
   ;; '("melpa" . "http://stable.melpa.org/packages/") ; many packages won't show if using stable
   '("melpa" . "http://melpa.milkbox.net/packages/")
   t))
(package-initialize)
(package-install 'eglot)
(require 'eglot)
(add-hook 'java-mode-hook 'eglot-ensure) 
(add-to-list 'eglot-server-programs '(java-mode . ("/Library/Java/JavaVirtualMachines/jdk1.8.0_201.jdk/Contents/Home/bin/java" "-jar" "/Users/linghuiluo/Downloads/CogniCryptLSP-0.0.1.jar " "-c" "/Users/linghuiluo/Downloads/config")))

 
