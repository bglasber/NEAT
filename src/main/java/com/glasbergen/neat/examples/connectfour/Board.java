package com.glasbergen.neat.examples.connectfour;

public class Board {
	NodeState board[][];
	
	public Board(){
		board = new NodeState[6][7];
		for(int i = 0; i < 6; i++ ){
			for(int j = 0; j < 7; j++){
				board[i][j] = NodeState.OPEN;
			}
		}
	}
	
	public boolean addToBoard(int col, Colour c){
		for( int i = 0; i < 6; i++ ){
			if( board[i][col] == NodeState.OPEN ){
				continue;
			} 
			//Not open
			if(i == 0){
				throw new IllegalArgumentException("row is full!");
			} else {
				board[i-1][col] = colourToNodeColour(c);
				return checkForWin(i-1, col, colourToNodeColour(c));
			}
		}
		board[5][col] = colourToNodeColour(c); 
		return checkForWin(5, col, colourToNodeColour(c));
	}
	
	private boolean checkForWin(int i, int col, NodeState c) {
		// Left
		if( col >= 3 ){
			if( board[i][col-3] == c &&
				board[i][col-2] == c &&
				board[i][col-1] == c &&
				board[i][col] == c /* unnecessary */ 
			){
				return true;
			}
		}
		// Up Left
		if( col >= 3 && i >= 3 ){
			if( board[i-3][col-3] == c &&
				board[i-2][col-2] == c &&
				board[i-1][col-1] == c &&
				board[i][col] == c ) {
				return true;
			}
		}
		// Up Right
		if( col <= 3 && i >= 3 ){
			if( board[i-3][col+3] == c &&
				board[i-2][col+2] == c &&
				board[i-1][col+1] == c &&
				board[i][col] == c ){
				return true;
			}
		}
		// Right
		if( col <= 3 ){
			if( board[i][col+3] == c &&
				board[i][col+2] == c &&
				board[i][col+1] == c &&
				board[i][col] == c ){
				return true;
			}
		}
		// Down Right
		if( col <= 3 && i <= 2 ){
			if( board[i+3][col+3] == c && 
				board[i+2][col+2] == c &&
				board[i+1][col+1] == c &&
				board[i][col] == c ){
				return true;
			}
		}
		// Down
		if( i <= 2 ){
			if( board[i+3][col] == c && 
				board[i+2][col] == c &&
				board[i+1][col] == c &&
				board[i][col] == c ){
				return true;
			}
		}
		// Down Left
		if( i <= 2 && col >= 3){
			if( board[i+3][col-3] == c && 
				board[i+2][col-2] == c &&
				board[i+1][col-1] == c &&
				board[i][col] == c ){
				return true;
			}
		}
		return false;
	}

	public int[] boardStateToInputVector(Colour myColour){
		//Each Node has 3 states - RED or not, BLACK or not, Open or not
		//+1 for Bias
		int[] input = new int[6*7*3+1];
		int curInd = 0;
		for( int i = 0; i < 6; i++ ){
			for( int j = 0; j < 7; j++ ){
				if( board[i][j] == NodeState.OPEN ){
					input[curInd+2] = 1;
				} else if( board[i][j] == colourToNodeColour(myColour) ){
					input[curInd] = 1;
				} else {
					input[curInd+1] = 1;
				} 
				curInd += 3;
			}
		}
		input[6*7*3] = 1; //Bias, always 1
		return input;
	}

	public NodeState colourToNodeColour(Colour c) {
		return (c == Colour.BLACK) ? NodeState.BLACK : NodeState.RED;
	}
	
}
