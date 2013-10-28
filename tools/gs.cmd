REM call with pdf file name without extension
REM PDF file must be in ..\pdf directory
REM GhostScript 9.10 must be installed on system
call "c:\Program Files\gs\gs9.10\bin\gswin64.exe" -q -dNOPAUSE -dBATCH -sDEVICE=pdfwrite -sOutputFile=%~dp0..\temp\%1.unencrypted.pdf -c .setpdfwrite -f %~dp0..\pdf\%1.pdf
