package jp.ac.osaka_u.ist.sdl.ectec.db;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DBMakerTest {

	private static DBConnectionManager manager;

	private static DBMaker maker;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		manager = new DBConnectionManager(new SQLiteDBConfig("test-resources"
				+ File.separator + "db" + File.separator + "test.db"), 10000);
		maker = new DBMaker(manager);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		manager.close();
	}

	@Test
	public void test() {
		try {
			maker.makeDb(true);
			assertTrue(true);
		} catch (Exception e) {
			fail();
		}
	}

}
