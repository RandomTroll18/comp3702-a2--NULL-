package main;

import java.util.*;

import solver.OrderingAgent;
import solver.State;
import solver.Action;
import solver.Consumption;
import problem.*;

/**
 * Agent that uses Value Iteration to generate an optimal policy
 */
public class ValueIterationAgent implements OrderingAgent {
	
	private final int MAX_GENERATION_TIME = 60; // Amount of time (in sec) allowed for generation
	private ProblemSpec spec = new ProblemSpec(); // The problem spec
	private Fridge fridge; // The fridge
    private List<Matrix> probabilities; // The probabilities
    private Set<State> possibleStates; // All the possible states
    private Set<Action> possibleActions; // All possible actions
    private Set<Consumption> possibleConsumptions; // All possible consumptions
    private Map<State, Action> policy; // The policy
	
	/**
	 * Constructor
	 * @param spec The problem spec
	 */
	public ValueIterationAgent(ProblemSpec spec) {
		this.spec = spec;
		this.fridge = spec.getFridge();
	    this.probabilities = spec.getProbabilities();
	    
	    this.possibleStates = new HashSet<State>();
	    this.possibleActions = new HashSet<Action>();
	    this.possibleConsumptions = new HashSet<Consumption>();
	    this.policy = new HashMap<State, Action>();
	}
	
	/**
	 * Generate all possible consumptions
	 */
	private void generateAllPossibleConsumptions() {
		double startTime, endTime; // Timer for generating states
		Map<Integer, Integer> consumption; // Action 
		int maxSum = this.fridge.getMaxItemsPerType() * this.fridge.getMaxTypes(); // The maximum amount we can purchase
		int maxIndex = this.fridge.getMaxTypes() - 1; // The maximum index of an action
		
		System.out.println("Generating all possible consumptions");
		startTime = endTime = Global.currentTime(); /* Set the time */
		consumption = new HashMap<Integer, Integer>();
		
		/* Generate zero case */
		for (int i = 0; i < (maxIndex + 1); ++i) {
			consumption.put(i, 0);
		}
		this.possibleActions.add(new Action(consumption));
		System.out.println("Size of consumption: " + consumption.size());
		
		while ((endTime - startTime) < 60) {
			if (this.fridge.getMaxTypes() <= 0) {
				System.err.println("Invalid number of item types");
				System.exit(10);
			} else if (this.fridge.getMaxTypes() == 1) { // Only have 1 type of item
				break;
			} else { // Fridge has 2 or more types of items
				for (int i = maxSum; i >= 0; --i) {
					for (int j = 0; j < (maxIndex + 1); ++j) {
						recursiveGeneration(2, maxSum, i, j, consumption, this.fridge.getMaxItemsPerType());
						for (int k = 0; k < (maxIndex + 1); ++k) {  // Reset list
							consumption.put(k, 0);
						}
					}
					endTime = Global.currentTime();
					if ((endTime - startTime) >= 60) {
						System.err.println("Too long. Stop");
						break;
					}
				}	
			}
			endTime = Global.currentTime(); // Get current time
			break;
		}
	}
	
	/**
	 * Generate all possible actions
	 */
	private void generateAllPossibleActions() {
		double startTime, endTime; // Timer for generating states
		Map<Integer, Integer> action; // Action 
		int maxSum = this.fridge.getMaxPurchase(); // The maximum amount we can purchase
		int maxIndex = this.fridge.getMaxTypes() - 1; // The maximum index of an action
		
		System.out.println("Generating all possible actions");
		startTime = endTime = Global.currentTime(); /* Set the time */
		action = new HashMap<Integer, Integer>();
		
		/* Generate zero case */
		for (int i = 0; i < (maxIndex + 1); ++i) {
			action.put(i, 0);
		}
		this.possibleActions.add(new Action(action));
		System.out.println("Size of action: " + action.size());
		
		while ((endTime - startTime) < 60) {
			if (this.fridge.getMaxTypes() <= 0) {
				System.err.println("Invalid number of item types");
				System.exit(10);
			} else if (this.fridge.getMaxTypes() == 1) { // Only have 1 type of item
				break;
			} else { // Fridge has 2 or more types of items
				for (int i = maxSum; i >= 0; --i) {
					for (int j = 0; j < (maxIndex + 1); ++j) {
						recursiveGeneration(1, maxSum, i, j, action, this.fridge.getMaxPurchase());
						for (int k = 0; k < (maxIndex + 1); ++k) {  // Reset list
							action.put(k, 0);
						}
					}
					endTime = Global.currentTime();
					if ((endTime - startTime) >= 60) {
						break;
					}
				}	
			}
			endTime = Global.currentTime(); // Get current time
			break;
		}
	}
	
