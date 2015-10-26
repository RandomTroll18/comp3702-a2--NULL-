package solver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * This class represents a state that a fridge can be in.
 * @author TMillward
 *
 */
public class State {
	private double cost;
	private Map<State, Integer> adjacentRewards;
	private Set<State> successors;
	private State parent;
	private Map<Integer,Integer> state; //mapping of item to number of that item in state
	
	/**
	 * Constructor
	 * @param cost
	 * @param parent
	 * @param state
	 * @param successors
	 * @param adjRewards
	 */
	public State (double cost, State parent, HashMap<Integer, Integer> state, HashSet<State> successors, HashMap<State, Integer> adjRewards) {
		this.cost = cost;
		this.parent = parent;
		this.state = new HashMap<Integer,Integer>(state);
		this.successors = successors;
		this.adjacentRewards = adjRewards;
	}
	
	public State(Map<Integer, Integer> currentList) {
		this.state = new HashMap<Integer,Integer>(currentList);
		
	}
	
	
	/*
	 * Setters
	 */
	public void setRewards(HashMap<State, Integer> rewards) {
		this.adjacentRewards = rewards;
	}
		
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public void setSuccessors(HashSet<State> succ) {
		this.successors = succ;
	}
	
	public void setParents(State parent) {
		this.parent = parent;
	}
	
	
	/*
	 * Getters
	 */
	public Map<State, Integer> getRewards() {
		return this.adjacentRewards;
	}
	
	
	
	public double getCost(){
		return this.cost; 
	}
	
	public Set<State> getSuccessors() {
		return this.successors; //who needs security when you can save one cpu cycle instead?
	}
	
	public State getParent() {
		return this.parent;
	}
		
	public Map<Integer, Integer> getState() {
		return this.state;
	}
	
	@Override
	public int hashCode() {
		return (17 + (state.hashCode() * 31));
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		if (this.state.equals(((State)obj).getState())) {
			return true;
		} 
		
		return false;
	}
	
	@Override
	public String toString() {
		return this.state.toString();
	}
	
}
