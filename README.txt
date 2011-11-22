EXT3 Parser 
-------------------------------------------------
By:   Scott Pullen
Date: 2008-12-15


For digital forensics course_____________________________________________________________________________________________

About:
Parses an EXT3 image. The EXT3 filesystem is one of the more popular file systems used by
the Linux operating system. This program allows the user to examine the super block
of the file system, the descriptor table and an arbitrary inode to examine. The descriptor
table contains information about all of the block groups. A block group is a group of blocks.
Blocks can contain a variety of data, like inodes, descriptor table, inode allocation bitmaps,
or actual file data. Each block group contains an inode table that contains inodes of files
and directories stored on the filesystem. 

If the inode selected is for a directory then it will print out all of the files
and other directories' names contained within the directory. This will also show what 
file type it is, if that flag is set in the super block in the incompatible types field.

I used the fsstat and istat programs by Brian Carrier to verify the information that
I was getting from my parser.

_____________________________________________________________________________________________

Options:
-h         - Help (prints instructions)
-o <value> - Offset to image in bytes (Optional parameter, default is 0)
-i <value> - Inode Location to parse (Optional Parameter)

_____________________________________________________________________________________________

Compilation (I compiled mine with java 6.0):
%> javac *.java

_____________________________________________________________________________________________

Running the program:

Print instructions:
%> java Ext3Parser
or
%> java Ext3Parser -h

-------------------------------------------------

Parse image (offset = 0):
%> java Ext3Parser <path_to_image>

-------------------------------------------------

Parse image with offset:
%> java Ext3Parser <path_to_image> -o <offset>

-------------------------------------------------

Parse image and parsing an arbitray inode:
%> java Ext3Parser <path_to_image> -i <inode_number>

-------------------------------------------------

Parse image and parse arbitrary inode with image offset:
%> java Ext3Parser <path_to_image> -o <offset> -i <inode_number>

_____________________________________________________________________________________________

Acquiring an image:

Create an image requires Unix OS (Linux, FreeBSD, etc...) and dd):
Create a new ext3 partition (one that is ~1gb or greater)
Run the dd command on the device (located in /dev/<drive_device>)
	(ex. dd if=/dev/sda1 of=~/Desktop/ext3_image.dd)
	
It is easier on fresh devices where there are no other partitions, if there is a partition
you'll need to put the offset as a parameter to dd.
You also have to be in root in order to run the dd program on the device, or run the 
program with sudo (ex. sudo dd ...).

An easy device to do this on is a flash drive (you can get a cheap one and put an EXT3
partition on it and then run the dd program on that device), throw some file on it so
you can actually have stuff parsed out instead of just 0's and junk.

_____________________________________________________________________________________________

Extensions:
One thing that I wanted to examine when writing this program is an extended attributes
parser. An inode can have extended attributes giving more information about the file.
I did write the code to do all of this, however it is untested (not by choice though).
None of the images that I used had any inodes with extended attributes. To test this I 
wrote a function that would just loop through all of the inodes on the filesystem and
stop if it every found an attribute. This never happened on the images I used. So in the
future I would like to get this working with an image with inodes that actually have 
attributes because I would hate to see the code I wrote go to waste when I spent a good deal
of time writing that portion not getting to use it. This was the only thing that changed from
my original proposal. However I added the directory entry parser for an inode with that 
is for a directory. This will print out all of the file names for that directory.

_____________________________________________________________________________________________

Notes:

- If the inode data returned is all zeroed then the inode is unallocated.
- If the dates are set to Dec 31 1961 then they are zeroed out, this can be seen
  with deletion times, if the file is not deleted, or if it is unallocated with the
  other dates.
- For some reason getting inode data doesn't work too well with images that are small.
_____________________________________________________________________________________________

Side note: Theoretically this could parse an EXT2 image (since EXT3 is an extension of
EXT2), but untested.