	/**
	 * Recursively generate list based on given values
	 * 
	 * @param int generateType - The type we are currently generating
	 * @param int maxSum - The maximum sum
	 * @param int remaining - The remaining value
	 * @param int currentIndex - The current index we are looking at
	 * @param Map<Integer, Integer> currentMap - Current map being modified
	 * @param int maxVal - The maximum value
	 */
	private void recursiveGeneration (int generateType, int maxSum, int remaining, int currentIndex, Map<Integer, Integer> currentMap, int maxVal) {
		int localCurrentIndex = currentIndex; // The current index
		
		if (remaining < 0 || currentIndex > (this.fridge.getMaxTypes() - 1)) {
			addToList(generateType, currentMap);
			return;
		} else {
			for (int i = remaining; i >= 0; --i) {
				if (i >= maxVal) { // Too much remaining
					currentMap.put(currentIndex, maxVal);
					addToList(generateType, currentMap);
					recursiveGeneration(generateType, maxSum - maxVal, maxSum - maxVal, localCurrentIndex + 1, currentMap, maxVal);
				} else if (i == 0 && maxSum == 0) { // We are done
					clearFromCurrent(currentIndex, currentMap);
					addToList(generateType, currentMap);
					return;
				} else { // Just the right amount
					currentMap.put(currentIndex, i);
					addToList(generateType, currentMap);
					recursiveGeneration(generateType, maxSum - i, maxSum - i, localCurrentIndex + 1, currentMap, maxVal);
				}
			}
		}
	}
	
	/**
	 * Set to 0 everything from index current to size of 
	 * map
	 * 
	 * @param int current - The current index
	 * @param Map<Integer, Integer> mapToClear - The map to clear
	 */
	private void clearFromCurrent(int current, Map<Integer, Integer> mapToClear) {
		for (int i = current; i < mapToClear.size(); ++i) {
			mapToClear.put(i, 0);
		}
	}
	
	/**
	 * Add the given map to the respective list
	 * 
	 * @param int generateType - The type of item to add
	 * @param Map<Integer, Integer> toAdd - The map to add
	 */
	void addToList (int generateType, Map<Integer, Integer> toAdd) {
		/* Containers for various types */
		State stateContainer;
		Action actionContainer;
		Consumption consumptionContainer;
		
		switch (generateType) {
		case 0:
			stateContainer = new State(toAdd);
			if (!this.possibleStates.contains(stateContainer)) {
				this.possibleStates.add(stateContainer);
			}
			break;
		case 1:
			actionContainer = new Action(toAdd);
			if (!this.possibleActions.contains(actionContainer)) {
				this.possibleActions.add(actionContainer);
			}
			break;
		case 2:
			consumptionContainer = new Consumption(toAdd);
			if (!this.possibleConsumptions.contains(consumptionContainer)) {
				this.possibleConsumptions.add(consumptionContainer);
			}
			break;
		default:
			System.err.println("Invalid generation type");
			System.exit(13);
		}
	}
	
	/**
	 * Generate all the possible states
	 */
	private void generateAllPossibleStates() {
		//TODO
		double startTime, endTime, timeTaken; // Timer for generating states
		Map<Integer, Integer> state; // Action 
		int maxSum = this.fridge.getCapacity(); //TODO The maximum amount we can have in a state
		int maxIndex = this.fridge.getMaxTypes() - 1; // The maximum index of a state
		
		System.out.println("Generating all possible states");
		startTime = endTime = Global.currentTime(); /* Set the time */
		state = new HashMap<Integer, Integer>();
		
		/* Generate zero case */
		for (int i = 0; i < (maxIndex + 1); ++i) {
			state.put(i, 0);
		}
		this.possibleStates.add(new State(state));
		System.out.println("Size of state: " + state.size());
		
		while ((timeTaken = (endTime - startTime)) < 60) {
			if (this.fridge.getMaxTypes() <= 0) {
				System.exit(10);
			} else if (this.fridge.getMaxTypes() == 1) { // Only have 1 type of item
				break;
			} else { // Fridge has 2 or more types of items
				for (int i = maxSum; i >= 0; --i) {
					for (int j = 0; j < (maxIndex + 1); ++j) {
						recursiveGeneration(0, maxSum, i, j, state, this.fridge.getMaxItemsPerType());
						for (int k = 0; k < (maxIndex + 1); ++k) {  // Reset list
							state.put(k, 0);
						}
					}
					endTime = Global.currentTime();
					if ((timeTaken = (endTime - startTime)) >= 60) {
						break;
					}
				}	
			}
			endTime = Global.currentTime(); // Get current time
			break;
		}
	}
	
	/**
	 * Print out important values
	 * 
	 * @param mode - Indicates the type of values to print out.
	 */
	void printImportantValues (int mode) {
		Set<?> toPrint = null; // The set to print
		int currentIndex = 0; // The current index
		
		switch (mode) {
		case 0: // States
			System.err.println("Printing out states");
			toPrint = this.possibleStates;
			break;
		case 1: // Actions
			System.err.println("Printing out actions");
			toPrint = this.possibleActions;
			break;
		case 2: // Consumption
			System.err.println("Printing out consumptions");
			toPrint = this.possibleConsumptions;
			break;
		default: // Invalid mode
			System.err.println("Invalid mode for printing");
			System.exit(100);
		}
		
		if (toPrint == null) {
			System.err.println("Something really horrible went wrong");
			System.exit(150);
		}
		for (Object current : toPrint) {
			System.out.println("Value: " + (currentIndex + 1) + current.toString());
			currentIndex++;
		}
	}
	
