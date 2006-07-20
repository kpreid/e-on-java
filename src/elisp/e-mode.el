;; A minimal Emacs major-mode for E, www.erights.org.
;; by Will Glozer <wglozer@yahoo.com>
;; Hacked by Darius Bacon <darius@accesscom.com>
;; (It'd be nice if someone who really knew elisp would take over!)
;; With further mods by MarkM

;; This file is hereby placed into the public domain.
;; No warranty expressed or implied.

;; To use, put this file in your load-path and add
;; (require 'e-mode)
;; to your .emacs.  This will cause Emacs to automatically use 
;; E mode when editing .e, .e-awt, e-swt, .e-headless, .emaker, .caplet,
;; or .updoc files.
;; XXX what do we need to change to properly handle updoc files?

;; We're sleazily stealing C mode functions for now...
(require 'c-mode)
(require 'comint)


(defvar e-command "e"
  "*Shell command used to start E interpreter.")
(defvar e-args '()
  "*Arguments to pass to the E interpreter.")

(setq e-mode-map nil)

(defvar e-mode-map ()
  "Keymap used in `e-mode' buffers.")

(if e-mode-map
    nil
  (setq e-mode-map (make-sparse-keymap))
  ;; indentation level modifiers
  (define-key e-mode-map "\C-c\C-c"  'e-execute-buffer)
  (define-key e-mode-map "\C-c\C-r"  'e-execute-region)
;  (define-key e-mode-map "{"         'c-electric-brace)
;  (define-key e-mode-map "}"         'c-electric-brace)
;  (define-key e-mode-map "\C-m"      'newline-and-indent)
  )

(defvar e-font-lock-keywords
  (let ((keywords
         (mapconcat 
          'identity
          ;; The following list was extracted from e.y of the E distro:
          '(
            ;; Keywords 
            "bind" "break" "catch" "continue" "def"
            "else" "escape" "extends"
            "finally" "for" "guards" "if" "implements" "in" "interface"
            "match" "meta" "method" "pragma" "return" "switch"
            "thunk" "to" "try" "var" "when" "while" "_"

            ;; Pseudo-reserved (Reserved, but used in reserved productions) 
            "accum" "delegate" "module" "on" "select" "throws"

            ;; Reserved Keywords 
            "abstract" "an" "as" "assert" "attribute"
            "be" "begin" "behalf" "belief" "believe" "believes"
            "case" "class" "const" "constructor"
            "declare" "default" "define" "defmacro" "deprecated" "dispatch" "do"
            "encapsulate" "encapsulated" "encapsulates"
            "end" "ensure" "enum" "eventual" "eventually"
            "export" "facet" "forall" "function" "given"
            "hidden" "hides" "inline" "is"
            "know" "knows" "lambda" "let" "methods"
            "namespace" "native"
            "obeys" "octet" "oneway"
            "package" "private" "protected" "public"
            "raises" "reliance" "reliant" "relies" "rely" "reveal"
            "sake" "signed" "static" "struct"
            "suchthat" "supports" "suspect" "suspects" "synchronized"
            "this" "transient" "truncatable" "typedef"
            "unsigned" "unum" "uses" "using" "utf8" "utf16"
            "virtual" "volatile" "wstring"
            )
          "\\|")))
    (list
     ;; keywords
     (cons (concat "\\b\\(" keywords "\\)\\b[ \n\t(]") 1)
     ;; classes
     '("\\bclass[ \t]+\\([a-zA-Z_]+[a-zA-Z0-9_]*\\)"
       1 font-lock-type-face)
     ;; functions
     '("\\bdef[ \t]+\\([a-zA-Z_]+[a-zA-Z0-9_]*\\)"
       1 font-lock-function-name-face)
     ))
  "Additional expressions to highlight in e mode.")
(put 'e-mode 'font-lock-defaults '(e-font-lock-keywords))

(defvar e-mode-syntax-table nil
  "Syntax table used in `e-mode' buffers.")

(if e-mode-syntax-table
    nil
  (setq e-mode-syntax-table (make-syntax-table))
  (modify-syntax-entry ?\` "\"" e-mode-syntax-table)
  
  ;; what a hack job... gnu emacs 20 is incapable of supporting E's 3
  ;; comment styles

  ;; use me if you want # and /* */ comments
  (modify-syntax-entry ?\# "<" e-mode-syntax-table)
  (modify-syntax-entry ?\n ">" e-mode-syntax-table)
  (modify-syntax-entry ?/ ". 14b" e-mode-syntax-table)
  (modify-syntax-entry ?* ". 23b" e-mode-syntax-table)

  ;; use me if you want // and /* */ comments
  ;(modify-syntax-entry ?\n "> b" e-mode-syntax-table))
  ;(modify-syntax-entry ?/ ". 124b" e-mode-syntax-table)
  ;(modify-syntax-entry ?* ". 23" e-mode-syntax-table)
  )

(defun e-mode ()
  "Major mode for editing E files."
  (interactive)
  ;; set up local variables
  (kill-all-local-variables)
  (make-local-variable 'font-lock-defaults)
  (make-local-variable 'comment-start)
  (make-local-variable 'comment-end)
  (make-local-variable 'comment-start-skip)
  (make-local-variable 'comment-column)
  (make-local-variable 'comment-indent-function)
  (make-local-variable 'indent-region-function)
  (make-local-variable 'indent-line-function)
  
  (make-local-variable 'c-indent-level)
  (setq c-indent-level 4)
  
  (set-syntax-table e-mode-syntax-table)
  (setq major-mode              'e-mode
        mode-name               "E"
        ;;**local-abbrev-table      e-mode-abbrev-table
        font-lock-defaults      '(e-font-lock-keywords)
        comment-start           "# "
        comment-end             ""
        comment-start-skip      "# *"
        comment-column          40
        ;;comment-indent-function 'e-comment-indent-function
        indent-region-function  'c-indent-region
        indent-line-function    'c-indent-line
        ;;indent-line-function    'indent-to-left-margin

        ;; tell add-log.el how to find the current function/method/variable
        ;;add-log-current-defun-function 'e-current-defun
        )
  (use-local-map e-mode-map)
  (c-set-offset 'statement-cont '0)
  )

(defvar e-output-buffer "*E*")

(defun e-execute-region (start end &optional foo)
  "Execute the region in an E interpreter."
  (interactive "r\nP")
  (or (< start end)
      (error "Region is empty"))
  (let ((e-buffer (get-buffer-create e-output-buffer)))
    (if (get-process "E")
        nil
      (comint-exec e-buffer "E" e-command nil e-args))
    
    (save-excursion
      (process-send-region e-buffer start end)
      (pop-to-buffer e-buffer)
      (comint-mode)
      (setq comint-prompt-regexp ".* $"))))

(defun e-execute-buffer (&optional foo)
  "Send the contents of the buffer to an E interpreter.
   If there is a *E* process buffer it is used."
  (interactive "P")
  (e-execute-region (point-min) (point-max)))


(setq auto-mode-alist
      (append '(("\\.e$" . e-mode)
                ("\\.e-awt$" . e-mode)
                ("\\.e-swt$" . e-mode)
                ("\\.e-headless$" . e-mode)
                ("\\.emaker$" . e-mode)
                ("\\.caplet$" . e-mode)
                ("\\.updoc$" . e-mode))
              auto-mode-alist))

(provide 'e-mode)
