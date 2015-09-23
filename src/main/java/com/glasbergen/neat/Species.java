package com.glasbergen.neat;

import java.util.LinkedList;
import java.util.List;

public class Species {

	private static final double speciesDifferenceThreshold = 3.0;
	private static final double weightDifferenceConstant = 0;
	private static final double disjointConstant = 1;
	private static final double excessConstant = 0.4;
	private List<NeuralNetwork> networksInSpecies;
	private int numNetworksInSpecies;
	private NeuralNetwork mostFitSpecimen;
	private double bestFitnessValueThusFar = 0;
	
	/**
	 * A species is defined by one network, all others in this species
	 * must be "similar" enough to it
	 * @param definingNetwork
	 */
	public Species(NeuralNetwork definingNetwork){
		networksInSpecies = new LinkedList<>();
		networksInSpecies.add(definingNetwork);
		definingNetwork.setSpecies(this);
		numNetworksInSpecies = 1;
	}
	
	/**
	 * Run the distance algorithm on networkToAdd, and if it is within
	 * the speciesDifferenceThreshold, add it to this species
	 * @param networkToAdd
	 * @return boolean indicating whether it was added
	 */
	public boolean addNetworkIfSpeciesMatch(NeuralNetwork networkToAdd){
		if(computeSimilarity(networksInSpecies.get(0), networkToAdd) < speciesDifferenceThreshold){
			networksInSpecies.add(networkToAdd);
			numNetworksInSpecies++;
			networkToAdd.setSpecies(this);
			return true;
		}
		return false;
	}
	
	/**
	 * Computes the similarity between two neural networks
	 * c1 * E / N + c2 * D / N + c3 * avg(Weight Differences)
	 * @param neuralNetwork
	 * @param networkToAdd
	 * @return
	 */
	public double computeSimilarity(NeuralNetwork neuralNetwork, NeuralNetwork networkToAdd) {
		int numGenes = neuralNetwork.getNumGenes();
		int numGenes2 = networkToAdd.getNumGenes();
		numGenes = numGenes < numGenes2 ? numGenes : numGenes2;
		int excessGenes = neuralNetwork.getNumExcessGenes(networkToAdd);
		int disjointGenes = neuralNetwork.getNumDisjointGenes(networkToAdd);
		double averageWeightDifferences = neuralNetwork.computeAverageWeightDifferences(networkToAdd);
		return (excessGenes * excessConstant / numGenes) + (disjointGenes * disjointConstant / numGenes )
				+ weightDifferenceConstant * averageWeightDifferences;
	}

	/**
	 * Add the provided networks to this species, no checking for similarity
	 * @param networksToAdd
	 */
	public void addNetworksToSpecies(List<NeuralNetwork> networksToAdd){
		networksInSpecies.addAll(networksToAdd);
		numNetworksInSpecies += networksToAdd.size();
	}
	
	/** 
	 * return the number of networks contained in this species
	 * @return
	 */
	public int getNumNetworksInSpecies(){
		return numNetworksInSpecies;
	}

	/**
	 * Return the most fit neural network from the last run
	 * @return
	 */
	public NeuralNetwork getMostFitNetwork() {
		return mostFitSpecimen;
		
	}

	public void rememberIfBest(NeuralNetwork neuralNetwork, double fitness) {
		if( fitness > bestFitnessValueThusFar ){
			mostFitSpecimen = neuralNetwork;
			bestFitnessValueThusFar = fitness;
		}
		
	}
}