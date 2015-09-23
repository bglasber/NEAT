package com.glasbergen.neat;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

import org.junit.Test;

public class SpeciesComparisonTests {

	@Test
	public void testSameNetwork() {
		NeuralNetwork nn1 = new NeuralNetwork(1, 1, new double[]{2.0});
		assertThat(nn1.getNumDisjointGenes(nn1), equalTo(0));
		assertThat(nn1.getNumExcessGenes(nn1), equalTo(0));
		assertThat(nn1.computeAverageWeightDifferences(nn1), equalTo(0.0));
	}

	@Test
	public void testDifferentWeights(){
		NeuralNetwork nn1 = new NeuralNetwork(1, 1, new double[]{2.0});
		NeuralNetwork nn2 = new NeuralNetwork(1, 1, new double[]{1.0});
		assertThat(nn1.getNumExcessGenes(nn2), equalTo(0));
		assertThat(nn1.getNumDisjointGenes(nn2), equalTo(0));
		assertThat(nn1.computeAverageWeightDifferences(nn2), equalTo(1.0));
	}
	
	@Test
	public void testDifferentNetworks(){
		NeuralNetwork nn1 = new NeuralNetwork(1, 1, new double[]{1.0});
		NeuralNetwork nn2 = new NeuralNetwork(1, 1, new double[]{1.0});
		NodeUtils.getNextId(); //increment
		nn1.addNewNode();
		assertThat( nn1.getNumExcessGenes(nn2), equalTo(0) );
		assertThat( nn1.getNumDisjointGenes(nn2), equalTo(3) );
		assertThat( nn2.getNumExcessGenes(nn1), equalTo(1) );
		assertThat( nn2.getNumDisjointGenes(nn1), equalTo(2) );
		Species species = new Species(nn1);
		assertThat( species.computeSimilarity(nn1, nn1), equalTo(0.0) );
		assertThat( species.computeSimilarity(nn1, nn2), equalTo(3.0) );
		species.addNetworkIfSpeciesMatch(nn2); // Shouldn't get added
		assertThat( species.getNumNetworksInSpecies(), equalTo(1) );
		NeuralNetwork nn3 = new NeuralNetwork(1, 1, new double[]{2.0});
		Species species2 = new Species(nn2);
		species2.addNetworkIfSpeciesMatch(nn3);
		assertThat( species2.getNumNetworksInSpecies(), equalTo(2) );
	}
	
	@Test
	public void testClonedNetworks(){
		NeuralNetwork nn1 = new NeuralNetwork(1, 1, new double[]{1.0});
		NodeUtils.getNextId(); //increment
		nn1.addNewNode();
		NeuralNetwork nn2 = nn1.cloneNetwork();
		assertThat( nn1, not(equalTo(nn2)));
		assertThat( nn1.getNumExcessGenes(nn2), equalTo(0) );
		assertThat( nn1.getNumDisjointGenes(nn2), equalTo(0) );
		assertThat( nn2.getNumExcessGenes(nn1), equalTo(0) );
		assertThat( nn2.getNumDisjointGenes(nn1), equalTo(0) );
		assertThat(nn1.computeAverageWeightDifferences(nn2), equalTo(0.0));
	}
	
}
