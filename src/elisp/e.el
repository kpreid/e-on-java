; By Sebastián González (sgm@acm.org)
; and Boriss Mejías (bmc@info.ucl.ac.be)
; based on similar scripts from
; the Mozart Project (http://www.mozart-oz.org).
; and the current e-mode.el of the E project.
;
; This file is mainly base on the Mozart .el files
;
; ---------------------------------------------

(defvar e-emulator-buffer "*E Emulator*"
  "Name of the E emulator buffer.")

(defvar e-engine-program "rune"
  "Name of the E emulator executable.")

(defvar e-old-frame-title
  (cdr (assoc 'name (frame-parameters (car (visible-frame-list))))))

(defvar e-frame-title
  (concat "E Programming Interface (" e-old-frame-title ")"))

(defvar e-emulator-buffer-size 35
  "Percentage of screen to use for the E Emulator window.")

(defun e-feed-buffer ()
  "Feed the current buffer to the E Compiler."
  (interactive)
  (if (and file (buffer-modified-p)
           (y-or-n-p (format "Save buffer %s first? " (buffer-name))))
      (save-buffer))
  (e-feed-region (point-min) (point-max)))

(defun e-feed-region (start end)
  "Feed the current region to the E Compiler."
  (interactive "r")
  (e-send-string (e-get-region start end)))

(defun e-feed-line (arg)
  "Feed the current line to the E Compiler.
With ARG, feed that many lines.  If ARG is negative, feed that many
preceding lines as well as the current line."
  (interactive "p")
  (let ((region (e-line-region arg)))
    (e-feed-region (car region) (cdr region))))

(defun e-send-string (string &optional system)
  "Feed STRING to the E Compiler, restarting E if it died.
If SYSTEM is non-nil, it is a command for the system and is to be
compiled using a default set of switches."
  (interactive "String to feed: \nP")
  (e-start-if-not-running)
  (let ((proc (get-buffer-process e-emulator-buffer)))
    (comint-send-string proc (encode-coding-string string
                                                   buffer-file-coding-system))
    (comint-send-string proc "\n")))

(defun run-e ()
  "Run E as a sub-process.
Handle input and output via the E Emulator buffer."
  (interactive)
  (save-excursion
    (e-start-if-not-running))
  (or (eq major-mode 'run-e)
      (e-new-buffer))
)

(defun e-new-buffer ()
  "Create a new buffer and edit it in E mode."
  (interactive)
  (switch-to-buffer (generate-new-buffer "E"))
  )

(defun e-is-running ()
  (get-buffer-process e-emulator-buffer))

(defun e-start-if-not-running ()
  (if (not (e-is-running))
      (progn
        (setq e-emulator-buffer "*E Emulator*")
        (e-make-comint e-engine-program) ; no switches passed
        (e-buffer-show (get-buffer e-emulator-buffer))
	(e-set-title e-frame-title)
	(message "E started."))))

(defun e-make-comint (program &rest switches)
  (if (get-buffer e-emulator-buffer)
      (progn
	(delete-windows-on e-emulator-buffer)
	(kill-buffer e-emulator-buffer)))
  (apply 'make-comint "E Emulator" program nil switches))

(defun e-set-title (frame-title)
  "Set the title of the Emacs frame."
  (mapcar (function (lambda (scr)
                      (modify-frame-parameters
                       scr
                       (list (cons 'name frame-title)))))
          (visible-frame-list)))

(defun e-buffer-show (buffer)
  (if (and buffer (not (get-buffer-window buffer)))
      (let ((win (or (get-buffer-window e-emulator-buffer)
		     (split-window (get-largest-window)
				   (/ (* (window-height (get-largest-window))
					 (- 100 e-emulator-buffer-size))
				      100)))))
	(set-window-buffer win buffer)
	(set-buffer buffer)
	(set-window-point win (point-max))
	(bury-buffer buffer))))

(defun e-line-region (arg)
  ;; Return starting and ending positions of ARG lines surrounding point.
  ;; Positions are returned as a pair ( START . END ).
  (save-excursion
    (let (start end)
      (cond ((> arg 0)
	     (beginning-of-line)
	     (setq start (point))
	     (forward-line (1- arg))
	     (end-of-line)
	     (setq end (point)))
	    ((= arg 0)
	     (setq start (point))
	     (setq end (point)))
	    ((< arg 0)
	     (end-of-line)
	     (setq end (point))
	     (forward-line arg)
	     (setq start (point))))
      (cons start end))))

(defun e-get-region (start end)
  ;; Return the region from START to END from the current buffer as a string.
  (save-excursion
    (goto-char start)
    (skip-chars-forward " \t\n")
    (if (/= (count-lines start (point)) 0)
	(progn
	  (beginning-of-line)
	  (setq start (point))))
    (goto-char end)
    (skip-chars-backward " \t\n")
    (setq end (point)))
    (buffer-substring start end))

(provide 'e)
