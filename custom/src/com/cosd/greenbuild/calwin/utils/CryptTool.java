package com.cosd.greenbuild.calwin.utils;

/**
 * Encrypts a given string.  This is weak encryption, but better than plain text.
 * <p/>
 * Usage: java -cp cdcr-tools.jar com.cdcr.soms.utils.CryptTool <apass>
 * <p/>
 * It uses the same mechanism as --encrypt for tools that extend Application
 *
 * @author Andy.Taylor
 *
 */
public class CryptTool {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length==0) {
			System.err.println("Expected argument");
			System.exit(0);
		}
		//System.out.println("NOTE: this is not strong encryption and should not be relied upon as such.");
		//System.out.println(Base64.encodeToString(args[0].getBytes(), false));
	}

}
