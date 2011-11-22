import java.util.*;

public class ExtendedAttributes {

	byte[] attributeBlock = null;
	
	// inode Extended Attributes
	ExtendedAttributesHeader header = null;
	List<ExtendedAttributeNameEntry> nameEntries = new LinkedList<ExtendedAttributeNameEntry>();
	
	
	public ExtendedAttributes(byte[] attributeBlock) {
		this.attributeBlock = attributeBlock;
		this.parseExtendedAttributes();
	}
	
	/**
	 * Print out the attributes that this inode has
	 *
	 */
	public void printAttributes() {
		Utility.printDivider();
		System.out.println("Attributes for Inode");
		Utility.printDivider();
		System.out.println();
		this.header.printHeader();
		for(ExtendedAttributeNameEntry entry : this.nameEntries) {
			entry.printEntry();
		}
		Utility.printDivider();
	}
	
	/**
	 * Parse the attribute block
	 *
	 */
	private void parseExtendedAttributes() {
		// parse the ext attributes header
		byte[] headerBytes = new byte[32];
		for(int i = 0; i < headerBytes.length; i++) {
			headerBytes[i] = this.attributeBlock[i];
		}
		this.header = new ExtendedAttributesHeader(headerBytes);
		
		// parse the name entries
		int offset = 32; // the byte right after the header
		int nameLen = this.attributeBlock[offset];
		while(nameLen != 0 && offset < this.attributeBlock.length) {
			int length = 16 + nameLen;
			// read in the bytes
			byte[] nameEntryBytes = new byte[length];
			for(int i = 0; i < nameEntryBytes.length; i++) {
				nameEntryBytes[i] = this.attributeBlock[i + offset];
			}
			// create new name entry object and add it to the list of name entries
			this.nameEntries.add(new ExtendedAttributeNameEntry(nameEntryBytes));
			// set the new offset to be the current offset plus the length of the name entry
			offset += length;
			// get the next name length
			nameLen = this.attributeBlock[offset];
		}
	}
}
