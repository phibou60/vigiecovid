package vigiecovid.domain.dh;

import static org.junit.Assert.*;

import org.junit.Test;

import chamette.datasets.EmptyLineException;
import chamette.datasets.ParseException;

public class DhClAgeParserTest {

	@Test
	public void testParser() throws ParseException, EmptyLineException {
		String line0 = "\"reg\";\"cl_age90\";\"jour\";\"hosp\";\"rea\";\"HospConv\";\"SSR_USLD\";"
				+"\"autres\";\"rad\";\"dc\"";
		DhClAgeParser parser = new DhClAgeParser(line0);
		
		String line = "\"32\";\"0\";2021-06-18;1481;238;653;578;12;37657;9206";
		
		DhClAge dhClAge = parser.parse(line);
		
		assertEquals("32", dhClAge.getReg());
		assertEquals("0", dhClAge.getClAge());
		assertEquals("2021-06-18", dhClAge.getJour().toString());
		assertEquals(1481, dhClAge.getHosp());
		assertEquals(238, dhClAge.getRea());
		assertEquals(37657, dhClAge.getRad());
		assertEquals(9206, dhClAge.getDc());

	}

	@Test
	public void testParserOtherSep() throws ParseException, EmptyLineException {
		String line0 = "\"reg\",\"cl_age90\",\"jour\",\"hosp\",\"rea\",\"HospConv\",\"SSR_USLD\","
				+"\"autres\",\"rad\",\"dc\"";
		DhClAgeParser parser = new DhClAgeParser(line0);
		
		String line = "\"32\",\"0\",2021-06-18,1481,238,653,578,12,37657,9206";
		
		DhClAge dhClAge = parser.parse(line);
		
		assertEquals("32", dhClAge.getReg());
		assertEquals("0", dhClAge.getClAge());
		assertEquals("2021-06-18", dhClAge.getJour().toString());
		assertEquals(1481, dhClAge.getHosp());
		assertEquals(238, dhClAge.getRea());
		assertEquals(37657, dhClAge.getRad());
		assertEquals(9206, dhClAge.getDc());
	}

	@Test
	public void testParseToStream() {
		String line0 = "\"reg\";\"cl_age90\";\"jour\";\"hosp\";\"rea\";\"HospConv\";\"SSR_USLD\";"
				+"\"autres\";\"rad\";\"dc\"";
		DhClAgeParser parser = new DhClAgeParser(line0);
		
		String line = "\"32\";\"0\";2021-06-18;1481;238;653;578;12;37657;9206";

		DhClAge dhClAge = parser.parseToStream(line).findFirst().get();
			
		assertEquals("32", dhClAge.getReg());
		assertEquals("0", dhClAge.getClAge());
		assertEquals("2021-06-18", dhClAge.getJour().toString());
		assertEquals(1481, dhClAge.getHosp());
		assertEquals(238, dhClAge.getRea());
		assertEquals(37657, dhClAge.getRad());
		assertEquals(9206, dhClAge.getDc());
	}

}
