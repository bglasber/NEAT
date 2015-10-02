package com.glasbergen.neat.examples.connectfour;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class BoardTest {

	@Test
	public void simpleBoardAdd(){
		Board b = new Board();
		b.addToBoard(0, Colour.BLACK);
		int[] inputVec = b.boardStateToInputVector(Colour.BLACK);
		for(int i = 0; i < 6*7*3; i+=3){
			if( i == 105 ) { //bottom left position
				assertThat(inputVec[i], equalTo(1));
				assertThat(inputVec[i+1], equalTo(0));
				assertThat(inputVec[i+2], equalTo(0));
			} else {
				assertThat(inputVec[i], equalTo(0));
				assertThat(inputVec[i+1], equalTo(0));
				assertThat(inputVec[i+2], equalTo(1));
			}
		}
		assertThat(inputVec[6*7*3], equalTo(1));
	}
	
	@Test
	public void simpleRightWin(){
		Board b = new Board();
		b.addToBoard(0, Colour.BLACK);
		b.addToBoard(1, Colour.BLACK);
		b.addToBoard(2, Colour.BLACK);
		assertThat(b.addToBoard(3, Colour.BLACK), equalTo(true));
		int[] inputVec = b.boardStateToInputVector(Colour.BLACK);
		for(int i = 0; i < 6*7*3; i+=3){
			if( i >= 105 && i <= 114) { //bottom left 4 positions
				assertThat(inputVec[i], equalTo(1));
				assertThat(inputVec[i+1], equalTo(0));
				assertThat(inputVec[i+2], equalTo(0));
			} else {
				assertThat(inputVec[i], equalTo(0));
				assertThat(inputVec[i+1], equalTo(0));
				assertThat(inputVec[i+2], equalTo(1));
			}
		}
	}
	
	@Test
	public void simpleLeftWin(){
		Board b = new Board();
		b.addToBoard(6, Colour.BLACK);
		b.addToBoard(5, Colour.BLACK);
		b.addToBoard(4, Colour.BLACK);
		assertThat(b.addToBoard(3, Colour.BLACK), equalTo(true));
		int[] inputVec = b.boardStateToInputVector(Colour.BLACK);
		for(int i = 0; i < 6*7*3; i+=3){
			if( i >= 114 && i <= 123) { //bottom right 4 positions
				assertThat(inputVec[i], equalTo(1));
				assertThat(inputVec[i+1], equalTo(0));
				assertThat(inputVec[i+2], equalTo(0));
			} else {
				assertThat(inputVec[i], equalTo(0));
				assertThat(inputVec[i+1], equalTo(0));
				assertThat(inputVec[i+2], equalTo(1));
			}
		}
	}
	
	@Test
	public void simpleDownWin(){
		Board b = new Board();
		b.addToBoard(0, Colour.BLACK);
		b.addToBoard(0, Colour.BLACK);
		b.addToBoard(0, Colour.BLACK);
		assertThat(b.addToBoard(0, Colour.BLACK), equalTo(true));
		int[] inputVec = b.boardStateToInputVector(Colour.BLACK);
		for(int i = 0; i < 6*7*3; i+=3){
			if( i == 42 || i == 63 || i ==  84 || i == 105) { //stack bottom left
				assertThat(inputVec[i], equalTo(1));
				assertThat(inputVec[i+1], equalTo(0));
				assertThat(inputVec[i+2], equalTo(0));
			} else {
				assertThat(inputVec[i], equalTo(0));
				assertThat(inputVec[i+1], equalTo(0));
				assertThat(inputVec[i+2], equalTo(1));
			}
		}
	}
	
	@Test
	public void MultiColourConversion(){
		Board b = new Board();
		b.addToBoard(0, Colour.BLACK);
		b.addToBoard(0, Colour.RED);
		b.addToBoard(0, Colour.BLACK);
		assertThat(b.addToBoard(0, Colour.BLACK), equalTo(false));
		int[] inputVec = b.boardStateToInputVector(Colour.BLACK);
		for(int i = 0; i < 6*7*3; i+=3){
			if( i == 42 || i == 63 || i == 105) { //stack bottom left
				assertThat(inputVec[i], equalTo(1));
				assertThat(inputVec[i+1], equalTo(0));
				assertThat(inputVec[i+2], equalTo(0));
			} else if( i == 84 ) {
				assertThat(inputVec[i], equalTo(0));
				assertThat(inputVec[i+1], equalTo(1));
				assertThat(inputVec[i+2], equalTo(0));
			} else {
				assertThat(inputVec[i], equalTo(0));
				assertThat(inputVec[i+1], equalTo(0));
				assertThat(inputVec[i+2], equalTo(1));
			}
		}
		inputVec = b.boardStateToInputVector(Colour.RED);
		for(int i = 0; i < 6*7*3; i+=3){
			if( i == 42 || i == 63 || i == 105) { //stack bottom left
				assertThat(inputVec[i], equalTo(0));
				assertThat(inputVec[i+1], equalTo(1));
				assertThat(inputVec[i+2], equalTo(0));
			} else if( i == 84 ) {
				assertThat(inputVec[i], equalTo(1));
				assertThat(inputVec[i+1], equalTo(0));
				assertThat(inputVec[i+2], equalTo(0));
			} else {
				assertThat(inputVec[i], equalTo(0));
				assertThat(inputVec[i+1], equalTo(0));
				assertThat(inputVec[i+2], equalTo(1));
			}
		}
	}
	@Test
	public void testRedWin(){
		Board b = new Board();
		b.addToBoard(0, Colour.RED);
		b.addToBoard(1, Colour.RED);
		b.addToBoard(2, Colour.RED);
		assertThat(b.addToBoard(3, Colour.RED), equalTo(true));
	}

	@Test
	public void diagonalUpLeftWin(){
		Board b = new Board();
		assertThat(b.addToBoard(0, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(0, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(0, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(0, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(1, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(1, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(1, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(2, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(2, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(3, Colour.BLACK), equalTo(true));
	}
	
	@Test
	public void diagonalUpRightWin(){
		Board b = new Board();
		assertThat(b.addToBoard(0, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(1, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(1, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(2, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(2, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(2, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(3, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(3, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(3, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(3, Colour.RED), equalTo(true));
	}

	@Test
	public void diagonalDownLeftWin(){
		Board b = new Board();
		assertThat(b.addToBoard(3, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(3, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(3, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(3, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(2, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(2, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(2, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(1, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(1, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(0, Colour.RED), equalTo(true));
	}
	
	@Test
	public void diagonalDownRightWin(){
		Board b = new Board();
		assertThat(b.addToBoard(3, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(2, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(2, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(1, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(1, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(1, Colour.BLACK), equalTo(false));
		assertThat(b.addToBoard(0, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(0, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(0, Colour.RED), equalTo(false));
		assertThat(b.addToBoard(0, Colour.BLACK), equalTo(true));

	}
}
