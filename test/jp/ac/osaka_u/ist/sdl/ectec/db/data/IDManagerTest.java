package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IDManagerTest {
	
	@Test
	public void test1() {
		final long correct = 0;
		final long result = IDManager.issuanceMinimumId((short) 0);
		
		assertTrue(correct == result);
	}
	
	@Test
	public void test2() {
		final long correct = 281474976710656L;
		final long result = IDManager.issuanceMinimumId((short) 1);
		
		assertTrue(correct == result);
	}

}
