; By Sebastián González (sgm@acm.org)
; and Boriss Mejías (bmc@info.ucl.ac.be)
; based on similar scripts from
; the Mozart Project (http://www.mozart-oz.org).
; and the current e-mode.el of the E project.
;
; The functionality to feed lines, regions and
; buffers to the E interpreter is based on the
; Mozart .el files
;
; The color syntax and e files' extensions are
; taken from the file e-mode.el
;
; ---------------------------------------------

(require 'comint)

;-----------------------------------
; Code based on e-mode.el <start>
; To identify extensions of e-files!
;-----------------------------------
(setq auto-mode-alist
      (append '(("\\.e$" . run-e)
                ("\\.e-awt$" . run-e)
                ("\\.e-swt$" . run-e)
                ("\\.e-headless$" . run-e)
                ("\\.emaker$" . run-e)
                ("\\.caplet$" . run-e)
                ("\\.updoc$" . run-e))
              auto-mode-alist))
;-----------------------------------
; Code based on e-mode.el <end>
;-----------------------------------

(autoload 'e-feed-buffer "e"
  "Feed the current buffer to the E Compiler." t)
(autoload 'e-feed-region "e"
  "Feed the current region to the E Compiler." t)
(autoload 'e-feed-line "e"
  "Feed the current line to the E Compiler." t)

(defun run-e ()
  "Major mode for editing E files."
  (interactive)
  ;-----------------------------------
  ; Code taken from e-mode.el <start>
  ; This is all for color support!
  ;-----------------------------------
  (kill-all-local-variables)
  (make-local-variable 'font-lock-defaults)
  (make-local-variable 'comment-start)
  (make-local-variable 'comment-end)
  (make-local-variable 'comment-start-skip)
  (make-local-variable 'comment-column)
  (make-local-variable 'indent-region-function)
  (make-local-variable 'indent-line-function)

  (make-local-variable 'c-indent-level)
  (setq c-indent-level 4)

  (set-syntax-table run-e-syntax-table)

  (setq major-mode              'run-e
        mode-name               "E"
        font-lock-defaults      '(e-font-lock-keywords)
        comment-start           "# "
        comment-end             ""
        comment-start-skip      "# *"
        comment-column          40
        indent-region-function  'c-indent-region
        indent-line-function    'c-indent-line
        )

  (c-set-offset 'statement-cont '0)
  ;-----------------------------------
  ; Code taken from e-mode.el <end>
  ;-----------------------------------

  ;; install this keymap when working in E mode
  (let ((map (make-sparse-keymap)))
    (define-key map '[(control ?.) (control ?l)]
      'e-feed-line)
    (define-key map '[(control ?.) (control ?b)]
      'e-feed-buffer)
    (define-key map '[(control ?.) (control ?r)]
      'e-feed-region)
    (use-local-map map))
)

;-----------------------------------
; Code taken from e-mode.el <start>
; This is all for color support!
;-----------------------------------
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
            "accum" "delegate" "fn" "module" "on" "select" "throws"

            ;; Reserved Keywords 
            "abstract" "an" "as" "assert" "attribute"
            "be" "begin" "behalf" "belief" "believe" "believes"
            "case" "class" "const" "constructor"
            "declare" "default" "define" "defmacro" "deprecated" "dispatch" "do"
            "encapsulate" "encapsulated" "encapsulates"
            "end" "ensure" "enum" "eventual" "eventually"
            "export" "facet" "forall" "fun" "function" "given"
            "hidden" "hides" "inline"
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
(put 'run-e 'font-lock-defaults '(e-font-lock-keywords))

(defvar run-e-syntax-table nil
  "Syntax table used in `run-e' buffers.")

(if run-e-syntax-table
    nil
  (setq run-e-syntax-table (make-syntax-table))
  (modify-syntax-entry ?\` "\"" run-e-syntax-table)
  
  ;; what a hack job... gnu emacs 20 is incapable of supporting E's 3
  ;; comment styles

  ;; use me if you want # and /* */ comments
  (modify-syntax-entry ?\# "<" run-e-syntax-table)
  (modify-syntax-entry ?\n ">" run-e-syntax-table)
  (modify-syntax-entry ?/ ". 14b" run-e-syntax-table)
  (modify-syntax-entry ?* ". 23b" run-e-syntax-table)
  )

;-----------------------------------
; Code taken from e-mode.el <end>
;-----------------------------------

