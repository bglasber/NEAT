package com.glasbergen.neat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class EvolutionOptimizer {
	protected List<Species> species;
	protected List<NeuralNetwork> networks;
	protected List<NeuralNetwork> nextGeneration;
	
	public EvolutionOptimizer(){
		species = new LinkedList<>();
		networks = new LinkedList<>();
	}
	
	/** 
	 * Runs the current generation of neural networks
	 * <p>
	 * Set this.networks to be the ranked list of neural networks
	 * after running
	 * </p>
	 */
	public abstract void runCurrentGeneration();
	
	/**
	 * Construct the initial set of neural networks with
	 * the provided number of inputs and outputs
	 * @param numInputs
	 * @param numOutputs
	 */
	public void initializeNetworks(int numInputs, int numOutputs){
		for(int i = 0; i < NeatParameters.POPULATION_SIZE; i++){
			NeuralNetwork nn = new NeuralNetwork(numInputs, numOutputs);	
			if( i == 0 ) {
				species.add(new Species(nn));
			} else {
				boolean addedToExistingSpecies = false;
				for(Species spec : species ) {
					if( spec.addNetworkIfSpeciesMatch(nn) ){
						addedToExistingSpecies = true;
						break;
					}
				}
				if( !addedToExistingSpecies ){
					species.add(new Species(nn));
				}
			}
			networks.add(nn);
		}
		for( int i = 0; i < numInputs; i++){
			NodeUtils.getNextId();
		}
	}
	
	/**
	 * Once we have ranked the networks in the current generation,
	 * we call this to perform the necessary mutations to prepare the next generation
	 */
	private void mutate(){
		
		//TODO:  Need to generate crossovers, do perturbance, calculate descendants, etc.
		// 25% of surviving networks are permutations
		Iterator<NeuralNetwork> it = networks.iterator();
		nextGeneration = new LinkedList<>();
		while(nextGeneration.size() < NeatParameters.POPULATION_SIZE * NeatParameters.UNBRED_CONTENDER_RATE ){
			if( !it.hasNext() ){
				break;
			}
			NeuralNetwork orig = it.next();
			//Don't reuse if this network has stagnated
			if( orig.getSpeciesStagnantRounds() >= NeatParameters.STAGNATION_ROUNDS ){
				continue;
			}
			NeuralNetwork network = orig.cloneNetwork();
			if( MathTools.getPercent() <= NeatParameters.NEW_LINK_RATE ){
				network.addNewLink();
			} if( MathTools.getPercent() <= NeatParameters.NEW_NODE_RATE ){
				network.addNewNode();
			} 
			if( MathTools.getPercent() <= NeatParameters.WEIGHT_MUTATION_RATE ){
				network.perturb();
			}
			nextGeneration.add(network);
		}
		for(Species spec : species){
			// Most fit network in a species of greater than 5 goes to the next round, unchanged
			if( spec.getStagnantRounds() >= NeatParameters.STAGNATION_ROUNDS ){
				continue;
			}
			if( spec.getNumNetworksInSpecies() > NeatParameters.POPULATION_SIZE_TO_REMAIN_UNCHANGED ){
				NeuralNetwork network = spec.getMostFitNetwork();
				nextGeneration.add( network.cloneNetwork() );
			}
		}
		
		it = networks.iterator();
		//Breed species until we hit this cap
		while( nextGeneration.size() < NeatParameters.POPULATION_SIZE ){
			if( !it.hasNext() ) {
				break;
			}
			if( MathTools.getPercent() < NeatParameters.INTERSPECIES_BREEDING_RATE ){
				//Interspecies!
				NeuralNetwork n = it.next();
				if( n.getSpeciesStagnantRounds() >= NeatParameters.STAGNATION_ROUNDS ){
					continue;
				}
				for(Species spec : species){
					if( !spec.equals(n.getSpecies()) ){
						nextGeneration.addAll(spec.breedAll(n));
						break;
					}
				}
			} else {
				//Intraspecies!
				NeuralNetwork n = it.next();
				if( n.getSpeciesStagnantRounds() >= NeatParameters.STAGNATION_ROUNDS ){
					continue;
				}
				nextGeneration.addAll( n.getSpecies().breedAll( n ) );
				
			}
		}
		List<Species> newSpecies = new LinkedList<>();
		boolean first = true;
		for( NeuralNetwork n : nextGeneration ){
			if( first ) {
				Species newSpec = new Species(n);
				for(Species oldSpec : species){
					if(oldSpec.isMatchingSpecies(n)){
						newSpec.setAsNewGenerationOfOldSpecies(oldSpec);
					}
				}
				newSpecies.add(newSpec);
				
				first = false;
			} else {
				boolean addedToExistingSpecies = false;
				for(Species spec : newSpecies){
					if( spec.addNetworkIfSpeciesMatch(n) ) {
						addedToExistingSpecies = true;
						break;
					}
				}
				if( !addedToExistingSpecies ) {
					Species newSpec = new Species(n);
					newSpecies.add(newSpec);
					for(Species oldSpec : species){
						if(oldSpec.isMatchingSpecies(n)){
							newSpec.setAsNewGenerationOfOldSpecies(oldSpec);
						}
					}
				}
			}
		}
		species.clear();
		species = newSpecies;
	}
	
	/**
	 * Run generation after generation until we hit the fitness threshold,
	 * or the next generation has no networks in it
	 * 
	 * @param fitnessThreshold
	 * @return Network
	 */
	public NeuralNetwork runAllGenerations(double fitnessThreshold){
		for(long generationNumber = 0; ; generationNumber++){
			runCurrentGeneration(); //run generation
			mutate(); // mutate
			NeuralNetwork best = getNeuralNetworkWithBestSolutionFitness(networks); // dump best network
			System.out.println("Most Fit Network in generation " + generationNumber + " has fitness level of: "
					+ best.getSolutionFitness());
			best.dumpNetwork();
			// break ?
			if(best.getSolutionFitness() >= fitnessThreshold || generationNumber > NeatParameters.MAX_GENERATIONS ){
				return best;
			}
			// next round!
			networks = nextGeneration;
		}
	}

	private NeuralNetwork getNeuralNetworkWithBestSolutionFitness(List<NeuralNetwork> networks) {
		double currentBest = -1;
		NeuralNetwork currentBestNetwork = null;
		for( NeuralNetwork network : networks ){
			if( network.getSolutionFitness() > currentBest ){
				currentBestNetwork = network;
				currentBest = network.getSolutionFitness();
			}
		}
		return currentBestNetwork;
		
	}

}
