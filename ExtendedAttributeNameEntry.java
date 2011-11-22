import java.nio.*;

public class ExtendedAttributeNameEntry {

	int lenOfName;
	int attrType;
	int offsetToValue;
	int blockLocationOfValue;
	int sizeOfValue;
	int hashOfValue;
	String name;
	
	public ExtendedAttributeNameEntry(byte[] entry) {
		this.parseEntry(entry);
	}
	
	/**
	 * Print the attributes of the name entry
	 *
	 */
	public void printEntry() {
		Utility.printDivider();
		System.out.println("Length of name\t\t" + this.lenOfName);
		System.out.println("Attribute type\t\t" + this.getAttrType(this.attrType));
		System.out.println("Offset to value\t\t" + this.offsetToValue);
		System.out.println("Block location of value\t\t" + this.blockLocationOfValue);
		System.out.println("Size of value\t\t" + this.sizeOfValue);
		System.out.println("Hash of Value\t\t" + this.hashOfValue);
		System.out.println("Name\t\t\t" + this.name);
		Utility.printDivider();
	}
	
	/**
	 * Parse the name entry
	 * @param entry
	 */
	private void parseEntry(byte[] entry) {
		ByteBuffer buf = ByteBuffer.wrap(entry);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// length of name (bytes 0 - 0)
		this.lenOfName = buf.get(0);
		
		// attribute type (bytes 1 - 1)
		this.attrType = buf.get(1);
		
		// offset to value (bytes 2 - 3)
		this.offsetToValue = buf.getShort(2);
		
		// block location value (bytes 4 - 7)
		this.blockLocationOfValue = buf.getInt(4);
		
		// size of value (bytes 8 - 11) 
		this.sizeOfValue = buf.getInt(8);
		
		// hash of value (bytes 12 - 15)
		this.hashOfValue = buf.getInt(12);
		
		// name bytes (16 - (16 + lenOfName - 1)
		byte[] nameBytes = new byte[this.lenOfName];
		for(int i = 0; i < nameBytes.length; i++) {
			nameBytes[i] = buf.get(i + 16);
		}
		this.name = new String(nameBytes);
	}
	
	/**
	 * Get the string literal of the attribute type flag
	 * @param attrType
	 * @return
	 */
	private String getAttrType(int attrType) {
		if(attrType == 1) {
			return "User Space Attribute";
		}
		else if(attrType == 2) {
			return "POSIX ACL";
		}
		else if(attrType == 3) {
			return "POSIX ACL Default";
		}
		else if(attrType == 4) {
			return "Trusted space attribute";
		}
		else if(attrType == 5) {
			return "LUSTRE";
		}
		else {
			return "Security space attribute";
		}
	}
}
