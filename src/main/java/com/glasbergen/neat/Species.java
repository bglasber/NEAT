package com.glasbergen.neat;

import java.util.LinkedList;
import java.util.List;

public class Species {

	private List<NeuralNetwork> networksInSpecies;
	private int numNetworksInSpecies;
	private NeuralNetwork mostFitSpecimen;
	private double bestFitnessThisRound = 0;
	private double bestFitnessThusFar = 0;
	//TODO: Implement this
	private int stagnantRounds = 0;
	
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
		if(isMatchingSpecies(networkToAdd)){
			networksInSpecies.add(networkToAdd);
			numNetworksInSpecies++;
			networkToAdd.setSpecies(this);
			return true;
		}
		return false;
	}

	public boolean isMatchingSpecies(NeuralNetwork networkToAdd) {
		return computeSimilarity(networksInSpecies.get(0), networkToAdd) < 
				NeatParameters.SPECIES_DIFFERENCE_THRESHOLD;
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
		//Don't bother for small genomes
		if( numGenes < NeatParameters.SMALL_GENOME_CUTOFF ){
			numGenes = 1;
		}
		int excessGenes = neuralNetwork.getNumExcessGenes(networkToAdd);
		int disjointGenes = neuralNetwork.getNumDisjointGenes(networkToAdd);
		double averageWeightDifferences = neuralNetwork.computeAverageWeightDifferences(networkToAdd);
		return (excessGenes * NeatParameters.EXCESS_CONSTANT / numGenes) + 
				(disjointGenes * NeatParameters.DISJOINT_CONSTANT / numGenes ) +
				NeatParameters.WEIGHT_DIFFERENCE_CONSTANT * averageWeightDifferences;
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
		if( fitness > bestFitnessThisRound ){
			mostFitSpecimen = neuralNetwork;
			bestFitnessThisRound = fitness;
		}
		
	}

	public List<NeuralNetwork> breedAll(NeuralNetwork n) {
		List<NeuralNetwork> children = new LinkedList<>();
		for( NeuralNetwork network : networksInSpecies ){
			children.add( n.crossOver(network) );
		}
		return children;
	}
	
	public int getStagnantRounds(){
		return stagnantRounds;
	}
	
	public void setAsNewGenerationOfOldSpecies(Species oldSpec){
		bestFitnessThusFar = oldSpec.bestFitnessThusFar;
		stagnantRounds = oldSpec.stagnantRounds;
		if( oldSpec.bestFitnessThisRound <= oldSpec.bestFitnessThusFar){
			stagnantRounds++;
		}
	}

	public void setStagnantRounds(int stagnantRounds) {
		this.stagnantRounds = stagnantRounds;
	}
}
