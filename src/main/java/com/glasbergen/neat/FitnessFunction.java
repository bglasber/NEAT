package com.glasbergen.neat;

public interface FitnessFunction {

	public double eval(double totalError, int numberInSpecies);
}
