package com.glasbergen.neat.examples.connectfour;

import com.glasbergen.neat.EvolutionOptimizer;

public class ConnectFourOptimizer extends EvolutionOptimizer {

	public ConnectFourOptimizer() {
		initializeNetworks(6*7*3+1, 6);
	}
	@Override
	public void runCurrentGeneration() {
		//TODO:
		//Have every network play every other network
		//sort by number of wins
		//On IllegalArgument, the machine loses. Pick at random to give the
		//other machine something to play against
	}

}
