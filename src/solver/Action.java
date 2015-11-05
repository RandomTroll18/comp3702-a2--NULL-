package solver;

import java.util.*;

/**
 * The class representing the action taken by the fridge (purchases)
 */
public class Action {

	private Map<Integer, Integer> purchases; // The purchases
	
	/**
	 * Constructor
	 * 
	 * @param HashMap<Integer, Integer> newPurchaseList - new list of purchases
	 */
	public Action (Map<Integer, Integer> newPurchaseList) {
		this.purchases = new HashMap<Integer, Integer>(newPurchaseList);
	}
	
	/**
	 * Get the map of purchases
	 * 
	 * @return the map of purchases in this class
	 */
	public Map<Integer, Integer> getPurchases () {
		return this.purchases;
	}
	
	/**
	 * Get the amount purchased for a particular item
	 * 
	 * @param int itemType - The type of item
	 * 
	 * @return the amount purchased for the item at index itemType. 
	 * 			-1 if invalid item type
	 */
	public int getAmountPurchased (int itemType) {
		if (itemType >= this.purchases.size()) {
			return -1;
		}
		return this.purchases.get(itemType);
	}
	
	/**
	 * Get the total amount purchased
	 * 
	 * @return the total amount purchased
	 */
	public int totalConsumed() {
		int result = 0; // The result
		
		for (int i = 0; i < this.purchases.size(); ++i) {
			result += this.purchases.get(i);
		}
		return result;
	}
	
	@Override
	public String toString () {
		String toReturn; // The string to return
		toReturn = this.purchases.toString();
		return toReturn;
	}
	
	@Override
	public int hashCode () {
		return this.purchases.hashCode();
	}
	
	@Override
	public boolean equals (Object obj) {
		Action toCompare;
		
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		
		toCompare = (Action)obj;
		
		if (!this.purchases.equals(toCompare.getPurchases())) {
			return false;
		}
		
		return true;
	}
}
