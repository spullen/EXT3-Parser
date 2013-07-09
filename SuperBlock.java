import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class SuperBlock {
	
	// the bytes of the super block (Raw Data)
	private byte[] superBlockBytes;
	
	// super block attributes
	public int numOfInodes;
	public int numOfBlocks;
	public int numOfReservedBlocks;
	public int numOfUnallocatedBlocks;
	public int numOfUnallocatedInodes;
	public int blockGroupZeroStart;
	public int blockSize; // = 1024 << blockSize
	public int fragSize; //  = 1024 << fragSize
	public int blocksPerGroup;
	public int fragPerGroup;
	public int inodesPerGroup;
	public int lastMountTime;
	public int lastWrittenTime;
	public short currMountCount;
	public short maxMountCount;
	public String signature; // 0xEF53
	public short systemState;
	public short errorHandlingMethod;
	public short minorVersion;
	public int lastConsistencyCheckTime;
	public int intervalForForcedConsistencyCheckTime;
	public int creatorOS;
	public int majorVersion;
	public short uidForReservedBlocks;
	public short gidForReservedBlocks;
	public int firstNonReservedInode;
	public short sizeOfInodeStruct;
	public short blockGroupSuperBlockIsPartOf;
	public int compatibleFeatFlags;
	public int incompatibleFeatFlags;
	public int readOnlyFlags;
	public String fileSystemID;
	public String volumeName;
	public String lastMountPath;
	public int algorithmUsageBitmap;
	public int numBlocksPreallocatedForFiles;
	public int numBlocksPreallocatedForDirs;
	public String journalID;
	public int journalInode;
	public int journalDevice;
	public int headOfOrphanInodeList;
	public int[] hashSeeds;
	public int defHashVersion;
	public int defaultMountOptions;
	public int firstMetaBG;
	
	// compatible features
	public boolean dirsIndex   = false;
	public boolean canResize   = false;
	public boolean extAttr     = false;
	public boolean hasJournal  = false;
	public boolean afsInodes   = false;
	public boolean preAllocDir = false;
	
	// incompatible features
	public boolean compression   = true;
	public boolean fileTypeField = true;
	public boolean needRecovery  = true;
	public boolean usesJournalDev= true;
	
	// read only features
	public boolean sparseSuper     = false;
	public boolean containsLgFiles = false;
	public boolean usesBTree       = false;
	
	/**
	 * SuperBlock constructor
	 * @param superblock
	 */
	public SuperBlock(byte[] superBlockBytes) {
		this.superBlockBytes = superBlockBytes;
		this.parseSuperBlockAttributes();
	}
	
	/**
	 * Prints out the superblock data
	 *
	 */
	public void printSuperBlock() {
		System.out.println();
		Utility.printDivider();
		System.out.println("EXT3 Super Block");
		Utility.printDivider();
		System.out.println("Number of inodes\t\t\t" + this.numOfInodes);
		System.out.println("Number of blocks\t\t\t" + this.numOfBlocks);
		System.out.println("Number of reserved blocks\t\t" + this.numOfReservedBlocks);
		System.out.println("Number of unallocated blocks\t\t" + this.numOfUnallocatedBlocks);
		System.out.println("Number of unallocated inodes\t\t" + this.numOfUnallocatedInodes);
		System.out.println("Block where block group 0 starts\t" + this.blockGroupZeroStart);
		System.out.println("Block size\t\t\t\t" + this.blockSize);
		System.out.println("Fragment size\t\t\t\t" + this.fragSize);
		System.out.println("Blocks per group\t\t\t" + this.blocksPerGroup);
		System.out.println("Fragmentation per group\t\t\t" + this.fragPerGroup);
		System.out.println("Inodes per group\t\t\t" + this.inodesPerGroup);
		System.out.println("Last mount time\t\t\t\t" + (Utility.convertNumericToDate((long)this.lastMountTime).toString()));
		System.out.println("Last written time\t\t\t" + (Utility.convertNumericToDate((long)this.lastWrittenTime).toString()));
		System.out.println("Current mount count\t\t\t" + this.currMountCount);
		System.out.println("Maximum mount count\t\t\t" + this.maxMountCount);
		System.out.println("Signature (0xEF53)\t\t\t0x" + this.signature);
		System.out.println("File system state\t\t\t" + getSystemState(this.systemState));
		System.out.println("Error handling method\t\t\t" + getErrorHandling(this.errorHandlingMethod));
		System.out.println("Minor version\t\t\t\t" + this.minorVersion);
		System.out.println("Last consistency check time\t\t" + (Utility.convertNumericToDate((long)this.lastConsistencyCheckTime).toString()));
		System.out.println("Consistency check interval\t\t" + this.intervalForForcedConsistencyCheckTime);
		System.out.println("Creator OS\t\t\t\t" + getCreatorOS(this.creatorOS)); 
		System.out.println("Major version\t\t\t\t" + getMajorVersion(this.majorVersion));
		System.out.println("UID that can use reserved blocks\t" + this.uidForReservedBlocks);
		System.out.println("GID that can use reserved blocks\t" + this.gidForReservedBlocks);
		System.out.println("First non-reserved inode in file system\t" + this.firstNonReservedInode);
		System.out.println("Size of each inode structure\t\t" + this.sizeOfInodeStruct);
		System.out.println("Super blocks block group (backup)\t" + this.blockGroupSuperBlockIsPartOf);
		System.out.println("Compatible features\t\t\t" + getCompatibleFeatures(this.compatibleFeatFlags));
		System.out.println("Incompatible features\t\t\t" + getIncompatibleFeatures(this.incompatibleFeatFlags));
		System.out.println("Read only features\t\t\t" + getReadOnlyFeatures(this.readOnlyFlags)); 
		System.out.println("File System ID\t\t\t\t" + this.fileSystemID);
		System.out.println("Volume Name\t\t\t\t" + this.volumeName);
		System.out.println("Path where last mounted\t\t\t" + this.lastMountPath);
		System.out.println("Algorithm usage bitmap\t\t\t" + this.algorithmUsageBitmap);
		System.out.println("Number of blocks to preallocate for file\t" + this.numBlocksPreallocatedForFiles);
		System.out.println("Number of blocks to preallocate for directories\t" + this.numBlocksPreallocatedForDirs);
		System.out.println("Journal ID\t\t\t\t" + this.journalID);
		System.out.println("Journal inode\t\t\t\t" + this.journalInode);
		System.out.println("Journal device\t\t\t\t" + this.journalDevice);
		System.out.println("Head of orphan inode list\t\t" + this.headOfOrphanInodeList);
		System.out.println("Hash Seeds\t\t\t\t" + Arrays.toString(this.hashSeeds));
		System.out.println("Def Hash Version\t\t\t" + this.defHashVersion);
		System.out.println("Default Mount Options\t\t\t" + this.defaultMountOptions);
		System.out.println("First Meta BG\t\t\t\t" + this.firstMetaBG);
		Utility.printDivider();
		System.out.println();
	}
	
	private void parseSuperBlockAttributes() {
		// create a byte buffer from the super block bytes and set the order to little endian
		ByteBuffer sbBuf = ByteBuffer.wrap(this.superBlockBytes);
		sbBuf.order(ByteOrder.LITTLE_ENDIAN);
		
		// num of inodes (bytes 0 - 3)
		this.numOfInodes = sbBuf.getInt(0);
		
		// num of blocks (bytes 4 - 7)
		this.numOfBlocks = sbBuf.getInt(4);
		
		// num of blocks reserved to prevent fs filling up (bytes 8 - 11)
		this.numOfReservedBlocks = sbBuf.getInt(8);
		
		// num of unallocated blocks (bytes 12 - 15)
		this.numOfUnallocatedBlocks = sbBuf.getInt(12);
		
		// num of unallocated inodes (bytes 16 - 19)
		this.numOfUnallocatedInodes = sbBuf.getInt(16);
		
		// block where block group 0 starts (bytes 20 - 23)
		this.blockGroupZeroStart = sbBuf.getInt(20);
		
		// block size (bytes 24 - 27)
		this.blockSize = sbBuf.getInt(24);
		this.blockSize = 1024 << this.blockSize;
		
		// fragment size (bytes 28 - 31)
		this.fragSize = sbBuf.getInt(28);
		this.fragSize = 1024 << this.fragSize;
		
		// num of blocks per block group (bytes 32 - 35)
		this.blocksPerGroup = sbBuf.getInt(32);
		
		// num of fragments per block group (bytes 36 - 39)
		this.fragPerGroup = sbBuf.getInt(36);
		
		// num of inodes per group (bytes 40 - 43)
		this.inodesPerGroup = sbBuf.getInt(40);
		
		// last mount time (bytes 44 - 47)
		this.lastMountTime = sbBuf.getInt(44);
		
		// last written time (bytes 48 - 51)
		this.lastWrittenTime = sbBuf.getInt(48);
		
		// curr mount count (bytes 52 - 53)
		this.currMountCount = sbBuf.getShort(52);
		
		// max mount count (bytes 54 - 55)
		this.maxMountCount = sbBuf.getShort(54);
		
		// signature (bytes 56 - 57)
		this.signature = String.format("%04X", sbBuf.getShort(56));
		
		// file sys state (bytes 58 - 59)
		this.systemState = sbBuf.getShort(58);
		
		// error handling method (bytes 60 - 61)
		this.errorHandlingMethod = sbBuf.getShort(60);
		
		// minor version (bytes 62 - 63)
		this.minorVersion = sbBuf.getShort(62);
		
		// last consistency check time (bytes 64 - 67)
		this.lastConsistencyCheckTime = sbBuf.getInt(64);
		
		// interval between forced consistency checks (bytes 68 - 71)
		this.intervalForForcedConsistencyCheckTime = sbBuf.getInt(68);
		
		// Creator OS (bytes 72 - 75)
		this.creatorOS = sbBuf.getInt(72); 
		
		// major version (bytes 76 - 79)
		this.majorVersion = sbBuf.getInt(76);
		
		// UID that can use reserved blocks (bytes 80 - 81)
		this.uidForReservedBlocks = sbBuf.getShort(80);
		
		// GID that can use reserved blocks (byte 82 - 83)
		this.gidForReservedBlocks = sbBuf.getShort(82);
		
		// first non-reserved inode in file system (bytes 84 - 87)
		this.firstNonReservedInode = sbBuf.getInt(84);
		
		// size of each inode structure (bytes 88 - 89)
		this.sizeOfInodeStruct = sbBuf.getShort(88);
		
		// block group that this superblock is part of (if backup copy) (bytes 90 - 91)
		this.blockGroupSuperBlockIsPartOf = sbBuf.getShort(90);
		
		// compatible feature flags (bytes 92 - 95)
		this.compatibleFeatFlags = sbBuf.getInt(92);
		
		// incompatible feature flags (bytes 96 - 99)
		this.incompatibleFeatFlags = sbBuf.getInt(96);
		
		// read only feature flags (bytes 100 - 103)
		this.readOnlyFlags = sbBuf.getInt(100);
		
		// file system ID (bytes 104 - 119)
		this.fileSystemID = "";
		for(int i = 104; i <= 119; i++) {
			this.fileSystemID += String.format("%02X", sbBuf.get(i));
		}
		
		// volume name (bytes 120 - 135)
		byte[] volNameBytes = new byte[16];
		for(int i = 0; i < volNameBytes.length; i++) {
			volNameBytes[i] = sbBuf.get(120 + i);
		}
		this.volumeName = new String(volNameBytes);
		
		// path where last mounted (bytes 136 - 199)
		byte[] pathMountedBytes = new byte[64];
		for(int i = 0; i < pathMountedBytes.length; i++) {
			pathMountedBytes[i] = sbBuf.get(136 + i);
		}
		this.lastMountPath = new String(pathMountedBytes);
		
		// algorithm usage bitmap (bytes 200 - 203)
		this.algorithmUsageBitmap = sbBuf.getInt(200);
		
		// num of blocks pre allocated for files (bytes 204 - 204)
		this.numBlocksPreallocatedForFiles = (int)sbBuf.get(204);
		
		// num of blocks pre allocated for directories (bytes 205 - 205)
		this.numBlocksPreallocatedForDirs = (int)sbBuf.get(205);
		
		// journal ID (bytes 208 - 223)
		byte[] journalIDBytes = new byte[16];
		for(int i = 0; i < journalIDBytes.length; i++) {
			journalIDBytes[i] = sbBuf.get(208 + i);
		}
		this.journalID = new String(journalIDBytes);
		
		// journal inode (bytes 224 - 227)
		this.journalInode = sbBuf.getInt(224);
		
		// journal device (bytes 228 - 231)
		this.journalDevice = sbBuf.getInt(228);
		
		// head of orphan inode list (bytes 232 - 235)
		this.headOfOrphanInodeList = sbBuf.getInt(232);
		
		// hash seeds (bytes 236 - 251)
		this.hashSeeds = new int[4];
		for (int i = 0; i < 4; i++) {
			this.hashSeeds[i] = sbBuf.getInt();
		}
		
		// def hash version (byte 252)
		this.defHashVersion = (int) sbBuf.get(252);
		
		// skip bytes 253-257 (padding)
		
		// default mount options (bytes 256 - 259)
		this.defaultMountOptions = sbBuf.getInt(256);
		
		// first meta bg (bytes 260 - 263)
		this.firstMetaBG = sbBuf.getInt(260);
		
		// remaining 760 bytes of superblock is padding
	}
	
	/**
	 * Returns the text version of the state based on the flag
	 * @param flag
	 * @return state String
	 */
	public String getSystemState(int flag) {
		if(flag == 1) {
			return "File System is clean";
		}
		else if(flag == 2) {
			return "File system has errors";
		}
		else {
			return "Orphan inodes are being recovered";
		}
	}
	
	/**
	 * Get the String version of the error handling
	 * @param value
	 * @return errorHandlingString String
	 */
	public String getErrorHandling(int value) {
		if(value == 1) {
			return "Continue";
		}
		else if(value == 2) {
			return "Remount file system as read only";
		}
		else {
			return "Panic";
		}
	}
	
	/**
	 * Get the OS the filesystem was created with
	 * @param value
	 * @return CreatorOSString String
	 */
	public String getCreatorOS(int value) {
		if(value == 0) {
			return "Linux";
		}
		else if(value == 1) {
			return "GNU Hurd";
		}
		else if(value == 2) {
			return "Masix";
		}
		else if(value == 3) {
			return "FreeBSD";
		}
		else {
			return "Lites";
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public String getMajorVersion(int value) {
		if(value == 0) {
			return "Original";
		}
		else {
			return "Dynamic";
		}
	}
	
	/**
	 * Get all of the compatible features for the filesystem
	 * @param flag
	 * @return
	 */
	public String getCompatibleFeatures(int value) {
		String features = "";
		if((value / 32) > 0 && value >= 32) {
			features += "Dir index_ ";
			value -= 32;
			this.dirsIndex = true;
		}
		if((value / 16) > 0 && value >= 16) {
			features += "Can resize_ ";
			value -= 16;
			this.canResize = true;
		}
		if((value / 8) > 0 && value >= 8) {
			features += "Ext Attr_ ";
			value -= 8;
			this.extAttr = true;
		}
		if((value / 4) > 0 && value >= 4) {
			features += "Journal_ ";
			value -= 4;
			this.hasJournal = true;
		}
		if((value / 2) > 0 && value >= 2) {
			features += "AFS inode exists_ ";
			value -= 2;
			this.afsInodes = true;
		}
		if((value / 1) > 0 && value >= 1) {
			features += "Preallocated Dirs_ ";
			value -= 1;
			this.preAllocDir = true;
		}
		features = features.replace('_', ',');
		if(features.length() >= 2) {
			features = features.substring(0, features.length() - 2);
		}
		return features;
	}
	
	/**
	 * Gets the incompatible features
	 * @param value
	 * @return
	 */
	public String getIncompatibleFeatures(int value) {
		String features = "";
		if((value / 8) > 0 && value >= 8) {
			features += "Journal Dev_ ";
			value -= 8;
			this.usesJournalDev = false;
		}
		if((value / 4) > 0 && value >= 4) {
			features += "Needs Recovery_ ";
			value -= 4;
			this.needRecovery = false;
		}
		if((value / 2) > 0 && value >= 2) {
			features += "Contain File Type_ ";
			value -= 2;
			this.fileTypeField = false;
		}
		if((value / 1) > 0 && value >= 1) {
			features += "Compression_";
		}
		features = features.replace('_', ',');
		if(features.length() >= 2) {
			features = features.substring(0, features.length() - 2);
		}
		return features;
	}
	
	/**
	 * Gets the read only features
	 * @param value
	 * @return features
	 */
	public String getReadOnlyFeatures(int value) {
		String features = "";
		if((value / 4) > 0 && value >= 4) {
			features += "Uses B-Tree_ ";
			value -= 4;
			this.usesBTree = true;
		}
		if((value / 2) > 0 && value >= 2) {
			features += "Contains Large Files_ ";
			value -= 2;
			this.containsLgFiles = true;
		}
		if((value / 1) > 0 && value >= 1) {
			features += "Sparse tables_ ";
			value -= 1;
			this.sparseSuper = true;
		}
		features = features.replace('_', ',');
		if(features.length() >= 2) {
			features = features.substring(0, features.length() - 2);
		}
		return features;
	}
}
