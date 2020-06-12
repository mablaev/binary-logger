This app could be used for generating(writing into the file) events with predefined structure(hard code now) and further reading them and printing in console output. 
To use the program appropriately you should:
1) Have pre-installed java ver >= 8 on your local machine;To make sure you have it just run, from command shell in any OS:
sh> java -version
And in the output you should see something like this:

openjdk version "11.0.7" 2020-04-14...

if you don't see it, please visit https://java.com/en/download/help/download_options.xml
Once you install it, check java -version again.

2) Unpack the archive with the source code
sh>jar xf binary-logger.zip

3) Go to into extracted folder binary-logger
sh> cd binary-logger

4) for Linux systems run 2 scripts:     
sh>build.sh     
sh>run.sh <ARGS>

for Windows:
cmd>build.bat
cmd>run.bat <ARGS>

where ARGS are: 
f=<some path or file name>
fsz=<max file size>
evts=<number of events>
cl=<concurrency level>
wr=<true/false, omit means false>
rd=<true/false, omit means false>
