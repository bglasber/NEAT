package com.glasbergen.neat;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.anyOf;
import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.not;

import org.junit.Test;

public class MutationTest {

	@Test
	public void testAddNode() {
		
		//1x1 network
		NeuralNetwork network  = new NeuralNetwork(1, 1, new double[]{ 2.0 });
		
		List<Node> nonExitNodes = network.getNetworkNodes();
		assertThat(nonExitNodes.size(), equalTo(1));
		NodeUtils.getNextId();
		network.addNewNode();
		
		//Should now be a hidden node
		nonExitNodes = network.getNetworkNodes();
		assertThat(nonExitNodes.size(), equalTo(2));
		
		//Check that it split the existing connection
		Node addedNode = nonExitNodes.get(1);
		Map<Node, Double> addedNodeDeps = addedNode.getAllDependencies();
		assertThat(addedNodeDeps.size(), equalTo(1));
		assertThat(addedNodeDeps.containsKey(nonExitNodes.get(0)), equalTo(true));
		assertThat(addedNodeDeps.get(nonExitNodes.get(0)), equalTo(2.0));
		
		//Check a new connection was made to the output node with random weight
		Node outputNode = network.getOutputNodes().get(0);
		assertThat(outputNode.getAllDependencies().containsKey(addedNode), equalTo(true));
		assertThat(outputNode.getAllDependencies().containsKey(nonExitNodes.get(0)), equalTo(false));
		assertThat(outputNode.getAllDependencies().get(addedNode), not(equalTo(null)));
	}
	
	@Test
	public void testAddLink(){
		NeuralNetwork network = new NeuralNetwork(1, 1, new double[]{2.0});
		NodeUtils.getNextId();
		//Split one connection into two with hidden node in the middle
		network.addNewNode();
		
		//Should now be a hidden node
		List<Node> nonExitNodes = network.getNetworkNodes();
		assertThat(nonExitNodes.size(), equalTo(2));
		Node addedNode = nonExitNodes.get(1);
		assertThat(addedNode.getAllDependencies().size(), equalTo(1));
		network.dumpNetwork();
		
		//Should add link from input node to output node
		network.addNewLink();
		network.dumpNetwork();
		assertThat(addedNode.getAllDependencies().size(), equalTo(1));
		assertThat(network.getOutputNodes().get(0).getAllDependencies().size(), equalTo(2));
	}
	
	@Test
	public void testAverageWeightsInCrossover(){
		NeuralNetwork network = new NeuralNetwork(1, 1, new double[]{2.0});
		NeuralNetwork other = new NeuralNetwork(1, 1, new double[]{1.0});
		Species spec = new Species(network);
		spec.addNetworkIfSpeciesMatch(other);
		network.setFitness(1.0);
		NeuralNetwork child = network.crossOver(other);
		assertThat( child, not(equalTo(network)) );
		assertThat( child, not(equalTo(other)) );
		assertThat( child.getOutputNodes().get(0).getAllDependencies().values().iterator().next(), anyOf(equalTo(1.0), equalTo(2.0)));	
	}
	
	@Test
	public void maintainMoreFitNetworkStructure(){
		NeuralNetwork network = new NeuralNetwork(1, 1, new double[]{2.0});
		NeuralNetwork other = new NeuralNetwork(1, 1, new double[]{1.0});
		Species spec = new Species(network);
		spec.addNetworkIfSpeciesMatch(other);
		NodeUtils.getNextId();
		network.addNewNode();
		network.setFitness(1.0);
		NeuralNetwork child = network.crossOver(other);
		assertThat( child.getNetworkNodes().size(), equalTo(2) );
	}

}
