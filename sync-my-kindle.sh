# Script to copy my documents to a sub-folder on the kindle, and automatically assign them to collections.
#
# Only copy PDF and TXT files
#

FILES="--include=*.pdf --include=*.txt"
COLLECTIONS="system/collections.json"
KINDLE="/cygdrive/h"

# Copy any new files. Only include Kindle-readable files, and exclude empty directories.
echo "Sync'ing files from dropbox to kindle..."
#TEST=-n
MIRROR="--delete"
rsync -av $TEST $MIRROR --prune-empty-dirs $FILES --exclude="archive/" --include="*/" --exclude="*" /cygdrive/w/dropbox "$KINDLE/documents"

# Generate 'collections' file
echo "Building collections..."
cd "$KINDLE"
./build-collection.sh > $COLLECTIONS

echo "<Hit RETURN to exit>"
read DUMMY

# eof