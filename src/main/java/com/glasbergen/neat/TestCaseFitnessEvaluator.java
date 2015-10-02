package com.glasbergen.neat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestCaseFitnessEvaluator implements FitnessEvaluator{

	private List<NeuralNetwork> networksToTest;
	private FitnessFunction func;
	private double[][] inputs;
	private double[][] expectedOutputs;
	
	//TODO: extend to allow arbitrary functions for calculating error on a test case
	public TestCaseFitnessEvaluator(List<NeuralNetwork> networksToTest, TestCases tc, FitnessFunction fitnessFunction){
		this.networksToTest = networksToTest;
		func = fitnessFunction;
		this.inputs = tc.getInputs();
		this.expectedOutputs = tc.getExpectedOutputs();
	}
	
	public List<NeuralNetwork> rankAllNetworks(){
		for(NeuralNetwork nn : networksToTest ){
			evaluateFitness(nn, inputs, expectedOutputs);
		}
		Comparator<NeuralNetwork> networkComparator = new Comparator<NeuralNetwork>() {
			
			@Override
			public int compare(NeuralNetwork o1, NeuralNetwork o2) {
				return -Double.compare(o1.getFitness(), o2.getFitness());
			}
		};
		Collections.sort(networksToTest, networkComparator);
		return networksToTest;
	}
	/**
	 * Given a neural network, an array of inputs, and an array of outputs for those test cases,
	 * evaluate how 'fit' this neural network is
	 * @param nn
	 * @param inputs
	 * @param expectedOutputs
	 * @return
	 */
	public double evaluateFitness(NeuralNetwork nn, double[][] inputs, double[][] expectedOutputs){
		int numTestCases = inputs.length;
		double totalError = 0;
		double hypError = 0;
		for( int testCase = 0; testCase < numTestCases; testCase++){
			double errorOnThisTestCase = 0;
			double actual[] = nn.propagate2(inputs[testCase]);
			for( int i = 0; i < actual.length; i++ ){
				double error = ( expectedOutputs[testCase][i] - actual[i] );
				double adjustedOut = (actual[i]) >= 0.5 ? 1 : 0;
				adjustedOut = ( expectedOutputs[testCase][i] - adjustedOut );
				hypError += adjustedOut * adjustedOut;
				errorOnThisTestCase += error * error;
			}
			//TODO: do we scale the mean squared error here?
			totalError += errorOnThisTestCase;
		}
		
		//totalError = totalError / numTestCases;
		double fitness = func.evalFitness(totalError, nn.getSpecies().getNumNetworksInSpecies());
		nn.setSolutionFitness(func.evalSolutionFitness(hypError));
		nn.setFitness(fitness);
		return fitness;
	}

}
