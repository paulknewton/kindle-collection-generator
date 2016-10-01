package org.topolino.kindle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * Represents a Kindle collection of books. Used to generate a collections.json
 * file based on an input list of files.
 * 
 * Note: uses the build-collection.sh to generate the file list.
 * 
 * Run with '-d' to see debugging output.
 * 
 * @author Paul
 * 
 */
public class KindleCollection {
	// constants to detect e-book filenames and extract the 'asin' identifier
	private static final String EBOOK_PATTERN = ".*\\.azw";	// suffix of e-book filenames
	private static final String ASIN_PREFIX = "-asin_"; // used to denote the ASIN identifier in an e-book filename
	private static final int ASIN_LEN = 10;	// length of an ASIN in an e-book filename
	private static final String ASIN_KEY_SUFFIX = "^EBOK";	// appended to ASINs in a collection

	static public String PREFIX = "/mnt/us/documents/"; // Kindle requires this
														// prefixed to the
														// filename

	private static final String ROOT_COLLECTION = "Bookshelf";	// name to use for the root collection
	
	static boolean debug = false;

	private MessageDigest md;

	// the collection of the form <collectionName: book1, book2, book3...>
	private Map<String, List<String>> collections;

	KindleCollection() throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("SHA-1");

		collections = new HashMap<String, List<String>>();
	}

	public static void main(String[] args) throws IOException,
			NoSuchAlgorithmException {

		if (args.length == 1 && "-d".equals(args[0])) {
			debug = true;
		}

		KindleCollection kColl = new KindleCollection();

		BufferedReader rdr = new BufferedReader(
				new InputStreamReader(System.in));
		kColl.readCollection(rdr);

		// print out our collection
		if (debug)
			System.out.println(kColl.prettyPrint());
		System.out.println(kColl.printAsJson());
	}

	/**
	 * Reads files from 'rdr' and populates a Kindle collection.
	 * 
	 * @param rdr
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public void readCollection(BufferedReader rdr)
			throws UnsupportedEncodingException, IOException {
		// store the collections
		String filename = null;

		// read each line from stdin and hash
		while ((filename = rdr.readLine()) != null) {
			filename = PREFIX + filename;

			// extract collection name from the last dir name (this might be
			// 'documents' if in the root)
			String path = filename.substring(0, filename.lastIndexOf('/'));
			String dirname = path.substring(path.lastIndexOf('/') + 1);
			
			// rename 'root' collection from 'documents' to 'Bookshelf'
			if ("documents".equals(dirname)) {
				dirname = ROOT_COLLECTION;
			}

			// add to an existing collection, or create a new one
			List<String> collection = collections.get(dirname);
			if (collection == null) {
				// System.out.println("Creating a new collection: " + dirname);

				collection = new ArrayList<String>();
				collections.put(dirname, collection);
			}
			collection.add(hashIt(filename)); // can I still update this
												// list?

			if (debug)
				System.out.println(filename + "\t" + path + "\t" + dirname
						+ "\t" + hashIt(filename));
		}
	}

	/**
	 * Creates SHA-1 hash code for a filename. Note: the filename must include
	 * /mnt/us/documents
	 * 
	 * @param filename
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String hashIt(String filename) throws UnsupportedEncodingException {

		// the hashcode depends on the type of file
		if (filename.matches(EBOOK_PATTERN)) {
			return hashEbook(filename);
		} else {
			return hashWithSha1(filename);
		}
	}

	/**
	 * Builds the hashcode for e-books of the form '#asin^EBOK'
	 * 
	 * @param filename
	 * @return
	 */
	private String hashEbook(String filename) {
		int i = filename.indexOf(ASIN_PREFIX);
		//if (debug) System.out.println("Found " + ASIN_PREFIX + " at " + i);
		int start = i + ASIN_PREFIX.length();
		String asin = filename.substring(start, start + ASIN_LEN);
		if (debug) System.out.println("ASIN=" + asin);
		
		return "#" + asin + ASIN_KEY_SUFFIX;
		
	}

	/**
	 * Builds the hashcode for non e-books of the form 'sha1(iso-8859-1(filename))'
	 * 
	 * @param filename
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String hashWithSha1(String filename)
			throws UnsupportedEncodingException {
		byte[] bytes = filename.getBytes("ISO-8859-1");	// use this instead of UTF-8 to handle accents
		byte[] messageDigest = md.digest(bytes);

		md.reset(); // don't forget to clear the digest buffer

		// convert to hex string
		BigInteger bi = new BigInteger(1, messageDigest);
		String hash = "*" + String.format("%0" + (messageDigest.length << 1) + "x",
				bi);

		return hash;
	}

	/**
	 * Create a text string in JSON format representing the collection.
	 * 
	 * @return
	 */
	public String printAsJson() {
		StringBuffer sb = new StringBuffer();

		sb.append("{");

		Set<String> keys = collections.keySet();
		boolean firstList = true;
		for (String listName : keys) {
			if (!firstList) {
				sb.append(",");
			}
			sb.append(printSingleCollectionAsJson(listName,
					collections.get(listName)));
			firstList = false;
		}

		sb.append("}");

		return sb.toString();

	}

	private String printSingleCollectionAsJson(String listName,
			List<String> books) {

		// used in the 'lastAccess' field
		long currentTime = new Date().getTime();

		// build indentation
		// StringBuffer padBuffer = new StringBuffer();
		// for (int pad = 0; pad < indent; pad++) {
		// padBuffer.append(" ");
		// }
		// String padding = padBuffer.toString();

		// start building the JSON string
		StringBuffer itemBuffer = new StringBuffer();

		// collection name...
		itemBuffer.append("\"" + listName + "@en-US\":{\"items\":[");

		boolean firstBook = true;
		for (String entry : books) {
			// book name...
			itemBuffer.append((firstBook ? "" : ",") + "\"" + entry + "\"");
			firstBook = false;
		}

		// trailing fields...
		itemBuffer.append("],\"lastAccess\":" + currentTime + "}");

		return itemBuffer.toString();

	}

	/**
	 * Creates a nicely formatted text string of the collection (not in JSON
	 * format).
	 * 
	 * @return
	 */
	public String prettyPrint() {
		StringBuffer sb = new StringBuffer();

		Set<String> keys = collections.keySet();
		for (String key : keys) {
			List<String> books = collections.get(key);
			sb.append("Collection: " + key + "\n");

			for (String book : books) {
				sb.append("Book: " + book + "\n");
			}
		}

		return sb.toString();
	}

}
