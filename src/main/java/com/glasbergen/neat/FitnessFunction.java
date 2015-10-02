package com.glasbergen.neat;

public interface FitnessFunction {

	public double evalFitness(double totalError, int numberInSpecies);

	public double evalSolutionFitness(double totalError);
}
