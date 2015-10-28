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
	private final double MAX_TOTAL_TIME = 300; // The max amount of time allowed (in seconds)
	private double timeRemaining; // The amount of time remaining
	private ProblemSpec spec = new ProblemSpec(); // The problem spec
	private Fridge fridge; // The fridge
    private List<Matrix> probabilities; // The probabilities
    private Set<State> possibleStates; // All the possible states
    private Set<Action> possibleActions; // All possible actions
    private Set<Consumption> possibleConsumptions; // All possible consumptions
    private Map<State, Action> policy; // The policy
    private boolean calculated = false; // Record if we've already calculated our policies
	
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
	    
	    this.timeRemaining = this.MAX_TOTAL_TIME;
	}
	
	/**
	 * Generate all the possible states
	 */
	private void generateAllPossibleStates() {
		//TODO
		double startTime, endTime; // Timer for generating states
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
		
		while ((endTime - startTime) < this.MAX_GENERATION_TIME) {
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
					if ((endTime - startTime) >= this.MAX_GENERATION_TIME) {
						break;
					}
				}	
			}
			endTime = Global.currentTime(); // Get current time
			this.timeRemaining -= (endTime - startTime);
			System.out.println("Time remaining: " + this.timeRemaining);
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
		
		while ((endTime - startTime) < this.MAX_GENERATION_TIME) {
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
					if ((endTime - startTime) >= this.MAX_GENERATION_TIME) {
						break;
					}
				}	
			}
			endTime = Global.currentTime(); // Get current time
			this.timeRemaining -= (endTime - startTime);
			System.out.println("Time remaining: " + this.timeRemaining);
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
	private void addToList (int generateType, Map<Integer, Integer> toAdd) {
		/* Containers for various types */
		State stateContainer;
		Action actionContainer;
		Consumption consumptionContainer;
		
		switch (generateType) {
		case 0:
			stateContainer = new State(toAdd);
			if (!this.possibleStates.contains(stateContainer)) {
				stateContainer.setCost(sumOf(toAdd));
				this.policy.put(stateContainer, null);
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
	 * Print out important values
	 * 
	 * @param mode - Indicates the type of values to print out.
	 */
	private void printImportantValues (int mode) {
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
		case 3: // Policies
			for (State current : this.policy.keySet()) {
				System.out.println("State " + (currentIndex + 1) + ": "
						+ current.getState().toString() + ". Do Action: "
						+ this.policy.get(current).getPurchases().toString());
				currentIndex++;
			}
			return;
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
	 * Find out the maximum value for a given map of actions
	 * 
	 * @param actions - The map of actions to their values
	 * @return Return the action with the largest value out of all the actions
	 */
	private Action maxArg(Map<Action, Double> actions) {
		Action largestValue = null; // The largest value
		
		for (Action current : actions.keySet()) {
			if (largestValue == null || (actions.get(largestValue) < actions.get(current))) {
				largestValue = current;
			}
		}
		
		return largestValue;
	}
	
	/**
	 * Value generation
	 * 
	 * @param state - The current state
	 * @param action - The current action
	 * @param Set<State> lookup - The set of states we are looking up
	 * @return The total value for that state
	 */
	private Double valueGeneration(State state, Action action, Set<State> lookup) {
		Double maxValue = null; // The maximum value
		double currentVal = 0.0; // The current value
		List<Double> currentProb = new ArrayList<Double>();
		for (State possible : this.possibleStates) {
			currentProb = transition(state, action, possible);
			for (int i = 0; i < currentProb.size(); ++i) {
				currentVal += currentProb.get(i) 
							* (reward(possible) + this.spec.getDiscountFactor() * possible.getCost());
			}
			if (maxValue == null) { // First time generating value
				maxValue = currentVal;
			} else { // Need to sum up these values
				maxValue += currentVal;
			}
		}
		return maxValue;
	} 
		
		
	/**
	 * Reward function for a state. Currently, our reward 
	 * is simply the total number of items in the state minus 
	 * the capacity of the fridge.
	 * 
	 * This means that we get the highest reward if we 
	 * fully stock our fridge
	 * 
	 * @param current - The current state
	 * @return The immediate reward for being in this state
	 */
	private double reward (State current) {
		int total = sumOf(current.getState()); // The total number of items
		
		return total - this.fridge.getCapacity();
	}
	
	/**
	 * Check if the end state is valid. This means check that 
	 * the difference between the sums of both states are not 
	 * greater than total number of items in fridge * maximum 
	 * consumption of one item.
	 * 
	 * Will also check if the difference between the contents 
	 * of both states for each item is not greater than the maximum
	 * consumption
	 * 
	 * @param currentState The current state
	 * @param endState The end state
	 * @return true if the end state is valid. false otherwise
	 */
	private boolean IsValidEndState (State currentState, State endState) {
		/* The sums */
		int currentSum = sumOf(currentState.getState());
		int endSum = sumOf(endState.getState());
		
		if (Math.abs(currentSum - endSum) > (this.fridge.getMaxTypes() * this.fridge.getMaxItemsPerType())) {
			return false;
		}
		
		for (int i = 0; i < currentState.getState().size(); ++i) {
			if (Math.abs(currentSum - endSum) > this.fridge.getMaxItemsPerType()) {
				return false;
			}
		}
		
		return true;
	}
		
		
	/**
	 * Transition function. Currently, we only get the list of 
	 * probabilities of each item.
	 * 
	 * @param current - The current state
	 * @param action - The action
	 * @param possible - The possible state
	 * @return The list of probabilities where each index refers to 
	 * the probability of that item
	 */
	private List<Double> transition (State current, Action action, State possible) {
		List<Double> probs = new ArrayList<Double>(); // Probabilities
		Map<Integer, Integer> currentStock, purchase, possibleStock; // Maps
		double currentProb; // The current probability
		Matrix currentMatrix; // The current probability matrix
		int row, column; // The row and column
		
		for (int i = 0; i < current.getState().size(); ++i) {
			currentProb = 0.0;
			currentStock = current.getState();
			purchase = action.getPurchases();
			possibleStock = possible.getState();
			row = currentStock.get(i) + purchase.get(i);
			column = row - possible.getState().get(i);
			currentMatrix = this.probabilities.get(i);
			if (column < 0 || column >= currentMatrix.getNumCols()
						|| row >= currentMatrix.getNumRows()) { // Invalid state
				probs.add(0.0);
				continue;
			}
			if (possibleStock.get(i) > 0 || 
						(possibleStock.get(i) == 0 && column == 0)) { // Sufficiently provided
				currentProb = currentMatrix.get(row, column);
			} else if (possibleStock.get(i) == 0 && column > 0) { 
				// Range of probabilities because user could have eaten plenty
				for (int j = column; j < currentMatrix.getNumCols(); ++j) {
					currentProb += currentMatrix.get(row, j);
				}
			}
			probs.add(currentProb);
		}
		return probs;
	}
	
	/**
	 * Check if the given action is valid or not. This means 
	 * check if the current action causes us to over-stock our fridge
	 * 
	 * @param currentAction - The action we are looking at
	 * @param currentState - The state we are looking at
	 * @return true if currentAction leads to state having more than the 
	 * capacity of the fridge. false otherwise
	 */
	private boolean validAction (Action currentAction, State currentState) {
		int sumOfAction = sumOf(currentAction.getPurchases());
		int sumOfState = sumOf(currentState.getState());
		
		if ((sumOfAction + sumOfState) > fridge.getCapacity()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Return the sum of the integers in the list
	 * 
	 * @param integerMap - The map of integers
	 * @return - the sum of integers in the list
	 */
	private int sumOf(Map<Integer, Integer> integerList) {
		int sum = 0; // The sum
		for (Integer current : integerList.keySet()) {
			sum += integerList.get(current);
		}
		return sum;
	}
	
	/**
	 * Check if the given action and state is a better policy
	 * 
	 * @param current - The current state
	 * @param newActionValue - The value of the new action
	 * @return true if we have a better policy. False otherwise
	 */
	private boolean isBetterPolicy(State current, Double newActionValue) {
		if (!this.policy.keySet().contains(current)) { // No policy set for this yet
			return true;
		}
		
		if (current.getCost() <= newActionValue) { // New action has better cost
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the difference between the values of the 
	 * previous policy and the new policy are <= val.
	 * 
	 * Also reassigns the old policy
	 * 
	 * @param double val - The difference
	 * @param Map<State, Action> newPolicy - The new policy
	 * @return true if the minimum difference > val. false otherwise
	 */
	private boolean CheckDifference (double val, Map<State, Action> newPolicy) {
		Double currDiff, minDiff = null; // The minimum and current difference
		if (this.policy.size() != this.possibleStates.size()) { // Previous policy wasn't assigned yet
			return true;
		}
		
		for (State current : newPolicy.keySet()) {
			for (State old : this.policy.keySet()) {
				if (current.equals(old)) {
					currDiff = Math.abs(current.getTemporaryCost() - old.getCost());
					if (minDiff == null || currDiff <= minDiff) {
						minDiff = currDiff;
					}
					break;
				}
			}
		}
		
		if (minDiff > val) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Make a copy of a policy
	 * 
	 * @param Map<State, Action> newPolicy - The new policy
	 */
	private void copyPolicy(Map<State, Action> newPolicy) {
		/* Containers for clones */
		State current;
		Action currentAction;
		
		this.policy = new HashMap<State, Action>();
		
		for (State newState : newPolicy.keySet()) {
			current = new State(newState.getState());
			current.setCost(newState.getTemporaryCost());
			currentAction = new Action(newPolicy.get(newState).getPurchases());
			this.policy.put(current, currentAction);
		}
	}
	
	/**
	 * Generate optimal policy
	 */
	private void generateOptimalPolicy () {
		double startTime, currentTime; // Timer things
		boolean alreadyRunThrough = false; // Record if we've already done one run
		Set<State> toLookup; // The states to lookup
		Map<Action, Double> differentValues = new HashMap<Action, Double>(); // The different values for the current state
		Map<State, Action> newPolicy = new HashMap<State, Action>(); // New policy
		State newState; // New state for new policy
		Action best; // The best action
		
		startTime = Global.currentTime();
		currentTime = Global.currentTime();
		while ((currentTime - startTime) <= this.timeRemaining) {
			if (alreadyRunThrough) {
				toLookup = this.policy.keySet();
			} else {
				toLookup = this.possibleStates;
			}
			for (State currentState: toLookup) {
				differentValues.clear(); // Reset different values
				for (Action currentAction: this.possibleActions) {
					if (!validAction(currentAction, currentState)) {
						continue;
					}
					differentValues.put(currentAction, valueGeneration(currentState, currentAction, toLookup));
				}
				best = maxArg(differentValues);
				if (isBetterPolicy(currentState, differentValues.get(best))) {
					newState = new State(currentState.getState());
					newState.setTemporaryCost(differentValues.get(best));
					newPolicy.put(newState, best);
				}
			}
			if (CheckDifference(0.1, newPolicy)) {
				copyPolicy(newPolicy);
				newPolicy.clear();
				alreadyRunThrough = true;
			} else {
				currentTime = Global.currentTime();
				break;
			}
		}
	}
	
	/** Interface methods */
	
	public void doOfflineComputation() {
		if (this.calculated) { // Already calculated
			return;
		}
		generateAllPossibleStates();
		generateAllPossibleActions();
		generateOptimalPolicy();
		
		this.calculated = true;
	}

	public List<Integer> generateShoppingList(List<Integer> inventory,
	            int numWeeksLeft) {
		State current; // current state
		Map<Integer, Integer> purchases = new HashMap<Integer, Integer>(); // Purchases
		Map<Integer, Integer> map = new HashMap<Integer, Integer>(); // Mapping of integers
		List<Integer> shopping = new ArrayList<Integer>(); // Shopping items
		
		for (int i = 0; i < inventory.size(); ++i) {
			map.put(i, inventory.get(i));
		}
		
		current = new State(map);
		purchases = this.policy.get(current).getPurchases();
		
		for (int i = 0; i < inventory.size(); ++i) {
			shopping.add(purchases.get(i));
		}
		
		return shopping;
	}
}
