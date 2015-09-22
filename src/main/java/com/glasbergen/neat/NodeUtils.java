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
}
