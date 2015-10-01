package com.glasbergen.neat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EvolutionOptimizer {

	private List<NeuralNetwork> networks;
	private List<NeuralNetwork> nextGeneration;
	private TestCases testCases;
	private double acceptableFitnessLevel;
	private List<Species> species;
	private FitnessFunction func;
	
	public EvolutionOptimizer(TestCases tc, FitnessFunction func, double acceptableFitnessLevel){
		networks = new LinkedList<>();
		testCases = tc;
		this.func = func;
		this.acceptableFitnessLevel = acceptableFitnessLevel;
		species = new LinkedList<>();
		for(int i = 0; i < 300; i++){
			NeuralNetwork nn = new NeuralNetwork(testCases.getNumInputs(), testCases.getNumOutputs());	
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
		for( int i = 0; i < testCases.getNumInputs(); i++){
			NodeUtils.getNextId();
		}
	}
	
	public void runCurrentGeneration(){
		FitnessEvaluator eval = new FitnessEvaluator(networks, func);
		networks = eval.rankAllNetworks(testCases.getInputs(), testCases.getExpectedOutputs());
		NeuralNetwork net = getNeuralNetworkWithBestSolutionFitness(networks);
		Iterator<NeuralNetwork> it = networks.iterator();
		nextGeneration = new LinkedList<>();
		//TODO:  Need to generate crossovers, do perturbance, calculate descendants, etc.
		// 25% of surviving networks are permutations
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
	
	public NeuralNetwork runAllGenerations(){
		for(long generationNumber = 0; ; generationNumber++){
			runCurrentGeneration();
			NeuralNetwork best = getNeuralNetworkWithBestSolutionFitness(networks);
			System.out.println("Most Fit Network in generation " + generationNumber + " has fitness level of: "
					+ best.getSolutionFitness());
			best.dumpNetwork();
			if(best.getSolutionFitness() >= acceptableFitnessLevel || nextGeneration.size() == 0 ){
				return best;
			}
			networks = nextGeneration;
		}
	}

	private NeuralNetwork getNeuralNetworkWithBestSolutionFitness(List<NeuralNetwork> networks) {
		double currentBest = 0;
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
