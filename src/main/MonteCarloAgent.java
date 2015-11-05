package main;

import java.util.*;

import problem.Fridge;
import problem.Matrix;
import problem.ProblemSpec;
import solver.OrderingAgent;

/**
 * Agent that uses the Monte Carlo Tree Search to generate 
 * an approximate optimal policy
 */
public class MonteCarloAgent implements OrderingAgent {

	private ProblemSpec spec = new ProblemSpec(); // The problem spec
	private Fridge fridge; // The fridge
    private List<Matrix> probabilities; // The probabilities
    
    /**
     * Constructor
     * @param spec The problem spec
     */
    public MonteCarloAgent(ProblemSpec spec) {
    	this.spec = spec;
		fridge = spec.getFridge();
	    probabilities = spec.getProbabilities();
    }
	
	public void doOfflineComputation() {
		
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
