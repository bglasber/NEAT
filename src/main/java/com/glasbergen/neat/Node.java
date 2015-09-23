package com.glasbergen.neat;

import java.util.HashMap;
import java.util.Map;

public class Node {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id != other.id)
			return false;
		return true;
	}

	private int id;
	private double result;
	private Map<Node, Double> dependencies;
	
	public Node(int id){
		this.id = id;
		dependencies = new HashMap<>();
	}

	/**	
	 * Return a list of all other nodes which must be evaluated before we can evaluate this one
	 * @return
	 */
	public void setDependency(Node node, Double weight){
		dependencies.put(node, weight);
	}
	public Map<Node, Double> getAllDependencies(){
		return dependencies;
	}

	public void computeResult(){
		result = 0;
		for( Node n : dependencies.keySet() ){
			result = result + n.getResult() * dependencies.get(n);
		}
		result = MathTools.sigmoid(result);
	}

	public void setResult(double d) {
		result = d;
	}

	public double getResult(){
		return result;
	}

	public void perturbDependencies() {
		for( Double d : dependencies.values() ){
			//90% chance of being uniformly perturbed
			if( MathTools.getPercent() <= 0.9 ){
				d = d + 20 * MathTools.getUniformCenteredAtZero();
			} else {
				d = MathTools.getRandDouble();
			}	
		}
	}

	public int getId() {
		return id;
	}

}
