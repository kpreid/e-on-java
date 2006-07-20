Subject:
[e-lang] E using Emacs
From: Boriss Mejias <bmc@info.ucl.ac.be>
Date: Tue, 19 Oct 2004 01:29:24 +0200
To: e-lang@mail.eros-os.org
CC: Sebastián González <sgm@info.ucl.ac.be>

Dear E users

Some weeks ago I commented on this list that I use *.el files in my
emacs environment to be able to interact directly with the E
interpreter.

In the beginning, these files were developed by Sebastián González,
based on some scripts of the Mozart system. Then, we mixed them with
an existing script of the current E distribution to add colour
support. So now we have both :)

I'm attaching the two files: custom-e-mode.el and e.el
You'll need to add these two lines to your .emacs file

(add-to-list 'load-path "/wherever/you/place/the/files")
(load "/wherever/you/place/the/files/custom-e-mode")

where /wherever/you/place/the/files is actually wherever you place the
files e.el and custom-e-mode :)

Now, once you open an E file with emacs, let's say:
    emacs foo.e
You'll have colour syntax. Then, you can use

C-. C-l  To feed the current line into the E interpreter
C-. C-r  To feed the selected region
C-. C-b  To feed the buffer

Please, let me know if you find bugs.
That's it...
Boriss


_______________________________________________
e-lang mailing list
e-lang@mail.eros-os.org
http://www.eros-os.org/mailman/listinfo/e-lang

custom-e-mode.el
	
Content-Type:
	text/plain
Content-Encoding:
	8bit

e.el
	
Content-Type:
	text/plain
Content-Encoding:
	8bit

Part 1.4
	
Content-Type:
	text/plain
Content-Encoding:
	7bit

