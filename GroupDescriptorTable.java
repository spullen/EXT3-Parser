import java.util.LinkedList;
import java.util.List;

public class GroupDescriptorTable {
	
	private byte[] grpDescTableBytes = null;
	private int numberOfBlockGroups = 0;
	
	// entries of the group descriptor table
	private List<GroupDescriptorEntry> entries = new LinkedList<GroupDescriptorEntry>();
	
	/**
	 * Group Descriptor Table Constructor
	 * @param grpDescTableBytes
	 */
	public GroupDescriptorTable(byte[] grpDescTableBytes, int numberOfBlockGroups) {
		this.grpDescTableBytes = grpDescTableBytes;
		this.numberOfBlockGroups = numberOfBlockGroups;
		this.parseGroupDescriptorTable();
	}
	
	/**
	 * Prints each entry of the group descriptor table
	 *
	 */
	public void printGroupDescriptorTable() {
		int cnt = 0;
		for(GroupDescriptorEntry gde : this.entries) {
			System.out.println("Block Group " + cnt + ":");
			gde.printEntry();
			cnt++;
		}
	}
	
	/**
	 * Get the group entry
	 * @param groupNumber
	 * @return
	 */
	public GroupDescriptorEntry getGroupDescriptorEntry(int groupNumber) {
		return this.entries.get(groupNumber);
	}
	
	/**
	 * Parses the Group Descriptor Table
	 *
	 */
	private void parseGroupDescriptorTable() {
		for(int i = 0; i < this.numberOfBlockGroups; i++) {
			// offset = i * 32
			// read from offset upto offset + (32 - 1)
			byte[] entry = new byte[32];
			for(int j = 0; j < entry.length; j++) {
				entry[j] = this.grpDescTableBytes[j + (i * 32)];
			}
			// create new Group Descriptor Entry
			this.entries.add(new GroupDescriptorEntry(entry));
		}
	}
}
