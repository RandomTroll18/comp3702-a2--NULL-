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
	
	/**
	 * Get the total amount consumed
	 * 
	 * @return the total amount consumed
	 */
	public int totalConsumed() {
		int result = 0; // The result
		
		for (int i = 0; i < this.consumption.size(); ++i) {
			result += this.consumption.get(i);
		}
		return result;
	}
	
	/**
	 * Get the map of consumptions
	 * 
	 * @return the map of consumptions
	 */
	public Map<Integer, Integer> getConsumptions () {
		return this.consumption;
	}
	
	
	/* Overriden methods */
	
	@Override
	public int hashCode () {
		return this.consumption.hashCode();
	}
	
	@Override
	public boolean equals (Object obj) {
		Consumption toCompare;
		
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		
		toCompare = (Consumption)obj;
		
		if (!this.consumption.equals(toCompare.getConsumptions())) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString () {
		String toReturn; // The string to return
		toReturn = this.consumption.toString();
		return toReturn;
	}
}
