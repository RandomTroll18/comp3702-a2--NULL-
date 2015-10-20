package solver;

import java.util.HashMap;
import java.util.HashSet;


/**
 * This class represents a state that a fridge can be in.
 * @author TMillward
 *
 */
public class State {
	private double cost;
	private HashMap<State, Integer> adjacentRewards;
	private HashSet<State> successors;
	private State parent;
	private HashMap<Integer,Integer> state; //mapping of item to number of that item in state
	
	public State (double cost, State parent, HashMap<Integer, Integer> state, HashSet<State> successors, HashMap<State, Integer> adjRewards) {
		this.cost = cost;
		this.parent = parent;
		this.state = state;
		this.successors = successors;
		this.adjacentRewards = adjRewards;
	}
	
	public HashMap<State, Integer> getRewards() {
		return this.adjacentRewards;
	}
	
	public double getCost(){
		return this.cost; 
	}
	
	public HashSet<State> getSuccessors() {
		return this.successors; //who needs security when you can save one cpu cycle instead?
	}
	
	public State getParent() {
		return this.parent;
	}
		
	public HashMap<Integer, Integer> getState() {
		return this.state;
	}
	
	@Override
	public int hashCode() {
		return (parent.hashCode() * 3 + successors.hashCode()) * 17 + (state.hashCode() * 31);
	}
	
	@Override
	public boolean equals(Object obj) {
		State ob = (State) obj;
		if (this.cost == ob.getCost() && this.successors.equals(ob.getSuccessors()) && this.parent.equals(ob.getParent()) && this.state.equals(ob.getState())) {
			return true;
		}
		
		return false;
	}
	
}
