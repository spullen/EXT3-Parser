
public class Ext3Parser {
	private String imageFile;
	private int offsetToImage;
	
	private int numberOfBlockGroups = 0;
	
	private SuperBlock superBlock = null;
	private GroupDescriptorTable gdt = null;
	
	/**
	 * Constructor for Ext3Parser
	 * @param imageFile
	 * @param offsetToImage
	 */
	public Ext3Parser(String imageFile, int offsetToImage) {
		this.imageFile = imageFile;
		this.offsetToImage = offsetToImage;
	}
	
	/**
	 * Parse the super block of the ext3 image
	 *
	 */
	public void parseSuperBlock() {
		// read in the super block from the image
		byte[] superBlockBytes = new byte[1024];
		superBlockBytes = FileReader.seekAndRead(superBlockBytes, (1024 + this.offsetToImage), this.imageFile);
		
		this.superBlock = new SuperBlock(superBlockBytes);
		this.superBlock.printSuperBlock();
	}
	
	/**
	 * Parse the group descriptor table
	 *
	 */
	public void parseGroupDescriptorTable() {
		// read in the block of the group descriptor table
		byte[] groupDescriptorTableBytes = new byte[this.superBlock.blockSize];
		groupDescriptorTableBytes = FileReader.seekAndRead(groupDescriptorTableBytes, (this.superBlock.blockSize + this.offsetToImage), this.imageFile);
		
		// figure out how many groups are in the filesystem
		double groups = (this.superBlock.numOfBlocks * 1.0)/(this.superBlock.blocksPerGroup * 1.0);
		this.numberOfBlockGroups = (int)Math.ceil(groups);
		this.gdt = new GroupDescriptorTable(groupDescriptorTableBytes, this.numberOfBlockGroups);
		this.gdt.printGroupDescriptorTable();
	}
	
	/**
	 * Check to see if the inode is in range
	 * @param inodeLocation
	 * @return false if not in range otherwise return true
	 */
	public boolean inodeInRange(int inodeLocation) {
		if(inodeLocation < 1 || inodeLocation > this.superBlock.numOfInodes) {
			return false;
		}
		return true;
	}
	
