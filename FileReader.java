import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileReader {

	/**
	 * Reads in b.length of data from the offset of a file
	 * @param b
	 * @param offset
	 * @param filename
	 * @return b byte[]
	 */
	public static byte[] seekAndRead(byte[] b, int offset, String filename) {
		File file = null;
		RandomAccessFile raf = null;
		try {
			file = new File(filename);
			raf = new RandomAccessFile(file, "r");
			raf.seek((long)offset); // set the file pointer to the offset
			raf.readFully(b); // read in the bytes from where the file pointer is
			raf.close();
		}
		catch(IOException ioe) {
			System.out.println("Error: " + ioe.toString());
		}
		return b; // return the bytes read in
	}
}
