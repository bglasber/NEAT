package com.glasbergen.neat.examples;

import java.util.LinkedList;
import java.util.List;

import com.glasbergen.neat.TestCaseOptimizer;
import com.glasbergen.neat.TestCaseFitnessEvaluator;
import com.glasbergen.neat.FitnessFunction;
import com.glasbergen.neat.NeuralNetwork;
import com.glasbergen.neat.TestCases;

public class Xor {

	public static void main(String[] args) {
		System.out.println("Neat!");
		double inputs[][] = new double[4][3];
		double expectedOutputs[][] = new double[4][1];
		//XOR
		inputs[0][0] = 0;
		inputs[0][1] = 0;
		inputs[0][2] = 1;
		expectedOutputs[0][0] = 0;
		inputs[1][0] = 0;
		inputs[1][1] = 1;
		inputs[1][2] = 1;
		expectedOutputs[1][0] = 1;
		inputs[2][0] = 1;
		inputs[2][1] = 0;
		inputs[2][2] = 1;
		expectedOutputs[2][0] = 1;
		inputs[3][0] = 1;
		inputs[3][1] = 1;
		inputs[3][2] = 1;
		expectedOutputs[3][0] = 0;
		TestCases tc = new TestCases(inputs, expectedOutputs);
		FitnessFunction func = new FitnessFunction() {
			
			@Override
			public double eval(double totalError, int numberInSpecies) {
				// f(x,N) = (4 - x)^2 / N
				double fitness = (4 - totalError);
				fitness = fitness * fitness;
				fitness = fitness / numberInSpecies;
				return fitness;
			}

			@Override
			public double evalUnscaled(double totalError) {
				double fitness = (4 - totalError);
				fitness = fitness * fitness;
				return fitness;
			}
		};
		TestCaseOptimizer optimizer = new TestCaseOptimizer(tc, func);
		NeuralNetwork output = optimizer.runAllGenerations(15.9);
		double[] outputs = output.propagate2(new double[]{ 0, 0, 1 });
		System.out.println("XOR 0 0 -> " + outputs[0]);
		outputs = output.propagate2(new double[]{ 0, 1, 1 });
		System.out.println("XOR 0 1 -> " + outputs[0]);
		outputs = output.propagate2(new double[]{ 1, 0, 1 });
		System.out.println("XOR 1 0 -> " + outputs[0]);
		outputs = output.propagate2(new double[]{ 1, 1, 1 });
		System.out.println("XOR 1 1 -> " + outputs[0]);
		List<NeuralNetwork> net = new LinkedList<>();
		net.add(output);
		TestCaseFitnessEvaluator eval = new TestCaseFitnessEvaluator(net, tc, func);
		eval.evaluateFitness(output, inputs, expectedOutputs);
		System.out.println("Fitness: " + output.getSolutionFitness());
	}

}
