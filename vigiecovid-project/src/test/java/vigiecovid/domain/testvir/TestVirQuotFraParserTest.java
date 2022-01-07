package vigiecovid.domain.testvir;

import static org.junit.Assert.*;

import org.junit.Test;

import chamette.datasets.EmptyLineException;
import chamette.datasets.ParseException;

public class TestVirQuotFraParserTest {

	@Test
	public void testParser() throws ParseException, EmptyLineException {
		String line0 = "fra;jour;P_f;P_h;P;T_f;T_h;T;cl_age90;pop";
		TestVirQuotFraParser parser = new TestVirQuotFraParser(line0);
		
		String line = "FR;2020-05-13;7;6;13;601;649;1282;09;7763205.6862729";
		
		TestVirQuotFra t = parser.parse(line);
		
		assertEquals("2020-05-13", t.getJour().toString());
		assertEquals(13, t.getPositifs());
		assertEquals(1282, t.getTests());
	}

	@Test
	public void testParserOtherSep() throws ParseException, EmptyLineException {
		String line0 = "fra,jour,P_f,P_h,P,T_f,T_h,T,cl_age90,pop";
		TestVirQuotFraParser parser = new TestVirQuotFraParser(line0);
		
		String line = "FR,2020-05-13,7,6,13,601,649,1282,09,7763205.6862729";
		
		TestVirQuotFra t = parser.parse(line);
		
		assertEquals("2020-05-13", t.getJour().toString());
		assertEquals(13, t.getPositifs());
		assertEquals(1282, t.getTests());
	}

	@Test
	public void testParseToStream() {
		String line0 = "fra;jour;P_f;P_h;P;T_f;T_h;T;cl_age90;pop";
		TestVirQuotFraParser parser = new TestVirQuotFraParser(line0);
		
		String line = "FR;2020-05-13;7;6;13;601;649;1282;09;7763205.6862729";

		TestVirQuotFra t = parser.parseToStream(line).findFirst().get();
			
		assertEquals("2020-05-13", t.getJour().toString());
		assertEquals(13, t.getPositifs());
		assertEquals(1282, t.getTests());
	}

}
