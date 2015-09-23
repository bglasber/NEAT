package com.glasbergen.neat;

public class NodeUtils {

	private static int id = 0;

	/**
	 * Get the next node id
	 * NB: not thread safe
	 * @return
	 */
	public static int getNextId(){
		return id++;
	}

	public static Node getBiasNode() {
		Node bias = new Node(-1);
		bias.setResult(1);
		return bias;
	}
}
