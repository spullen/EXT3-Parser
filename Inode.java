import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Inode {
	
	private byte[] inodeBytes = null;
	private int inodeNumber;
	
	// inode attributes
	int fileMode;
	short lowUserID;
	int lowSizeInBytes;
	int accessTime;
	int changeTime;
	int modTime;
	int delTime;
	short lowGroupID;
	short linkCount;
	int sectorCount;
	int flags;
	int[] directBlockPtrs = new int[12]; // (48 bytes / 12 ptrs) = 4 bytes each, 4 bytes == 1 int
	int singleIndirectBlockPtr;
	int doubleIndirectBlockPtr;
	int tripeIndirectBlockPtr;
	int genNum;
	int extAttrBlock;
	int upSizeInBytes;
	int blockAddrOfFrag;
	int fragIndexInBlock;
	int fragSize;
	short upUserID;
	short upGroupID;
	
	boolean isDirectory = false;
	
	/**
	 * Inode Constructor
	 * @param inodeBytes
	 */
	public Inode(byte[] inodeBytes, int inodeNumber) {
		this.inodeBytes = inodeBytes;
		this.inodeNumber = inodeNumber;
		this.parseInode();
	}
	
	/**
	 * Print out the inode infromation
	 *
	 */
	public void printInode() {
		Utility.printDivider();
		System.out.println("Inode " + this.inodeNumber);
		Utility.printDivider();
		//System.out.println("File mode\t\t\t" + this.fileMode);
		System.out.println("Permissions\t\t\t" + this.getPermissions(this.fileMode & 0x01FF));
		System.out.println("Exec Flags\t\t\t" + this.getExecFlags((this.fileMode & 0x0E00) >> 9));
		System.out.println("Type Flags\t\t\t" + this.getTypeFlags((this.fileMode & 0xF000) >> 12));
		System.out.println("Lower bits of userID\t\t" + this.lowUserID);
		System.out.println("Lower bits of size\t\t" + this.lowSizeInBytes);
		System.out.println("Access Time\t\t\t" + (Utility.convertNumericToDate(this.accessTime)).toString());
		System.out.println("Change Time\t\t\t" + (Utility.convertNumericToDate(this.changeTime)).toString());
		System.out.println("Mod Time\t\t\t" + (Utility.convertNumericToDate(this.modTime)).toString());
		System.out.println("Delete Time\t\t\t" + (Utility.convertNumericToDate(this.delTime)).toString());
		System.out.println("Lower bits groupID\t\t" + this.lowGroupID);
		System.out.println("Link Count\t\t\t" + this.linkCount);
		System.out.println("Sector Count\t\t\t" + this.sectorCount);
		System.out.println("Inode Flags\t\t\t" + this.getFlags(this.flags));
		System.out.print("Direct Block Pointers[ ");
		for(int i = 0; i < this.directBlockPtrs.length; i++) {
			System.out.print(this.directBlockPtrs[i] + " ");
		}
		System.out.print("]\n");
		System.out.println("Single Indirect Pointer\t\t" + this.singleIndirectBlockPtr);
		System.out.println("Double Indirect Pointer\t\t" + this.doubleIndirectBlockPtr);
		System.out.println("Triple Indirect Pointer\t\t" + this.tripeIndirectBlockPtr);
		System.out.println("Generation Number\t\t" + this.genNum);
		System.out.println("Extended Attribute Block\t" + this.extAttrBlock);
		System.out.println("Upper bits of size\t\t" + this.upSizeInBytes);
		System.out.println("Block address of fragmentation\t" + this.blockAddrOfFrag);
		System.out.println("Fragmentation Index in block\t" + this.fragIndexInBlock);
		System.out.println("Fragmentation Size\t\t" + this.fragSize);
		System.out.println("Upper bits of userID\t\t" + this.upUserID);
		System.out.println("Upper bits of groupID\t\t" + this.upGroupID);
		Utility.printDivider();
		System.out.println();
	}
	
	/**
	 * Get the block number which the extended attributes are in
	 * @return extend
	 */
	public int getExtendedAttributesBlockNumber() {
		return this.extAttrBlock;
	}
	
	/**
	 * Get the block number where the directory entry is in
	 * @return 
	 */
	public int getDirectoryEntryBlockNumber() {
		return this.directBlockPtrs[0];
	}
	
	/**
	 * Parse the attributes of the Inode
	 *
	 */
	private void parseInode() {
		ByteBuffer buf = ByteBuffer.wrap(this.inodeBytes);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// file mode (bytes 0 - 1)
		this.fileMode = buf.getShort(0);
		
		// lower 16 bits of user ID (bytes 2 - 3)
		this.lowUserID = buf.getShort(2);
		
		// lower 32 bits of group ID (bytes 4 - 7)
		this.lowSizeInBytes = buf.getInt(4);
		
		// access time (bytes 8 - 11)
		this.accessTime = buf.getInt(8);
		
		// change time (bytes 12 - 15)
		this.changeTime = buf.getInt(12);
		
		// modification time (bytes 16 - 19)
		this.modTime = buf.getInt(16);
		
		// deletion time (bytes 20 - 23)
		this.delTime = buf.getInt(20);
		
		// lower 16 bits of group ID (bytes 24 - 25)
		this.lowGroupID = buf.getShort(24);
		
		// link count (bytes 26 - 27)
		this.linkCount = buf.getShort(26);
		
		// sector count (bytes 28 - 31)
		this.sectorCount = buf.getInt(28);
		
		// flags (bytes 32 - 35)
		this.flags = buf.getInt(32);
		
		// 12 direct block ptrs (bytes 40 - 87)
		for(int i = 0; i < 12; i++) {
			this.directBlockPtrs[i] = buf.getInt((i * 4) + 40);
		}
		
		// single indirect ptr (bytes 88 - 91)
		this.singleIndirectBlockPtr = buf.getInt(88);
		
		// double indirect ptr (bytes 92 - 95)
		this.doubleIndirectBlockPtr = buf.getInt(92);
		
		// triple indirect ptr (bytes 96 - 99)
		this.tripeIndirectBlockPtr = buf.getInt(96);
		
		// generation number (bytes 100 - 103)
		this.genNum = buf.getInt(100);
		
		// ext attr block (bytes 104 - 107)
		this.extAttrBlock = buf.getInt(104);
		
		// upper 32 bits of size / Dir ACL (bytes 108 - 111) 
		this.upSizeInBytes = buf.getInt(108);
		
		// block address of fragments (bytes 112 - 115)
		this.blockAddrOfFrag = buf.getInt(112);
		
		// fragment index in block (bytes 116 - 116)
		this.fragIndexInBlock = (int)buf.get(116);
		
		// fragment size (bytes 117 - 117)
		this.fragSize = (int)buf.get(117);
		
		// upper 16 bits of userID (bytes 120 - 121)
		this.upUserID = buf.getShort(120);
		
		// upper 16 bits of groupID (bytes 122 - 123)
		this.upGroupID = buf.getShort(122);
	}
	
	/**
	 * Get the file permissions
	 * @param permissionFlag
	 * @return
	 */
	private String getPermissions(int permissionFlag) {
		String permissions = "";
		// user read
		if((permissionFlag / 0x100) > 0 && permissionFlag >= 0x100) {
			permissions += "r";
			permissionFlag -= 0x100;
		}
		else {
			permissions += "-";
		}
		// user write
		if((permissionFlag / 0x080) > 0 && permissionFlag >= 0x080) {
			permissions += "w";
			permissionFlag -= 0x080;
		}
		else {
			permissions += "-";
		}
		// user execute
		if((permissionFlag / 0x040) > 0 && permissionFlag >= 0x040) {
			permissions += "x | ";
			permissionFlag -= 0x040;
		}
		else {
			permissions += "- | ";
		}
		// group read
		if((permissionFlag / 0x020) > 0 && permissionFlag >= 0x020) {
			permissions += "r";
			permissionFlag -= 0x020;
		}
		else {
			permissions += "-";
		}
		// group write
		if((permissionFlag / 0x010) > 0 && permissionFlag >= 0x010) {
			permissions += "w";
			permissionFlag -= 0x010;
		}
		else {
			permissions += "-";
		}
		// group execute
		if((permissionFlag / 0x008) > 0 && permissionFlag >= 0x008) {
			permissions += "x | ";
			permissionFlag -= 0x008;
		}
		else {
			permissions += "- | ";
		}
		// other read
		if((permissionFlag / 0x004) > 0 && permissionFlag >= 0x004) {
			permissions += "r";
			permissionFlag -= 0x004;
		}
		else {
			permissions += "-";
		}
		// other write
		if((permissionFlag / 0x002) > 0 && permissionFlag >= 0x002) {
			permissions += "w";
			permissionFlag -= 0x002;
		}
		else {
			permissions += "-";
		}
		// other execute
		if((permissionFlag / 0x001) > 0 && permissionFlag >= 0x001) {
			permissions += "x";
			permissionFlag -= 0x001;
		}
		else {
			permissions += "-";
		}
		return permissions;
	}
	
	/**
	 * Get the file flags
	 * @param flags
	 * @return
	 */
	private String getExecFlags(int flags) {
		String flagStr = "";
		// set user id
		if((flags / 0x800) > 0 && flags >= 0x800) {
			flagStr += "Set User ID_";
			flags -= 0x800;
		}
		// set group id
		if((flags / 0x400) > 0 && flags >= 0x400) {
			flagStr += "Set Group ID_";
			flags -= 0x400;
		}
		if((flags / 0x200) > 0 && flags >= 0x200) {
			flagStr += "Sticky Bit_ ";
			flags -= 0x200;
		}
		flagStr = flagStr.replace('_', ',');
		if(flagStr.length() >= 2) {
			flagStr = flagStr.substring(0, flagStr.length() - 2);
		}
		return flagStr;
	}
	
	/**
	 * Gets the type flags
	 * @param flag
	 * @return
	 */
	private String getTypeFlags(int flags) {
		String typeFlags = "";
		// unix socket
		if((flags / 0xC) > 0 && flags >= 0xC) {
			typeFlags += "Unix Socket_ ";
			flags -= 0xC;
		}
		// sym link
		if((flags / 0xA) > 0 && flags >= 0xA) {
			typeFlags += "Sym Link_ ";
			flags -= 0xA;
		}
		// regular file
		if((flags / 0x8) > 0 && flags >= 0x8) {
			typeFlags += "Regular File_ ";
			flags -= 0x8;
		}
		// block device
		if((flags / 0x6) > 0 && flags >= 0x6) {
			typeFlags += "Block Device_ ";
			flags -= 0x6;
		}
		// Directory
		if((flags / 0x4) > 0 && flags >= 0x4) {
			typeFlags += "Directory_ ";
			flags -= 0x4;
			this.isDirectory = true;
		}
		// Character Device
		if((flags / 0x2) > 0 && flags >= 0x2) {
			typeFlags += "Character Device_ ";
			flags -= 0x2;
		}
		// FIFO
		if((flags / 0x1) > 0 && flags >= 0x1) {
			typeFlags += "FIFO_ ";
			flags -= 0x1;
		}
		typeFlags = typeFlags.replace('_', ',');
		if(typeFlags.length() >= 2) {
			typeFlags = typeFlags.substring(0, typeFlags.length() - 2);
		}
		return typeFlags;
	}
	
	/**
	 * Gets the inode field flags
	 * @param flags
	 * @return
	 */
	private String getFlags(int flags) {
		String flagsStr = "";
		// is journaled w/ EXT3
		if((flags / 0x00002000) > 0 && flags >= 0x00002000) {
			flagsStr += "File Data is journaled with EXT3_ ";
			flags -= 0x00002000;
		}
		// hash index directory
		if((flags / 0x00001000) > 0 && flags >= 0x00001000) {
			flagsStr += "Hash indexed directory_ ";
			flags -= 0x00001000;
		}
		// A-time is not updated
		if((flags / 0x00000080) > 0 && flags >= 0x00000080) {
			flagsStr += "A-time is not updated_ ";
			flags -= 0x00000080;
		}
		// file is not included in 'dump' cmd
		if((flags / 0x00000040) > 0 && flags >= 0x00000040) {
			flagsStr += "File is not included in 'dump' command_ ";
			flags -= 0x00000040;
		}
		// append only
		if((flags / 0x00000020) > 0 && flags >= 0x00000020) {
			flagsStr += "Append Only_ ";
			flags -= 0x00000020;
		}
		// immutable file
		if((flags / 0x00000010) > 0 && flags >= 0x00000010) {
			flagsStr += "Immutable File_ ";
			flags -= 0x00000010;
		}
		// Sync Updates
		if((flags / 0x00000008) > 0 && flags >= 0x00000008) {
			flagsStr += "Sync Updates_ ";
			flags -= 0x00000008;
		}
		// File Compression
		if((flags / 0x00000004) > 0 && flags >= 0x00000004) {
			flagsStr += "File Compression_ ";
			flags -= 0x00000004;
		}
		// Keep data copy on delete
		if((flags / 0x00000002) > 0 && flags >= 0x00000002) {
			flagsStr += "Keep data copy on delete_ ";
			flags -= 0x00000002;
		}
		// secure deletion
		if((flags / 0x00000001) > 0 && flags >= 0x00000001) {
			flagsStr += "Secure Deletion_ ";
			flags -= 0x00000001;
		}
		flagsStr = flagsStr.replace('_', ',');
		if(flagsStr.length() >= 2) {
			flagsStr = flagsStr.substring(0, flagsStr.length() - 2);
		}
		return flagsStr;
	}
}
