# Groups files into collections and generates a new collections.json file.
# Collection names are derived from the last directory in the full pathname of each file.
#
# Run with '-d' to see full output
#

(cd documents; find -type f \( -name "*.pdf" -o -name "*.txt" -o -name "*.azw" \)) | sed 's/\.\///g' | java -jar mykindle.jar "$@"

#(cd documents; find -type f \( -name "*.azw" \)) | sed 's/\.\///g' | java -jar mykindle.jar "$@"


 