package com.glasbergen.neat.io;

import java.lang.reflect.Type;

import com.glasbergen.neat.NeuralNetwork;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class NetworkDeserializer implements JsonDeserializer<NeuralNetwork> {

	@Override
	public NeuralNetwork deserialize(JsonElement el, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject obj = el.getAsJsonObject();
		return new NeuralNetwork(obj);
	}

}
