package vigiecovid.domain.dh;

import static org.junit.Assert.*;
import org.junit.Test;

public class DhTest {

	@Test
	public void testDh() {
		Dh dh = new Dh(1, 2, 3, 4);
		
		assertEquals(1, dh.getHosp());
		assertEquals(2, dh.getRea());
		assertEquals(3, dh.getRad());
		assertEquals(4, dh.getDc());
	}

	@Test
	public void testPlus() {
		Dh dh = new Dh(1, 2, 3, 4);
		Dh dh2 = new Dh(10, 20, 30, 40);
		
		dh.plus(dh2);
		assertEquals(11, dh.getHosp());
		assertEquals(22, dh.getRea());
		assertEquals(33, dh.getRad());
		assertEquals(44, dh.getDc());
	}

}
