package com.glasbergen.neat.io;

import java.lang.reflect.Type;
import java.util.Map;

import com.glasbergen.neat.NeuralNetwork;
import com.glasbergen.neat.Node;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class NetworkSerializer implements JsonSerializer<NeuralNetwork> {

	@Override
	public JsonElement serialize(NeuralNetwork net, Type type, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add(JsonConstants.NUM_INPUTS, new JsonPrimitive(net.getNumInputs()));
		result.add(JsonConstants.NUM_OUTPUTS, new JsonPrimitive(net.getNumOutputs()));
		result.add(JsonConstants.FITNESS, new JsonPrimitive(net.getFitness()));
		result.add(JsonConstants.SOLUTION_FITNESS, new JsonPrimitive(net.getSolutionFitness()));
		result.add(JsonConstants.MAX_NETWORK_NODE_ID, new JsonPrimitive(net.getNetworkNodes().get(net.getNetworkNodes().size()-1).getId()));
		for( Node n : net.getNetworkNodes() ) {
			Map<Node,Double> deps = n.getAllDependencies();
			JsonObject subObj = new JsonObject();
			for( Node dep : deps.keySet() ) {
				subObj.add(Integer.toString(dep.getId()), new JsonPrimitive(deps.get(dep)));
			}
			result.add(JsonConstants.networkNodeString(n.getId()), subObj);
		}
		for( Node n : net.getOutputNodes() ) {
			Map<Node,Double> deps = n.getAllDependencies();
			JsonObject subObj = new JsonObject();
			for( Node dep : deps.keySet() ) {
				subObj.add(Integer.toString(dep.getId()), new JsonPrimitive(deps.get(dep)));
			}
			result.add(JsonConstants.outputNodeString(n.getId()), subObj);
		}
		return result;
	}

}
