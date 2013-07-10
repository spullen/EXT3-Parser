import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;

public class DirectoryEntry {

	private byte[] entryBytes = null;
	private boolean fileType = false;
	
	private List<String> directories = new LinkedList<String>();
	private HashMap<String, String> dirFileTypes = new HashMap<String, String>();
	
	public DirectoryEntry(byte[] entry, boolean fileType) {
		this.entryBytes = entry;
		this.fileType = fileType;
		this.parseEntry();
	}
	
	/**
	 * Print the directory entry
	 *
	 */
	public void printDirectories() {
		for (String dir : this.directories)
			System.out.println("\t" + dir + "(" + this.dirFileTypes.get(dir) + ")");
	}
	
	/**
	 * Parse the directory entry
	 *
	 */
	private void parseEntry() {
		/*
		 * Algorithm is:
		 * 		offset = 0
		 * 		offset read attributes for entry
		 * 		get the length of the entry
		 * 		read and store the name
		 * 		new offset
		 * 			offset += lengthOfEntry
		 */
		ByteBuffer buf = ByteBuffer.wrap(this.entryBytes);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// figure out which entry version to use based on the fileType
		int offset = 0;
		
		// Inode value (bytes 0-3)
		int nextInodeValue = buf.getInt(0);
		
		// Read the next identified inode value
		while (nextInodeValue != 0 && offset < this.entryBytes.length) {

			// get the ushort entry length (bytes 4 - 5)
			int entryLength = Utility.getUnsignedShort(buf, offset + 4);

			// Originally fileType did not exist and was not added until 0.5 as such, the file type flag indicates if
			// the file type will be available. If not available, the file inode must be read to get the type.
			int nameLength;
			int inodeFileType = 0;

			if (fileType) {
				// get the short name length (bytes 6 - 7)
				nameLength = buf.getShort(offset + 6);
			} else {
				// get the ubyte name length (byte 6)
				nameLength = buf.get(offset + 6) & 0xff;

				// get the ubyte file type ( byte 7)
				inodeFileType = buf.get(offset + 7) & 0xff;
			}

			// create a new byte array to store the name bytes
			byte[] name = new byte[nameLength];
			buf.position(offset + 8);
			buf.get(name);

			final String nameStr = new String(name);
			this.directories.add(nameStr);

			// figure out the directory file type and store in HashMap with key = the name
			this.dirFileTypes.put(nameStr, getTypeField(inodeFileType));

			// update the offset
			offset += entryLength;

			// get the next Inode Value
			if (offset < this.entryBytes.length) {
				nextInodeValue = buf.getInt(offset);
			}
		}
	}
	
	/**
	 * Get the file type string literal for the filetype flag
	 * @param fileType
	 * @return
	 */
	private String getTypeField(int fileType) {
		switch(fileType) {
		case 0:
			return "Unknown Type";
		case 1:
			return "Regular File";
		case 2:
			return "Directory";
		case 3:
			return "Character device";
		case 4:
			return "Block device";
		case 5:
			return "FIFO";
		case 6:
			return "Unix Socket";
		default:
			return "Symbolic Link";
		}
	}
}
