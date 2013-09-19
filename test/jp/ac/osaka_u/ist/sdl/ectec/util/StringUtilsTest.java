package jp.ac.osaka_u.ist.sdl.ectec.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void test() {
		final List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		final String result = StringUtils.convertListToString(list, ",");

		assertTrue(result.equals("1,2,3"));
	}

	@Test
	public void test2() {
		final String str = "1,2,3,100,100,200";
		final List<Long> result = new ArrayList<Long>();
		StringUtils.convertStringToCollection(result, str);
		assertTrue(result.size() == 6);
	}

	@Test
	public void test3() {
		final String str = "1,2,3,100,100,200";
		final List<Long> result = new ArrayList<Long>();
		StringUtils.convertStringToCollection(result, str);
		assertTrue(result.get(2) == 3);
	}

}
