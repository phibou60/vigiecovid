package vigiecovid.domain.testvir;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

public class TestVirQuotFraTest {

	@Test
	public void testTestVirQuotFra() {

		TestVirQuotFra t = new TestVirQuotFra(LocalDate.parse("2020-03-18"), 1, 2);
		
		assertEquals("2020-03-18", t.getJour().toString());
		assertEquals("2020-03-18;1;2", t.toString());

		TestVirQuotFra t2 = t.clone(); 
		
		assertEquals(1, t2.getPositifs());
		assertEquals(2, t2.getTests());
		assertEquals(5000, Math.round(t2.getPc()*100));
		assertEquals("2020-03-18", t2.getJour().toString());
	}

}
