package com.glasbergen.neat;

public class TestCases {

	private double[][] inputs;
	private double[][] outputs;
	
	public TestCases(double [][] inputs, double[][] expectedOutputs){
		this.inputs =  inputs.clone();
		this.outputs = expectedOutputs.clone();
	}
	
	public double[][] getInputs(){
		return inputs;
	}
	
	public double[][] getExpectedOutputs(){
		return outputs;
	}
	
	//TODO: Handle case where they provide us no test cases
	public int getNumInputs(){
		return inputs[0].length;
	}
	
	public int getNumOutputs(){
		return outputs[0].length;
	}
}
