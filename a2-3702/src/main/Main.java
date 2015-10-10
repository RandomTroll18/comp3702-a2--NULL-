package main;

import java.io.File;

/**
 * COMP3702 Assignment 2
 * Student Names: Joshua Rillera, Thomas Millward
 * Student Numbers (respectively): 43150621, 43236945
 * Date Due: 11:59 PM, 17th of October, 2015
 * 
 * Class that will run everything
 */
public class Main {
	
	/**
	 * Check that input file exists, isn't a directory and can be read from
	 * 
	 * @param inputFilePath - The string containing input file path
	 * @return true if the file exists and can be read from
	 * 			false if the file doesn't exist or cannot be read
	 */
	private static boolean checkInputFile (String inputFilePath) {
		File file = new File(inputFilePath); // File object
		if (!file.exists() || !file.canRead() || !file.isFile()) return false;
		return true;
	}
	
	/**
	 * Check if output file exists. If it does exist, check that we can 
	 * write to it.
	 * 
	 * @param outputFilePath - The string containing output file path
	 * @return true if the output file can be written to (if it exists) 
	 * 			or if the output file does not exist
	 * 			false if the file cannot be written to (if it does exist)
	 */
	private static boolean checkOutputFile (String outputFilePath) {
		File file = new File(outputFilePath); // File object
		if (!file.exists()) return true; // File doesn't exist
		else if (file.exists()) {
			if (file.isFile() && file.canWrite()) return true;
		}
		return false;
	}
	
	/**
	 * Checks the given command line arguments
	 * 
	 * @param args - The command line arguments
	 * @return true if cmd args are valid, false if otherwise
	 */
	private static boolean checkArguments (String []args) {
		if (args.length != 2) {
			System.err.println("Not enough args");
			return false;
		} else if (!checkInputFile(args[0])) {
			System.err.println("Input File cannot be used");
			return false;
		} else if (!checkOutputFile(args[1])) {
			System.err.println("Output File exists but can't be written to");
			return false;
		}
		return true;
	}

	/**
	 * Automatically create a weekly shopping order to minimize the number 
	 * of times the fridge fails to provide items to users
	 */
	public static void main (String []args) {
		WorkSpace workSpace; // The workspace that we will solve the problem in
		Global.initializeTimer(300); // 5 minutes
		
		if (!checkArguments(args)) return;
		
		workSpace = new WorkSpace(args[0], args[1]);
		if (workSpace.solve()) {
			System.out.println("Solved in time");
		} else {
			System.out.println("Not solved in time");
		}
		
		Global.setEndTime();
		Global.checkTime();
	}
	
}
