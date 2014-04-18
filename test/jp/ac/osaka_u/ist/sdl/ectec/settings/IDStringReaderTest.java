package jp.ac.osaka_u.ist.sdl.ectec.settings;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class IDStringReaderTest {

	@Test
	public void test1() throws Exception {
		List<Long> result = IDStringReader.read("1");
		assertTrue(result.size() == 1);
	}

	@Test
	public void test2() throws Exception {
		List<Long> result = IDStringReader.read("1,2");
		assertTrue(result.size() == 2);
	}
	
	@Test
	public void test3() throws Exception {
		List<Long> result = IDStringReader.read("1-3");
		assertTrue(result.size() == 3);
	}
	
	@Test
	public void test4() throws Exception {
		List<Long> result = IDStringReader.read("1-3,5");
		assertTrue(result.size() == 4);
	}
	
	@Test
	public void test5() throws Exception {
		List<Long> result = IDStringReader.read("1-3,5-6");
		assertTrue(result.size() == 5);
	}
	
	@Test
	public void test6() throws Exception {
		List<Long> result = IDStringReader.read("1-3,5,6");
		assertTrue(result.size() == 5);
	}
	
	@Test
	public void test7() throws Exception {
		List<Long> result = IDStringReader.read("1-3,5-6,7");
		assertTrue(result.size() == 6);
	}
	
	@Test
	public void test8() throws Exception {
		List<Long> result = IDStringReader.read("1,5-6,7,9-11");
		assertTrue(result.size() == 7);
	}
	
}
