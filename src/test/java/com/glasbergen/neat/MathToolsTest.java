package com.glasbergen.neat;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

public class MathToolsTest {

	@Test
	public void testSigmoid() {
		assertThat(MathTools.sigmoid(1000), equalTo(1.0));
		assertThat(MathTools.sigmoid(0), equalTo(0.5));
		assertThat(MathTools.sigmoid(-1000), equalTo(0.0));
	}

}
