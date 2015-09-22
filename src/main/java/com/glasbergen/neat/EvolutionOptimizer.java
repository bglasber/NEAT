package com.glasbergen.neat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EvolutionOptimizer {

	private List<NeuralNetwork> networks;
	private TestCases testCases;
	private double acceptableFitnessLevel;
	
	public EvolutionOptimizer(TestCases tc, double acceptableFitnessLevel){
		networks = new LinkedList<>();
		testCases = tc;
		this.acceptableFitnessLevel = acceptableFitnessLevel;
		for(int i = 0; i < 50; i++){
			networks.add(new NeuralNetwork(testCases.getNumInputs(), testCases.getNumOutputs()));
		}
		for( int i = 0; i < testCases.getNumInputs(); i++){
			NodeUtils.getNextId();
		}
	}
	
	public void runCurrentGeneration(){
		FitnessEvaluator eval = new FitnessEvaluator(networks);
		networks = eval.rankAllNetworks(testCases.getInputs(), testCases.getExpectedOutputs());
		Iterator<NeuralNetwork> it = networks.iterator();
		List<NeuralNetwork> nextGeneration = new LinkedList<>();
		//TODO:  Need to generate crossovers, do perturbance, calculate descendants, etc.
		for(int i = 0; i < 10; i++){
			NeuralNetwork network = it.next();
			nextGeneration.add(network);
			if( MathTools.getPercent() <= 0.03 ){
				network.addNewNode();
			} else if( MathTools.getPercent() <= 0.05 ){
				network.addNewLink();
			}
			if( MathTools.getPercent() <= 0.8 ){
				network.perturb();
			}
			nextGeneration.add(network);
		}
		networks = nextGeneration;
		
	}
	
	public void runAllGenerations(){
		double currentBestFitness = Double.MAX_VALUE;
		for(long generationNumber = 0; ; generationNumber++){
			runCurrentGeneration();
			currentBestFitness = networks.get(0).getFitness();
			System.out.println("Most Fit Network in generation " + generationNumber + " has fitness level of: "
					+ currentBestFitness);
			networks.get(0).dumpNetwork();
			if(currentBestFitness <= acceptableFitnessLevel){
				break;
			}
		}
		networks.get(0).dumpNetwork();
	}
	
}
