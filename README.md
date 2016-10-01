# kindle-collection-generator
Copy files to a Kindle and automatically create Collections

My old kindle reader can read files (PDF, text etc). Connect your Kindle your PC and just copy the files over in Windows Explorer.

The problem is when there are large numbers of files. The interface is clunky and it is a nightmare to navigate.
What to do?

I wrote this program to automatically create Kindle Collections (basically a list of files with a name).
Each collection is generated from the name of the folder, and contains...well, all the files in that folder.

The clever bit is trying to work out how to encode the collection. From looking on the internet, this uses a hash of the contents.
Look in the code for more details.

The work to calculate the hashes and generate the collections file is written in Java.
There are a few shell scripts to copy the files over to the kindle then launch the Java program.

