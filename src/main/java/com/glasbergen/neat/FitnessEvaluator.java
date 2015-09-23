package com.glasbergen.neat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FitnessEvaluator {

	private List<NeuralNetwork> networksToTest;
	private FitnessFunction func;
	
	//TODO: extend to allow arbitrary functions for calculating error on a test case
	public FitnessEvaluator(List<NeuralNetwork> networksToTest, FitnessFunction fitnessFunction){
		this.networksToTest = networksToTest;
		func = fitnessFunction;
	}
	
	public List<NeuralNetwork> rankAllNetworks(double[][] inputs, double[][] expectedOutputs){
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
		for( int testCase = 0; testCase < numTestCases; testCase++){
			double errorOnThisTestCase = 0;
			double actual[] = nn.propagate2(inputs[testCase]);
			for( int i = 0; i < actual.length; i++ ){
				double error = ( expectedOutputs[testCase][i] - actual[i] );
				errorOnThisTestCase += error * error;
			}
			//TODO: do we scale the mean squared error here?
			totalError += errorOnThisTestCase;
		}
		
		totalError = totalError / numTestCases;
		double fitness = func.eval(totalError, nn.getSpecies().getNumNetworksInSpecies());
		nn.setFitness(fitness);
		return fitness;
	}

}