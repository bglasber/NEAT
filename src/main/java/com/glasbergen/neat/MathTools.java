package com.glasbergen.neat;

import java.util.List;
import java.util.Random;

public class MathTools {

	private static Random rand = new Random();
	
	public static double sigmoid( double z ){
		return 1 / (1 + Math.exp(-4.9*z));
	}
	
	public static double getUniformCenteredAtZero(){
		//return 2 * rand.nextDouble() -1;
		return getRandDouble();
	}
	
	public static double getPercent(){
		return ((double) rand.nextInt(100) + 1) / 100;
	}

	public static Double getRandDouble() {
		//TODO: chosen arbitrary number for now so we don't get out of range
		return (double) -500 + 1000 * rand.nextDouble();
	}
	
	public static <T> T getRandomElement(List<T> l){
		int ind = rand.nextInt(l.size());
		return l.get(ind);
	}
	public static int getInt(int bound){
		return rand.nextInt(bound);
	}
}
