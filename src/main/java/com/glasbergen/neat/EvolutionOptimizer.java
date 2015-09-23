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
	private static final int populationSize = 150;
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
		Iterator<NeuralNetwork> it = networks.iterator();
		nextGeneration = new LinkedList<>();
		//TODO:  Need to generate crossovers, do perturbance, calculate descendants, etc.
		// 25% of surviving networks are permutations
		for( int i = 0; i < populationSize * 0.25; i++ ){
			NeuralNetwork orig = it.next();
			NeuralNetwork network = orig.cloneNetwork();
			if( MathTools.getPercent() <= 0.3 ){
				network.addNewLink();
			} 
			if( MathTools.getPercent() <= 0.2 ){
				network.addNewNode();
			} 
			if( MathTools.getPercent() <= 0.8 ){
				network.perturb();
			}
			nextGeneration.add(network);
		}
		for(Species spec : species){
			// Most fit network in a species of greater than 5 goes to the next round, unchanged
			//if( spec.getNumNetworksInSpecies() > 5 ){
				NeuralNetwork network = spec.getMostFitNetwork();
				nextGeneration.add( network.cloneNetwork() );
			//}
		}
		
		it = networks.iterator();
		//Breed species until we hit this cap
		while( nextGeneration.size() < populationSize ){
			if( !it.hasNext() ) {
				break;
			}
			if( MathTools.getPercent() < 0.003 ){
				//Interspecies!
			} else {
				//Intraspecies!
				NeuralNetwork n = it.next();
				nextGeneration.addAll( n.getSpecies().breedAll( n ) );
				
			}
		}
		species.clear();
		boolean first = true;
		for( NeuralNetwork n : nextGeneration ){
			if( first ) {
				species.add(new Species(n));
				first = false;
			} else {
				boolean addedToExistingSpecies = false;
				for(Species spec : species){
					if( spec.addNetworkIfSpeciesMatch(n) ) {
						addedToExistingSpecies = true;
						break;
					}
				}
				if( !addedToExistingSpecies ) {
					species.add(new Species(n));
				}
			}
		}
	}
	
	public NeuralNetwork runAllGenerations(){
		double currentBestFitness = Double.MAX_VALUE;
		for(long generationNumber = 0; ; generationNumber++){
			runCurrentGeneration();
			currentBestFitness = networks.get(0).getFitness();
			System.out.println("Most Fit Network in generation " + generationNumber + " has fitness level of: "
					+ currentBestFitness);
			networks.get(0).dumpNetwork();
			if(currentBestFitness >= acceptableFitnessLevel){
				break;
			}
			networks = nextGeneration;
		}
		networks.get(0).dumpNetwork();
		return networks.get(0);
	}
	
}
