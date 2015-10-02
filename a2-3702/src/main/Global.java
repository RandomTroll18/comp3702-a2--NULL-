package main;

/**
 * Class that contains variables and functions to be used by 
 * multiple source files
 */
public class Global {

	// Newline character to be used in strings
	public static final String NEWLINE 
			= System.getProperty("line.separator");
	
	/*
	 * Variables for timing. Need to be static
	 */
	private static long startTime;
	private static long timeLimit;
	private static long endTime = 0;
	private static double timeTaken;
	
	/**
	 * Initialize timing variables
	 * 
	 * @param time - The time limit in seconds
	 */
	public static void initializeTimer (int time) {
		startTime = System.currentTimeMillis();
		timeLimit = startTime + (time * 1000); // The time limit is 60 seconds
	}
	
	/**
	 * Get the start time
	 * 
	 * @return the starting time (in milliseconds)
	 */
	public static long getStartTime () {
		return startTime;
	}
	
	/**
	 * Get the end time
	 * 
	 * @return the end time (in milliseconds)
	 */
	public static long getEndTime () {
		return endTime;
	}
	
	/**
	 * Get the time limit
	 * 
	 * @return the time limit (in milliseconds)
	 */
	public static long getTimeLimit () {
		return timeLimit;
	}
	
	/**
	 * Return whether or not we still have time left
	 * 
	 * @return true if we still have time left. false if otherwise
	 */
	public static boolean stillTimeLeft () {
		long currentTime = System.currentTimeMillis();
		return (currentTime < timeLimit);
	}
	
	/**
	 * Set the end time and sets the 
	 * amount of time taken
	 */
	public static void setEndTime () {
		/* Need to convert time to double */
		double convertStartTime = (double)startTime;
		double convertEndTime;
		endTime = System.currentTimeMillis();
		convertEndTime = (double)endTime;
		
		timeTaken = (convertEndTime - convertStartTime) / 1000;
	}
	
	/**
	 * Get the total time taken (in seconds)
	 * @return the total time taken (in seconds)
	 */
	public static double timeTaken () {
		return timeTaken;
	}
	
	/**
	 * Check the time taken and print appropriate messages
	 */
	public static void checkTime () {
		System.out.println("Time Taken (in seconds): " 
				+ Global.timeTaken() + "s");
		if (timeTaken() <= 60) {
			System.out.println("Still On Time with " 
					+ ((double)60 - timeTaken()) 
					+ " seconds left");
		} else {
			System.out.println("Over-time by: " 
					+ (timeTaken() - (double)60) 
					+ " seconds");
		}
	}
}
