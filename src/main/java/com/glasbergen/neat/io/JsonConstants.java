package com.glasbergen.neat.io;

public class JsonConstants {

	public static final String NUM_INPUTS = "num_inputs";
	public static final String NUM_OUTPUTS = "num_outputs";
	public static final String FITNESS = "fitness";
	public static final String SOLUTION_FITNESS = "solution_fitness";
	public static final String MAX_NETWORK_NODE_ID = "max_network_node_id";
	
	public static final String networkNodeString(int id){
		return "node_" + Integer.toString(id) + "_depends";
	}
	
	public static final String outputNodeString(int id){
		return "output_node_" + Integer.toString(id) + "_depends";
	}
}