	/**
	 * Parses an inode specified from the user
	 * @param inodeLocation
	 */
	public void parseInode(int inodeLocation) {
		// figure out which group the inode is in
		int groupNumber = (inodeLocation - 1)/this.superBlock.blocksPerGroup;
		
		// get the group descriptor entry
		GroupDescriptorEntry gde = this.gdt.getGroupDescriptorEntry(groupNumber);
		
		// figure out how many inodes per block there are
		int inodesPerBlock = (int)Math.ceil(this.superBlock.blockSize/this.superBlock.sizeOfInodeStruct);
		
		// figure out which block the inode resides in
		int inodeBlock = gde.startingBlockAddressInodeTable + (((inodeLocation - 1) % this.superBlock.inodesPerGroup)/ inodesPerBlock);
		
		// figure out how many bytes are needed to seek into the image file
		int imageSeek = inodeBlock * this.superBlock.blockSize;
		
		// figure out the offset in the block to the inode
		int indexOfInodeInTable = (((inodeLocation - 1) % this.superBlock.inodesPerGroup) % inodesPerBlock);
		int offsetIntoBlock = indexOfInodeInTable * this.superBlock.sizeOfInodeStruct;
		
		// add the offset into the block to the image seek
		imageSeek += offsetIntoBlock;
		
		
		// sanity check
		System.out.println("Starting block address: " + gde.startingBlockAddressInodeTable);
		System.out.println("Inodes per block: " + inodesPerBlock);
		System.out.println("Index of inode in inode table: " + indexOfInodeInTable);
		System.out.println("Offset into block: " + offsetIntoBlock);
		System.out.println("Image seek: " + imageSeek);
		
		
		// create the byte array to hold the inode
		byte[] inodeBytes = new byte[this.superBlock.sizeOfInodeStruct];
		
		// read in the inode bytes
		inodeBytes = FileReader.seekAndRead(inodeBytes, imageSeek, this.imageFile);
		
		// create a new Inode object
		Inode inode = new Inode(inodeBytes, inodeLocation);
		inode.printInode();
		
		/*
		 * NOTE: For some reason I couldn't find any inode with attributes.
		 * 	     To test this I made a script that ran through all the inodes 
		 * 		 on the filesyste and then break out when I found any attributes,
		 * 		 turns out each image that I tried this on never exited showing
		 * 		 that I could find not attributes. Furthermore the code for 
		 * 		 parsing the attributes is not tested and left out (by commenting)
		// get the extended attributes block number
		int extendedAttributeBlockNumber = inode.getExtendedAttributesBlockNumber();
		// if the extended attribute block number is greater than 0 then parse it
		if(extendedAttributeBlockNumber > 0) {
			// read in the block
			byte[] attributeBlock = new byte[this.superBlock.blockSize];
			int seek = extendedAttributeBlockNumber * this.superBlock.blockSize;
			attributeBlock = FileReader.seekAndRead(attributeBlock, seek, this.imageFile);
			
			// create a new extended attributes object and parse the attributes
			ExtendedAttributes xAttr = new ExtendedAttributes(attributeBlock);
			
			// print the attributes information
			xAttr.printAttributes();
		}
		*/
		
		// parse the directory entry if the inode is for a directory
		if(inode.isDirectory) {
			Utility.printDivider();
			System.out.println("Directory Entry for Inode " + inodeLocation);
			Utility.printDivider();
				
			// get the directory entry block number
			int dirEntryBlockNum = inode.getDirectoryEntryBlockNumber();
				
			if(dirEntryBlockNum > 0) {
				byte[] dirEntry = new byte[this.superBlock.blockSize];
				
				// get the offset to the dir entry block and read in those bytes
				int seek = dirEntryBlockNum * this.superBlock.blockSize;
				dirEntry = FileReader.seekAndRead(dirEntry, seek, this.imageFile);
				
				// parse that block
				// create a new DirectoryEntry object to do this
				DirectoryEntry de = new DirectoryEntry(dirEntry, this.superBlock.fileTypeField);
				
				// print out the directories
				de.printDirectories();
			}
		}
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String imageFile = "";
		int offsetToImage = 0;
		int inodeLocation = -1;
		
		// get the parameters
		if(args.length == 5) {
			imageFile = args[0];
			offsetToImage = (new Integer(args[2])).intValue();
			inodeLocation = (new Integer(args[4])).intValue();
		}
		else if(args.length == 3) {
			imageFile = args[0];
			if(args[1].equals("-o")) {
				offsetToImage = (new Integer(args[2])).intValue();
			}
			else {
				inodeLocation = (new Integer(args[2])).intValue();
			}
		}
		else if(args.length == 1) {
			if(args[0].equals("-h")) {
				instructions();
				System.exit(0);
			}
			imageFile = args[0];
		}
		else {
			instructions();
			System.exit(0);
		}
		
		Ext3Parser ext3 = new Ext3Parser(imageFile, offsetToImage); // create a new Ext3Parser object
		ext3.parseSuperBlock(); // parse the super block
		ext3.parseGroupDescriptorTable(); // parse the group descriptor table
		if(inodeLocation != -1) {
			// check to see if the inode is range
			if(ext3.inodeInRange(inodeLocation)) {
				ext3.parseInode(inodeLocation); // if the inode is in range then parse the inode
			}
			else {
				System.out.println("Inode Location entered is not in range");
			}
		}
	}
	
	/**
	 * Print out the instructions for the program
	 *
	 */
	public static void instructions() {
		System.out.println("EXT3 Filesystem Parser");
		System.out.println("To run program:");
		System.out.println("java Ext3Parser <path_to_file>");
		System.out.println("java Ext3Parser <path_to_file> -o <offset_to_image>");
		System.out.println("java Ext3Parser <path_to_file> -i <inode_location>");
		System.out.println("java Ext3Parser <path_to_file> -o <offset_to_image> -i <inode_location>");
		System.out.println("java Ext3Parser -h");
		System.out.println("-h: Help (prints out the instructions)");
		System.out.println("-o: Offset to image in file (Optional, default is 0)");
		System.out.println("-i: Inode Location to examine");
	}

}
