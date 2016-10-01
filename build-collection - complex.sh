# Groups files into collections and generates a new collections.json file.
# Collection names are derived from the last directory in the full pathname of each file.
# All files in the root 'documents' folder are placed in a 'my books' collection.
#
# TODO: If 2 collections have the same name the script simply creates 2 collections with the same name.
#		The Kindle will ignore the 2nd and assume these docs are not in any collection.
#

TIME_MS=`date +%s%N | cut -b1-13`

# get started...
echo -n "{"

cd documents

for i in *; do
  if [ ! -d "$i" ]; then continue; fi	# Only browse sub-folders. Skip books in the root folder
  
  # list each file (ignore directories)
  find $i -type f | while read f; do
  
    # derive the name of the collection for this file based on the last directory in the path
    DIR=`dirname "$f"`
	#NEXT_COLL=`echo "$DIR" | awk 'BEGIN {FS="/"} { print $(NF-1) }'`
	NEXT_COLL=`basename "$DIR"`
	#echo $NEXT_COLL
	
	# starting a new collection...
	if [ "$NEXT_COLL" != "$CURRENT_COLL" ]; then
	
	  # end the previous collection if one is currently open
	  if [ "$CURRENT_COLL" != "" ]; then
	    echo -n "],\"lastAccess\":$TIME_MS},"
	  fi

	  # start a new collection
	  #echo Start collection
	  CURRENT_COLL=$NEXT_COLL
	  FIRST_ITEM=true
	  echo -n "\"$CURRENT_COLL@en-US\":"
	  echo -n "{\"items\":["
	fi
	
	# add a file to the collection
	#echo "collection entry [$NEXT_COLL]"$f
	if [ "$FIRST_ITEM" = "false" ]; then echo -n ", "; fi
	  HASH=`echo -n "/mnt/us/documents/$f" | openssl sha1`	# remember to use echo -n (the hash does not include the newline in the filename)
	echo -n "\"*$HASH\""
	FIRST_ITEM=false
  done
  
  # close last collection
  echo -n "],\"lastAccess\":$TIME_MS}"
done

# put everything else in a 'my books' collection
FIRST_ITEM=true
echo -n ",\"*** My Books ***@en-US\":{\"items\":["
for i in *; do
  if [ -d "$i" ]; then continue; fi
  
  if [ "$FIRST_ITEM" = "false" ]; then echo -n ", "; fi
  HASH=`echo -n "/mnt/us/documents/$i" | openssl sha1`	# remember to use echo -n (the hash does not include the newline in the filename)
  echo -n "\"*$HASH\""
  FIRST_ITEM=false
done
echo -n "],\"lastAccess\":$TIME_MS}"
  
# ...and finish
echo -n "}"