package com.glasbergen.neat;

import java.util.List;

public class TestCaseOptimizer extends EvolutionOptimizer {

	private List<NeuralNetwork> nextGeneration;
	private TestCases testCases;
	private FitnessFunction func;
	
	public TestCaseOptimizer(TestCases tc, FitnessFunction func){
		testCases = tc;
		this.func = func;
		initializeNetworks(tc.getNumInputs(), tc.getNumOutputs());
	}
	
	public void runCurrentGeneration(){
		TestCaseFitnessEvaluator eval = new TestCaseFitnessEvaluator(networks, testCases, func);
		networks = eval.rankAllNetworks();
	}
	
}
