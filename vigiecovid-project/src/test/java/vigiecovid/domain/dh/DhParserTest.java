package vigiecovid.domain.dh;

import static org.junit.Assert.*;

import org.junit.Test;

import chamette.datasets.EmptyLineException;
import chamette.datasets.ParseException;

public class DhParserTest {

	@Test
	public void testParser() throws ParseException, EmptyLineException {
		String line0 = "\"dep\";\"sexe\";\"jour\";\"hosp\";\"rea\";\"HospConv\";\"SSR_USLD\";"
				+ "\"autres\";\"rad\";\"dc\"";
		DhParser parser = new DhParser(line0);
		
		String line = "\"59\";\"0\";2021-10-08;221;43;107;61;10;18531;4041";
		
		Dh dh = parser.parse(line);
		
		assertEquals("59", dh.getDep());
		assertEquals("0", dh.getSexe());
		assertEquals("2021-10-08", dh.getJour().toString());
		assertEquals(221, dh.getHosp());
		assertEquals(43, dh.getRea());
		assertEquals(18531, dh.getRad());
		assertEquals(4041, dh.getDc());

	}

	@Test
	public void testParserOtherSep() throws ParseException, EmptyLineException {
		String line0 = "fra,clage_vacsi,jour,n_dose1,n_complet,n_rappel,n_cum_dose1,n_cum_complet,"
				+"n_cum_rappel,couv_dose1,couv_complet,couv_rappel";
		DhParser parser = new DhParser(line0);
		
		String line = "\"59\",\"0\",2021-10-08,221,43,107,61,10,18531,4041";
		
		Dh dh = parser.parse(line);
		
		assertEquals("59", dh.getDep());
		assertEquals("0", dh.getSexe());
		assertEquals("2021-10-08", dh.getJour().toString());
	}

	@Test
	public void testParseToStream() {
		String line0 = "\"dep\";\"sexe\";\"jour\";\"hosp\";\"rea\";\"HospConv\";\"SSR_USLD\";"
				+ "\"autres\";\"rad\";\"dc\"";
		DhParser parser = new DhParser(line0);
		
		String line = "\"59\";\"0\";2021-10-08;221;43;107;61;10;18531;4041";

		Dh dh = parser.parseToStream(line).findFirst().get();
			
		assertEquals("59", dh.getDep());
		assertEquals("0", dh.getSexe());
		assertEquals("2021-10-08", dh.getJour().toString());
	}

}
