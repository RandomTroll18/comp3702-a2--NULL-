package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import problem.Fridge;
import problem.Matrix;
import problem.ProblemSpec;
import problem.Simulator;
import solver.ShoppingGenerator;
import solver.OrderingAgent;

/**
 * Class that will do all the work to solve the problem
 */
public class WorkSpace {
	
	private final int NUM_SIMULATIONS = 1; // The number of simulations to run
	
	private String outputFileName; // The file to output to
	private String inputFileName; // The file to read from
	private int numberOfWeeks; // The number of weeks to account for
	private double cost; // The cost
	private double discountFactor; // The discount factor
	private Fridge fridge; // The fridge
	private List<Matrix> probabilities; // The probabilities 

	/**
	 * Load the parameters and stochastic model of the user's consumption 
	 * behaviour
	 * 
	 * @param inputFile - The input file
	 * 
	 * @return true if successful. false otherwise
	 */
	private boolean loadInputFile (String inputFile) {
		BufferedReader reader; // The buffered reader
		String line; // The line currently being read
		boolean success = false; // Record whether or not we were successful in reading the file
		int lineNo = 0; // The number of lines that were just read
		Scanner scanner; // A scanner for a particular line
		
		try {
			reader = new BufferedReader(new FileReader(inputFile));
			
			line = reader.readLine();
			lineNo++;
			scanner = new Scanner(line);
			this.numberOfWeeks = scanner.nextInt();
			scanner.close();

			line = reader.readLine();
			lineNo++;
			scanner = new Scanner(line);
			this.cost = scanner.nextDouble();
			scanner.close();
			
			line = reader.readLine();
			lineNo++;
			scanner = new Scanner(line);
			this.discountFactor = scanner.nextDouble();
			scanner.close();
			
			line = reader.readLine();
			lineNo++;
			this.fridge = new Fridge(line.trim().toLowerCase());
			
			this.probabilities = new ArrayList<Matrix>();
			for (int k = 0; k < this.fridge.getMaxTypes(); k++) {
				double[][] data = new double[this.fridge.getCapacity() + 1]
						[this.fridge.getMaxItemsPerType() + 1];
				for (int i = 0; i <= this.fridge.getCapacity(); i++) {
					line = reader.readLine();
					lineNo++;
					double rowSum = 0;
					scanner = new Scanner(line);
					for (int j = 0; j <= this.fridge.getMaxItemsPerType(); j++) {
						data[i][j] = scanner.nextDouble();
						rowSum += data[i][j];
					}
					scanner.close();
					if (Math.round(rowSum * 100000) != 100000) {
						reader.close();
						throw new InputMismatchException(
								"Row probabilities do not sum to 1.");
					}
				}
				this.probabilities.add(new Matrix(data));
			}
			System.out.println("Number of weeks: " + numberOfWeeks);
			System.out.println("Cost: " + cost);
			System.out.println("Discount Factor: " + this.discountFactor);
			System.out.println("Fridge: " + this.fridge.getName());
			System.out.println("Probabilities: " + Global.NEWLINE);
			for (int i = 0; i < this.probabilities.size(); ++i) {
				System.out.println("Matrix " + (i + 1) + ": " 
							+ Global.NEWLINE 
							+ this.probabilities.get(i));
			}
			success = true;
			reader.close();
		} catch (Exception e) {
			success = false; // We failed somehow
			String errorToPrint; // The error to print
			Class<? extends Exception> exceptionClass = e.getClass();
			
			if (exceptionClass.equals(InputMismatchException.class)) {
				errorToPrint = String.format(
						"Invalid number format on line %d: %s", lineNo,
						e.getMessage());
			} else if (exceptionClass.equals(NoSuchElementException.class)) {
				errorToPrint = String.format("Not enough tokens on line %d",
						lineNo);
			} else if (exceptionClass.equals(NullPointerException.class)) {
				errorToPrint = String.format("Line %d expected, but file ended.", 
						lineNo);
			} else {
				errorToPrint = "Unhandled exception in reading input file";
			}
			System.err.println(errorToPrint);
		}
		
		return success;
	}
	
	/**
	 * Solve the problem
	 * 
	 * @return true if solved in time. false otherwise
	 */
	public boolean solve() {
		boolean solved = true; // Record if solved or not
		double startSimulatorTime, endSimulatorTime, timeTaken; // Simulator timer
		double totalPenalty = 0, totalMaxPenalty = 0; // Penalties
		ProblemSpec spec; // The problem spec
		Simulator simulator; // The simulator
		OrderingAgent agent = null; // The agent to run to solve this problem
		
		try {
			spec = new ProblemSpec(inputFileName); // Get the problem spec
			simulator = new Simulator(spec);
			/* Use Value Iteration. No time for Monte Carlo */
			agent = new ValueIterationAgent(spec);
			
			/* 
			 * Run for a number of simulations. Not the same as number of weeks.
			 * This is for the purposes of checking if we are getting this right often, I think 
			 */
			for (int i = 0; i < this.NUM_SIMULATIONS; ++i) { 
				System.out.printf("Run #%d\n", i + 1);
				System.out.println("-----------------------------------------------------------");
				simulator.reset();
				
				/* 
				 * We have 5 minutes for this. We can also decide whether or not to do this.
				 * Refer to Runner.java
				 */
				System.out.println("Doing Offline Computation");
				Global.initializeTimer(300);
				agent.doOfflineComputation();
				Global.setEndTime();
				Global.checkTime();
				for (int j = 0; j < spec.getNumWeeks(); j++) {
					/* We have one minute for shopping */
					startSimulatorTime = Global.currentTime();
					
					List<Integer> shopping = agent.generateShoppingList(
							simulator.getInventory(), spec.getNumWeeks() - (j + 1));
					simulator.simulateStep(shopping);
					
					endSimulatorTime = Global.currentTime();
					timeTaken = endSimulatorTime - startSimulatorTime;
					if (timeTaken > 60) {
						System.err.println("Step not solved in time! Took " 
									+ (timeTaken - 60.0) 
									+ " extra seconds");
						throw new Exception("Ran out of time");
					} else {
						System.out.println("Step solved in time! "
									+ (60.0 - timeTaken)
									+ " seconds left to spare");
					}
				}
				
				totalPenalty += simulator.getTotalPenalty();
				totalMaxPenalty += simulator.getTotalMaxPenalty();
				System.out.println("-----------------------------------------------------------");
			}
			
			solved = true;
			simulator.saveOutput(outputFileName);
			System.out.printf("Summary statistics from %d runs:\n", this.NUM_SIMULATIONS);
			System.out.println();
			System.out.printf("Total penalty: %f\n", -totalPenalty);
			System.out.printf("Total maximum penalty: %f\n", -totalMaxPenalty);
			System.out.printf("Overall penalty ratio: %f\n", totalPenalty / totalMaxPenalty);
		} catch (Exception e) {
			System.err.println("Error found: " + e.getMessage());
			System.err.println("Stack trace: " + e.getStackTrace().toString());
			e.printStackTrace();
			solved = false;
		}
		
		if (solved) { // Need to check if we solved it in time
			if (Global.stillTimeLeft()) {
				return true;
			} else {
				return false;
			}
		} else { // Error occurred
			return false;
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param inputFile - path to input file
	 * @param outputFile - path to output file
	 */
	public WorkSpace (String inputFile, String outputFile) {
		/* Get file names */
		this.outputFileName = outputFile;
		this.inputFileName = inputFile;
		
		if (!loadInputFile(inputFile)) {
			System.err.println("Loading input file failed");
			System.exit(99);
		}
	}
	
}
