package com.glasbergen.neat.examples.connectfour;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.glasbergen.neat.EvolutionOptimizer;
import com.glasbergen.neat.NeuralNetwork;
import com.glasbergen.neat.io.NetworkDeserializer;
import com.glasbergen.neat.io.NetworkSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConnectFourOptimizer extends EvolutionOptimizer {
	private Gson gson;
	private File outputFile;
	private PrintWriter pw;
	public ConnectFourOptimizer() throws IOException {
		initializeNetworks(6*7*3+1, 6);
		gson = new GsonBuilder().registerTypeAdapter(NeuralNetwork.class, new NetworkSerializer()).registerTypeAdapter(NeuralNetwork.class, new NetworkDeserializer()).create();
		outputFile = new File("connectFourNetworks.ser");
		if( !outputFile.exists() ){
			outputFile.createNewFile();
		}
		pw = new PrintWriter(outputFile);
				
	}
	@Override
	public void runCurrentGeneration() {
		//TODO:
		//Have every network play every other network
		//sort by number of wins
		//On IllegalArgument, the machine loses. Pick at random to give the
		//other machine something to play against
		ConnectFourFitnessEvaluator eval = new ConnectFourFitnessEvaluator(networks);
		networks = eval.rankAllNetworks();
		
		String json = gson.toJson(networks.get(0));
		pw.println(json);
	}

}
