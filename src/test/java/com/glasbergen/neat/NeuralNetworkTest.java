package com.glasbergen.neat;

import static org.junit.Assert.*;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.glasbergen.neat.io.NetworkDeserializer;
import com.glasbergen.neat.io.NetworkSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
public class NeuralNetworkTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	/**
	 * Very simple 1x1 neural network with 1.0 weights and 1.0 inputs
	 */
	@Test
	public void testInitializationOfOneInputOneOutput() {
		NeuralNetwork nn = new NeuralNetwork(1, 1, new double[]{ 1000 });
		double[] results = nn.propagate2(new double[]{1.0});
		assertThat(results.length, equalTo(1));
		assertThat(results[0], equalTo(1.0));
	}

	/**
	 * 2x2 neural network with all 1.0 weights to outputs
	 */
	@Test
	public void testInitializationOfTwoInputTwoOutput() {
		NeuralNetwork nn = new NeuralNetwork(2, 2, new double[]{ 1000, 1000, 1000, 1000 });
		double[] results = nn.propagate2(new double[]{1.0, 1.0});
		assertThat(results.length, equalTo(2));
		assertThat(results[0], equalTo(1.0));
		assertThat(results[1], equalTo(1.0));
	}
	
	/**
	 * 2x2 neural network with a more complex weighting scheme
	 */
	@Test
	public void testInitializationOfTwoInputTwoOutputComplexWeighting() {
		NeuralNetwork nn = new NeuralNetwork(2, 2, new double[]{ 1000, 1000, -5000, 1000 });
		double[] results = nn.propagate2(new double[]{1.0, 1.0});
		assertThat(results.length, equalTo(2));
		assertThat(results[0], equalTo(1.0));
		assertThat(results[1], equalTo(0.0));
	}

	@Test
	public void testSerialize() throws IOException{
		NeuralNetwork nn = new NeuralNetwork(2, 2, new double[]{1,1,1,1});
		NodeUtils.getNextId();
		NodeUtils.getNextId();
		nn.addNewNode();
		nn.addNewNode();
		nn.addNewLink();
		nn.addNewLink();
		Gson gson = new GsonBuilder().registerTypeAdapter(NeuralNetwork.class, new NetworkSerializer()).registerTypeAdapter(NeuralNetwork.class, new NetworkDeserializer()).create();
		String json = gson.toJson(nn);
		NeuralNetwork net = gson.fromJson(json, NeuralNetwork.class);
		assertThat( gson.toJson(net), equalTo(gson.toJson(nn)));
	}

}