	/**
	 * Find the optimal policy
	 */
	private void findOptimalPolicy () {
		// Need to find optimal policy here. Steps are as follows:
		/*
		 * Need to figure out matrices with probabilities (already have it)
		 * Need to work out value function (which initially, something close to 0)
		 */
	}
	
	/** Interface methods */
	
	public void doOfflineComputation() {
		generateAllPossibleStates();
		generateAllPossibleActions();
		generateAllPossibleConsumptions();
		System.err.println("Done generating");
		System.err.println("Number of States: " + this.possibleStates.size());
		System.err.println("Number of Actions: " + this.possibleActions.size());
		System.err.println("Number of Consumptions: " + this.possibleConsumptions.size());
		//printImportantValues(2);
		/*
		 * TODO: Value function (states) - the total amount of items
		 * TODO: Reward function (states) - capacity - total amount of items
		 * TODO: For every state, check each valid action
		 * TODO: New value function = sumof(probabibility of one type of food * discount factor * 
		 * value(s'))
		 * New value function = list of values for each item
		 * Actual new value = sum of that list
		 * Pick the largest value
		 * TODO: Keep calculating new value function, until difference of previous value function is 
		 * miniscule
		 * TODO make sure purched amount does not exceed capacity
		 * TODO endstate - make sure that the amount that the user ate does not  exceed the maximum for the type
		 * Pseudo-code
		 * 
		 * while (still have time - <= 5 minutes)
		 * Map<Action, double> differentValues
		 * foreach (State current : possibleStates)
		 * 		foreach (Action currentAction : possibleActions) - if invalid action - continue
		 * 			differentValues.set(currentAction, valueGeneration(current, currentAction)
		 * 		current.value = differentValues.maxKey;
		 * 		policy.set(current, differentValues.maxKey)
		 */
		double startTime, currentTime;
		startTime = Global.currentTime();
		
		
		
		currentTime = Global.currentTime();
			while ((currentTime - startTime) <= 300 ) {
				for(State currentState: possibleStates) {
					for(Action currentAction: possibleActions) {
						if (!validAction(currentAction)) {
							continue;
						}
						differentValues.set(currentAction, valueGeneration(currentState, currentAction);
					}
					currentState.getCost() = differentValues.maxKey;
				}
				currentTime = Global.currentTime();
			}
			
		/* valueGeneration (state, action)
		 * 		double maxValue;
		 * 		double currentVal;
		 * 		List<double> currentProb
		 * 		foreach (State possible : possibleStates)
		 * 			currentProb = Transition(state, action, possible);
		 * 			currentVal = sumof(currentProb(i) * reward(possible) + discount factor * value(possible))
		 * 			if (currentVal > maxVal) maxValue = currentVal
		 * 		
		 * 		return maxValue
		 */
		public double valueGeneration(State state, Action action) {
			double maxValue;
			double currentVal;
			List<Double> currentProb = new ArrayList<Double>();
			for (State possible : possibleStates) {
				currentProb = transition(state, action, possible);
				
			}
		} 
		/* Transition (current, action, end)
		 * 		List<double> probs
		 * 		double currentProb
		 * 		Matrix currentMatrix;
		 * 		int row;
		 * 		int column;
		 * 		for (int i = 0; i < current.size(); ++i)
		 * 			currentProb = 0.0;
		 * 			row = current.get(i) + action.get(i);
		 * 			column = (current.get(i) + action.get(i)) - end.get(i);
		 * 			currentMatrix = probabilities.get(i); 
		 *			if (end.get(i) > 0)
		 *				currentProb = currentMatrix.get(row, column);
		 *			else
		 *				for (int j = column; j < currentMatrix.numCols(); ++j)
		 *					currentProb += currentMatrix.get(row, j);
		 *			probs.add(currentProb);
		 *		return probs
		 *
		 *
		 *
		 */
	}
	
	public List<Integer> generateShoppingList(List<Integer> inventory,
	            int numWeeksLeft) {
		// Example code that buys one of each item type.
        // TODO Replace this with your own code.
		List<Integer> shopping = new ArrayList<Integer>();
		int totalItems = 0;
		for (int i : inventory) {
			totalItems += i;
		}
		
		int totalShopping = 0;
		for (int i = 0; i < fridge.getMaxTypes(); i++) {
			if (totalItems >= fridge.getCapacity() || 
			        totalShopping >= fridge.getMaxPurchase()) {
				shopping.add(0);
			} else {
				shopping.add(1);
				totalShopping ++;
				totalItems ++;
			}
		}
		return shopping;
	}
}
