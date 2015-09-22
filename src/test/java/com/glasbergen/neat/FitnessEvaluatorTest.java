package com.glasbergen.neat;
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;
public class FitnessEvaluatorTest {

	/**
	 * Test fitness of a simple 1x1 neural network
	 */
	@Test
	public void testSimpleFitness(){
		NeuralNetwork nn = new NeuralNetwork(1, 1, new double[]{ 1000.0 });
		double inputs[][] = new double[1][1];
		inputs[0][0] = 1;
		double expectedOutputs[][] = new double[1][1];
		expectedOutputs[0][0] = 1;
		assertThat(FitnessEvaluator.evaluateFitness(nn, inputs, expectedOutputs), equalTo(0.0));
	}
	
	/**
	 * Test that we return a higher fitness level (which is bad) if our test cases don't match
	 */
	@Test
	public void testBasicErrorCalcs(){
		NeuralNetwork nn = new NeuralNetwork(1, 1, new double[]{ 1000 });
		double inputs[][] = new double[1][1];
		inputs[0][0] = 1;
		double expectedOutputs[][] = new double[1][1];
		expectedOutputs[0][0] = 0;
		assertThat(FitnessEvaluator.evaluateFitness(nn, inputs, expectedOutputs), equalTo(1.0));
	}
	
	/**
	 * Test that we average the error from all test cases
	 */
	@Test
	public void testAverageError(){
		NeuralNetwork nn = new NeuralNetwork(2, 1, new double[]{ 1000, 1000 });
		double inputs[][] = new double[4][2];
		double expectedOutputs[][] = new double[4][1];
		//XOR
		inputs[0][0] = 1;
		inputs[0][1] = 1;
		expectedOutputs[0][0] = 0;
		inputs[1][0] = 1;
		inputs[1][1] = 0;
		expectedOutputs[1][0] = 1;
		inputs[2][0] = 1;
		inputs[2][1] = 0;
		expectedOutputs[2][0] = 1;
		inputs[3][0] = 0;
		inputs[3][1] = 0;
		expectedOutputs[3][0] = 0;
		assertThat(FitnessEvaluator.evaluateFitness(nn, inputs, expectedOutputs), equalTo(1.25 / 4));
	}
	
	/**
	 * Test that we count error on all the incorrect outputs
	 */
	@Test
	public void testAllOutputErrorConsidered(){
		NeuralNetwork nn = new NeuralNetwork(1, 2, new double[]{ 1000, 1000 });
		double inputs[][] = new double[1][1];
		double expectedOutputs[][] = new double[1][2];
		inputs[0][0] = 1;
		expectedOutputs[0][0] = 0;
		expectedOutputs[0][1] = 0;
		assertThat(FitnessEvaluator.evaluateFitness(nn, inputs, expectedOutputs), equalTo(2.0));
	}
}
