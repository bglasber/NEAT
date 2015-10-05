package com.glasbergen.neat.examples.connectfour;

import com.glasbergen.neat.NeatParameters;
import com.glasbergen.neat.NeuralNetwork;

public class ConnectFour {

	public static void main(String[] args){
		NeatParameters.POPULATION_SIZE = 150;
		NeatParameters.MUTATION_POWER = 10;
		NeatParameters.UNBRED_CONTENDER_RATE = 1.0;
		NeatParameters.NEW_LINK_RATE = 0.3;
		NeatParameters.NEW_NODE_RATE = 0.5;
		NeatParameters.MAX_GENERATIONS = 100;
		ConnectFourOptimizer opt = new ConnectFourOptimizer();
		NeuralNetwork net = opt.runAllGenerations(200);
		Board b = new Board();
		ConnectFourFitnessEvaluator.playGame(net, net, b);
		b.dumpAsciiBoard();
		
	}
}
