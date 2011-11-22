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
		for(String dir : this.directories) {
			System.out.print("\t" + dir);
			if(!this.fileType) {
				System.out.println("(" + this.dirFileTypes.get(dir) + ")");
			}
			else {
				System.out.println();
			}
		}
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
		if(fileType) {
			/*
			 * Layout:
			 * 			bytes 0 - 3: Inode Value
			 * 			bytes 4 - 5: Length of this entry
			 * 			bytes 6 - 7: Name length
			 * 			bytes 8+   : Name
			 */
			int nextInodeValue = buf.getInt(0);
			while(nextInodeValue != 0) {
				// get the entry length
				int entryLength = buf.getShort(offset + 4);
				
				// get the name length
				int nameLength = buf.getShort(offset + 6);
				
				// create a new byte array to store the name bytes
				byte[] name = new byte[nameLength];
				for(int i = 0; i < name.length; i++) {
					name[i] = buf.get(i + offset + 8);
				}
				this.directories.add(new String(name));
				
				// update the offset
				offset += entryLength;
				
				// get the next Inode Value
				nextInodeValue = buf.getInt(offset);
			}
			
		}
		else {
			/*
			 * Layout:
			 * 			bytes 0 - 3: Inode Value
			 * 			bytes 4 - 5: Length of ths entry
			 * 			bytes 6 - 6: Name Length
			 * 			bytes 7 - 7: File Type
			 * 			bytes 8+   : Name
			 */
			int nextInodeValue = buf.getInt(0);
			while(nextInodeValue != 0 && offset < this.entryBytes.length) {
				// get the entry length
				int entryLength = buf.getShort(offset + 4);
				
				// get the name length
				int nameLength = buf.get(offset + 6);
				
				// get the file type
				int fileType = buf.get(offset + 7);
				
				// create a new byte array to store the name bytes
				byte[] name = new byte[nameLength];
				for(int i = 0; i < name.length; i++) {
					name[i] = buf.get(i + offset + 8);
				}
				String nameStr = new String(name);
				this.directories.add(nameStr);
				
				// figure out the directory file type and store in HashMap with key = the name
				String ft = this.getTypeField(fileType);
				this.dirFileTypes.put(nameStr, ft);
				
				// update the offset
				offset += entryLength;
				
				// get the next Inode Value
				if(offset < this.entryBytes.length) {
					nextInodeValue = buf.getInt(offset);
				}
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
