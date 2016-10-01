# kindle-collection-generator
Copy files to a Kindle and automatically create Collections

My old kindle reader can read files (PDF, text etc). Connect your Kindle your PC and just copy the files over in Windows Explorer.

The problem is when there are large numbers of files. The interface is clunky and it is a nightmare to navigate.
What to do?

I wrote this program to automatically create Kindle Collections. A collection is just a list of files, grouped together under a name ("My latest docs", "Some notes", "Shopping Lists" etc). In my case, each collection is generated from the name of the folder, and contains...well, all the files in that folder.

The clever bit is trying to work out how to encode the collection. The kindle uses a JSON file listing all of the collections. For each collection, there is a list of the files. From looking on the internet, this uses a hash of the filename - with a few prefixes. Look in the code for more details.

The work to calculate the hashes and generate the collections file is written in a single Java file because most of the work is building the collections in memory first. Java also has the SHA-1 hash algorithm in one of the libraries.
There are a few shell scripts to copy the files over to the kindle then launch the Java program, but that is just wrapping.

