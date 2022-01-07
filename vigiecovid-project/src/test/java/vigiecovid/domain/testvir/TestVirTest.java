package vigiecovid.domain.testvir;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestVirTest {

	@Test
	public void testTestVir() {
		TestVir testVir = new TestVir(1, 2);
		
		assertEquals(1, testVir.getPositifs());
		assertEquals(2, testVir.getTests());
		assertEquals(5000, Math.round(testVir.getPc()*100));
		
		assertEquals(1, (long) testVir.get("positifs"));
		assertEquals(2, (long) testVir.get("tests"));
		assertEquals(50, (long) testVir.get("pc"));
	}

	@Test
	public void testPlus() {
		TestVir t = new TestVir(1, 2);
		TestVir t2 = new TestVir(10, 20);
		
		t.plus(t2);
		
		assertEquals(11, t.getPositifs());
		assertEquals(22, t.getTests());
		assertEquals(5000, Math.round(t.getPc()*100));
		
		t.avg();
		
		assertEquals(5, t.getPositifs());
		assertEquals(11, t.getTests());
		assertEquals(4545, Math.round(t.getPc()*100));
	}

}
