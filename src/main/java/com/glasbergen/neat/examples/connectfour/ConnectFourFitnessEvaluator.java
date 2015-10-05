package com.glasbergen.neat.examples.connectfour;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.glasbergen.neat.FitnessEvaluator;
import com.glasbergen.neat.NeuralNetwork;

public class ConnectFourFitnessEvaluator implements FitnessEvaluator{

	private List<NeuralNetwork> networksToTest;
	
	public ConnectFourFitnessEvaluator(List<NeuralNetwork> networks){
		this.networksToTest = networks;
	}
	@Override
	public List<NeuralNetwork> rankAllNetworks() {
		System.out.println("Number of networks: " + networksToTest.size());
		Iterator<NeuralNetwork> iter = networksToTest.iterator();
		while(iter.hasNext()){
			NeuralNetwork net = iter.next();
			Iterator<NeuralNetwork> opponentIter = networksToTest.iterator();
			net.setFitness(0);
			while(opponentIter.hasNext()){
				if( playGame( net, opponentIter.next(), new Board()) ){
					net.setFitness(net.getFitness()+1);
				}
			}
			net.setSolutionFitness(net.getFitness());
			net.setFitness(net.getFitness()/net.getSpecies().getNumNetworksInSpecies());
		}
		return networksToTest;
	}
	public static boolean playGame(NeuralNetwork net, NeuralNetwork opp, Board b) {
		boolean madeIllegalMove = false;
		boolean opponentMadeIllegalMove = false;
		
		for(;;){
			if( !madeIllegalMove ){
				try {
					boolean won = getNetworkNextMove(net, b, madeIllegalMove, Colour.BLACK);
					if( won ){
						return !madeIllegalMove;
					}
				} catch( IllegalArgumentException e ){
					//Made illegal Move
					madeIllegalMove = true;
					if( fallbackMove(b, Colour.BLACK) ) {
						return false;
					}
				}
			} else {
				if( fallbackMove(b, Colour.BLACK) ){
					return false;
				}
			}
			if( !opponentMadeIllegalMove ){
				try {
					boolean oppWon = getNetworkNextMove(opp, b, opponentMadeIllegalMove, Colour.RED);
					if( oppWon ){
						return false;
					}
				} catch( IllegalArgumentException e ){
					opponentMadeIllegalMove = true;
					if( fallbackMove(b, Colour.RED) ){
						return false;
					}
				}

			} else {
				if( fallbackMove(b, Colour.RED) ){
					return false;
				}
			}
			
		}
	}
	/**
	 *  tries a fallback move when the neural network suggested an illegal move
	 * @param b
	 * @return shouldTerminate
	 */
	private static boolean fallbackMove(Board b, Colour c) {
		boolean moveSucceeded = false;
		Random r = new Random();
		int tryCol = r.nextInt(6);
		try {
			if( b.addToBoard(tryCol, c) ){
				return true;
			}
			return false;
		} catch( IllegalArgumentException e ){
			
		}
		for(int i = 0; i < 6; i++){
			try {
				if( b.addToBoard(i, c) ){
					return true;
				} else {
					moveSucceeded = true;
					break;
				}
			} catch( IllegalArgumentException e2){
				continue;
			}
		}
		if( !moveSucceeded ){
			//Board is full
			return true;
		}
		return false;
	}
	private static boolean getNetworkNextMove(NeuralNetwork net, Board b, boolean madeIllegalMove, Colour c) {
		double[] output = net.propagate2((double[] )b.boardStateToInputVector(c));
		int mostConfidentColumn = getMaxIndex( output );
		return b.addToBoard(mostConfidentColumn, c);
	}
	private static int getMaxIndex(double[] output) {
		// TODO Auto-generated method stub
		double maxVal = 0;
		int maxInd = 0;
		for( int i = 0; i < output.length; i++ ){
			if( output[i] > maxVal ){
				maxInd = i;
				maxVal = output[i];
			}
		}
		return maxInd;
	}

}
