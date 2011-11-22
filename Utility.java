import java.util.*;
import java.nio.*;
import java.math.BigInteger;

/**
 * Class provides some helpful methods
 *
 */
public class Utility {


	/**
	 * This converts the dateNumeric which is msecs from the 
	 * unix epoc to a Date object
	 * @param dateNumeric
	 * @return
	 */
	public static Date convertNumericToDate(long dateNumeric) {
		return new Date(dateNumeric * 1000);
	}
	
	/**
	 * This converts a dateNumeric from either a unixEpoch or from Jan 1, 1601 in 
	 * nSecs. 
	 * If the unixEpoch is true then it just calls convertNumericToDate(dateNumeric)
	 * else it converts the value to the unix epoch date numeric and then calls 
	 * convertNumericToDate(dateNumeric)
	 * @param dateNumeric
	 * @param unixEpoch
	 * @return
	 */
	public static Date convertNumericToDate(long dateNumeric, boolean unixEpoch) {
		if(!unixEpoch) { // if it is not in unixEpoch then convert it to it
			long d = 116444736000000000L;
			dateNumeric = (dateNumeric - d)/10000000;
		}
		return convertNumericToDate(dateNumeric);
	}
	
	/**
	 * Prints out the hex of a ByteBuffer
	 * @param buf
	 */
	public static void printHex(ByteBuffer buf) {
		printHex(buf.array());
	}
	
	/**
	 * Prints out the hex of a byte array
	 * @param bytes
	 */
	public static void printHex(byte[] bytes) {
		for(int i = 0; i < bytes.length; i++) {
			System.out.format("%02X", bytes[i]);
		}
		System.out.println();
	}
	
	/**
	 * Takes a byte array and transforms it to decimal
	 * @param b
	 */
	public static int byteArrayToDecimalInTwosComplement(byte[] b) {
		byte[] tmp = new byte[b.length];
		// convert to Little Endian
		int j = 0;
		for(int i = (b.length - 1); i >= 0; i--) {
			tmp[j] = b[i];
			j++;
		}
		b = tmp;
		
		// create a two's complement number from the byte array
		BigInteger bi = new BigInteger(b);
		
		return bi.intValue();
	}
	
	public static void printDivider() {
		System.out.println("------------------------------------------------------------------");
	}
}
