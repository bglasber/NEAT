package com.glasbergen.neat.examples.connectfour;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;

import com.glasbergen.neat.NeatParameters;
import com.glasbergen.neat.NeuralNetwork;
import com.glasbergen.neat.Node;

import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class ConnectFour {

	public static void main(String[] args) throws IOException{
		NeatParameters.POPULATION_SIZE = 150;
		NeatParameters.MUTATION_POWER = 10;
		NeatParameters.UNBRED_CONTENDER_RATE = 1.0;
		NeatParameters.NEW_LINK_RATE = 0.3;
		NeatParameters.NEW_NODE_RATE = 0.5;
		NeatParameters.MAX_GENERATIONS = 200;
		ConnectFourOptimizer opt = new ConnectFourOptimizer();
		NeuralNetwork net = opt.runAllGenerations(200);
		Board b = new Board();
		ConnectFourFitnessEvaluator.playGame(net, net, b);
		b.dumpAsciiBoard();
		DirectedSparseGraph<Integer,Double> g = new DirectedSparseGraph<>();
		int highestId = 0;
		for(Node n : net.getNetworkNodes()){
			g.addVertex(n.getId());
			if( n.getId() > highestId ){
				highestId = n.getId();
			}
			for( Node dep : n.getAllDependencies().keySet()){
				g.addEdge(n.getAllDependencies().get(dep), dep.getId(), n.getId());
			}
		}
		highestId = highestId+1;
		for(Node n : net.getOutputNodes()){
			g.addVertex(n.getId()+highestId);
			for( Node dep : n.getAllDependencies().keySet()){
				g.addEdge(n.getAllDependencies().get(dep), dep.getId(), n.getId()+highestId);
			}
		}
		DAGLayout<Integer, Double> layout = new DAGLayout<>(g);
		layout.setSize(new Dimension(1920,1080));
		BasicVisualizationServer<Integer, Double> bvs = new BasicVisualizationServer<>(layout);
		bvs.setPreferredSize(new Dimension(1920,1080));
		JFrame frame = new JFrame("Neural Network");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(bvs);
		frame.pack();
		frame.setVisible(true);
	}
}
