package solver;

import java.util.*;

/**
 * Class representing the amount that the user 
 * wants to consume
 */
public class Consumption {
	private Map<Integer, Integer> consumption; // The amount being consumed
	
	/**
	 * Constructor
	 * 
	 * @param newConsumption - The new list of consumptions
	 */
	public Consumption (Map<Integer, Integer> newConsumption) {
		this.consumption = new HashMap<Integer, Integer>(newConsumption);
	}
	
	/**
	 * Get the amount consumed for a particular item type
	 * 
	 * @param int itemType - The item type
	 * @return the amount consumed for item at index itemType.
	 * 			-1 if index is invalid
	 */
	public int getAmountConsumed (int itemType) {
		if (itemType >= this.consumption.size()) {
			return -1;
		}
		return this.consumption.get(itemType);
	}
}
