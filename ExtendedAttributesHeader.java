import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ExtendedAttributesHeader {

	// attributes
	String signature = "";
	int refCount;
	int numberOfBlocks;
	int hash;
	
	public ExtendedAttributesHeader(byte[] headerBytes) {
		this.parseHeader(headerBytes);
	}
	
	public void printHeader() {
		Utility.printDivider();
		System.out.println("Attribute Header");
		Utility.printDivider();
		System.out.println("Signature\t\t\t" + this.signature);
		System.out.println("Reference Count\t\t" + this.refCount);
		System.out.println("Number of Blocks\t\t" + this.numberOfBlocks);
		System.out.println("Hash\t\t\t" + this.hash);
		Utility.printDivider();
	}
	
	/**
	 * Parse the header bytes
	 * @param headerBytes
	 */
	private void parseHeader(byte[] headerBytes) {
		ByteBuffer buf = ByteBuffer.wrap(headerBytes);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// signature (bytes 0 - 3)
		this.signature = String.format("%04X", buf.getInt(0));
		
		// ref count (bytes 4 - 7)
		this.refCount = buf.getInt(4);
		
		// number of blocks (bytes 8 - 11)
		this.numberOfBlocks = buf.getInt(8);
		
		// hash (bytes 12 - 15)
		this.hash = buf.getInt(12);
	}
}
