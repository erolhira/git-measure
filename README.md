# git-measure
git-measure is a tool for analyzing git repositories.<br>
The analysis gives a report of commits, file counts and line of codes per user.<br>

# Requirements
Jre 8+ must be installed.

# Installation
npm install -g git-measure

# Running git-measure
gm --since=18.04.2015 --before=01.01.2019 --dir=d:\ws\gitDir

Default parameters when called only gm:<br>
--since=18.04.2015<br>
--before=sysdate<br>
--dir=current directory<br>
