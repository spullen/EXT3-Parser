import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class GroupDescriptorEntry {
	
	// attributes
	int startingBlockAddressBlockBitmap;
	int startingBlockAddressInodeBitmap;
	int startingBlockAddressInodeTable;
	short numUnallocatedBlocksInGroup;
	short numUnallocatedInodesInGroup;
	short numDirectoriesInGroup;
	short pad;
	
	public GroupDescriptorEntry(byte[] entryBytes) {
		this.parseEntry(entryBytes);
	}
	
	/**
	 * Print out the entry data
	 *
	 */
	public void printEntry() {
		System.out.println("\tBlock Address of Block Bitmap\t" + this.startingBlockAddressBlockBitmap);
		System.out.println("\tBlock Address of Inode Bitmap\t" + this.startingBlockAddressInodeBitmap);
		System.out.println("\tBlock Address of Inode Table\t" + this.startingBlockAddressInodeTable);
		System.out.println("\tUnallocated Blocks in Group\t" + this.numUnallocatedBlocksInGroup);
		System.out.println("\tUnallocated Inodes in Group\t" + this.numUnallocatedInodesInGroup);
		System.out.println("\tNumber of Directories in Group\t" + this.numDirectoriesInGroup);
		System.out.println("\tPad\t\t\t\t" + this.pad);
		System.out.println();
	}
	
	/**
	 * Parse the raw bytes of the group description entry
	 * @param entryBytes
	 */
	private void parseEntry(byte[] entryBytes) {
		ByteBuffer buf = ByteBuffer.wrap(entryBytes);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// starting block address of block bitmap (bytes 0 - 3)
		this.startingBlockAddressBlockBitmap = buf.getInt(0);
		
		// starting block address of inode bitmap (bytes 4 - 7)
		this.startingBlockAddressInodeBitmap = buf.getInt(4);
		
		// starting block address of inode table (bytes 8 - 11)
		this.startingBlockAddressInodeTable = buf.getInt(8);
		
		// number of unallocated blocks in group (bytes 12 - 13)
		this.numUnallocatedBlocksInGroup = buf.getShort(12);
		
		// number of unallocated inodes in group (bytes 14 - 15)
		this.numUnallocatedInodesInGroup = buf.getShort(14);
		
		// number of directories in group (bytes 16 - 17)
		this.numDirectoriesInGroup = buf.getShort(16);
		
		// pad (bytes 18 - 19)
		this.pad = buf.getShort(18);
		
		// reserved (bytes 20 - 31)
	}
}
