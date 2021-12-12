package vigiecovid.domain.dh;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

public class DhClAgeTest {

	@Test
	public void testDh() {
		DhClAge dhClAge = new DhClAge("01", "09", LocalDate.parse("2020-03-18"), 1, 2, 3, 4);
		
		assertEquals("01", dhClAge.getReg());
		assertEquals("09", dhClAge.getClAge());
		assertEquals("2020-03-18", dhClAge.getJour().toString());
		assertEquals(1, dhClAge.getHosp());
		assertEquals(2, dhClAge.getRea());
		assertEquals(3, dhClAge.getRad());
		assertEquals(4, dhClAge.getDc());
	}

	@Test
	public void testPlus() {
		DhClAge dhClAge = new DhClAge("01", "09", LocalDate.parse("2020-03-18"), 1, 2, 3, 4);
		DhClAge dhClAge2 = new DhClAge("02", "19", LocalDate.parse("2020-03-19"), 10, 20, 30, 40);
		
		dhClAge.plus(dhClAge2);
		
		assertEquals("01", dhClAge.getReg());
		assertEquals("09", dhClAge.getClAge());
		assertEquals("2020-03-18", dhClAge.getJour().toString());

		assertEquals(11, dhClAge.getHosp());
		assertEquals(22, dhClAge.getRea());
		assertEquals(33, dhClAge.getRad());
		assertEquals(44, dhClAge.getDc());
	}

}
