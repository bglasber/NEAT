package com.glasbergen.neat;

public class Neat {

	public static void main(String[] args) {
		System.out.println("Neat!");
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
		};
		EvolutionOptimizer optimizer = new EvolutionOptimizer(tc, func, 0.05);
		optimizer.runAllGenerations();
	}

}
