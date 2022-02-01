package vigiecovid.domain.dh;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

public class DhTest {

	@Test
	public void testDh() {
		Dh dh = new Dh("01", "0", LocalDate.parse("2020-03-18"), 1, 2, 3, 4);
		
		assertEquals("01", dh.getDep());
		assertEquals("0", dh.getSexe());
		assertEquals("2020-03-18", dh.getJour().toString());
		assertEquals(1, dh.getHosp());
		assertEquals(2, dh.getRea());
		assertEquals(3, dh.getRad());
		assertEquals(4, dh.getDc());
	}

	@Test
	public void testPlus() {
		Dh dh = new Dh("01", "0", LocalDate.parse("2020-03-18"), 1, 2, 3, 4);
		Dh dh2 = new Dh("02", "1", LocalDate.parse("2020-03-19"), 10, 20, 30, 40);
		
		dh.plus(dh2);
		
		assertEquals("01", dh.getDep());
		assertEquals("0", dh.getSexe());
		assertEquals("2020-03-18", dh.getJour().toString());

		assertEquals(11, dh.getHosp());
		assertEquals(22, dh.getRea());
		assertEquals(33, dh.getRad());
		assertEquals(44, dh.getDc());
	}

	@Test
	public void testClone() {
		Dh dh1 = new Dh("01", "0", LocalDate.parse("2020-03-18"), 1, 2, 3, 4);
		Dh dh2 = new Dh(dh1);
		
		assertEquals("01", dh2.getDep());
		assertEquals("0", dh2.getSexe());
		assertEquals("2020-03-18", dh2.getJour().toString());

		assertEquals(1, dh2.getHosp());
		assertEquals(2, dh2.getRea());
		assertEquals(3, dh2.getRad());
		assertEquals(4, dh2.getDc());
	}

}
