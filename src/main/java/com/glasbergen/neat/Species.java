package com.glasbergen.neat;

import java.util.LinkedList;
import java.util.List;

public class Species {

	private static final double speciesDifferenceThreshold = 50;
	private static final double weightDifferenceConstant = 0;
	private static final double disjointConstant = 1;
	private static final double excessConstant = 0.4;
	private List<NeuralNetwork> networksInSpecies;
	private int numNetworksInSpecies;
	
	public Species(NeuralNetwork definingNetwork){
		networksInSpecies = new LinkedList<>();
		networksInSpecies.add(definingNetwork);
		numNetworksInSpecies = 1;
	}
	
	public boolean addNetworkIfSpeciesMatch(NeuralNetwork networkToAdd){
		if(computeSimilarity(networksInSpecies.get(0), networkToAdd) < speciesDifferenceThreshold){
			networksInSpecies.add(networkToAdd);
			return true;
		}
		return false;
	}
	
	public double computeSimilarity(NeuralNetwork neuralNetwork, NeuralNetwork networkToAdd) {
		int numGenes = neuralNetwork.getNumGenes();
		int numGenes2 = networkToAdd.getNumGenes();
		numGenes = numGenes < numGenes2 ? numGenes : numGenes2;
		int excessGenes = neuralNetwork.getNumExcessGenes(networkToAdd);
		int disjointGenes = neuralNetwork.getNumDisjointGenes(networkToAdd);
		double averageWeightDifferences = neuralNetwork.computeAverageWeightDifferences(networkToAdd);
		return (excessGenes * excessConstant / numGenes) + (disjointGenes * disjointConstant / numGenes ) + weightDifferenceConstant * averageWeightDifferences;
		
	}

	public void addNetworksToSpecies(List<NeuralNetwork> networksToAdd){
		networksInSpecies.addAll(networksToAdd);
		numNetworksInSpecies += networksToAdd.size();
	}
	
	public int getNumNetworksInSpecies(){
		return numNetworksInSpecies;
	}
}
