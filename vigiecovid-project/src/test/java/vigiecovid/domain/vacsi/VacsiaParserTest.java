package vigiecovid.domain.vacsi;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

import chamette.datasets.EmptyLineException;
import chamette.datasets.ParseException;

public class VacsiaParserTest {

	@Test
	public void testVacsiaParser() throws ParseException, EmptyLineException {
		String line0 = "fra;clage_vacsi;jour;n_dose1;n_complet;n_rappel;n_cum_dose1;n_cum_complet;"
				+"n_cum_rappel;couv_dose1;couv_complet;couv_rappel";
		VacsiaParser parser = new VacsiaParser(line0);
		
		String line1 = "FR;04;2020-12-27;1;2;3;4;5;6;7.0;8.0;9.0";
		
		Vacsi vacsi = parser.parse(line1);
		assertEquals("04", vacsi.getClage());
		assertEquals("2020-12-27", vacsi.getJour().toString());
		
		assertEquals(1, vacsi.getDose1());
		assertEquals(2, vacsi.getComplet());
		assertEquals(3, vacsi.getRappel());
		
		assertEquals(4, vacsi.getCumDose1());
		assertEquals(5, vacsi.getCumComplet());
		assertEquals(6, vacsi.getCumRappel());
		
		assertEquals(700, Math.round(vacsi.getCouvDose1()*100));
		assertEquals(800, Math.round(vacsi.getCouvComplet()*100));
		assertEquals(900, Math.round(vacsi.getCouvRappel()*100));
	}

	@Test
	public void testVacsiaParserOtherSep() throws ParseException, EmptyLineException {
		String line0 = "fra,clage_vacsi,jour,n_dose1,n_complet,n_rappel,n_cum_dose1,n_cum_complet,"
				+"n_cum_rappel,couv_dose1,couv_complet,couv_rappel";
		VacsiaParser parser = new VacsiaParser(line0);
		
		String line1 = "FR,04,2020-12-27,1,2,3,4,5,6,7.0,8.0,9.0";
		
		Vacsi vacsi = parser.parse(line1);
		assertEquals("04", vacsi.getClage());
		assertEquals("2020-12-27", vacsi.getJour().toString());
	}

	@Test
	public void testParseToStream() {
		String line0 = "fra;clage_vacsi;jour;n_dose1;n_complet;n_rappel;n_cum_dose1;n_cum_complet;"
				+"n_cum_rappel;couv_dose1;couv_complet;couv_rappel";
		VacsiaParser parser = new VacsiaParser(line0);
		
		String line1 = "FR;04;2020-12-27;1;2;3;4;5;6;7.0;8.0;9.0";
		Vacsi vacsi = parser.parseToStream(line1).findFirst().get();
		
		assertEquals("04", vacsi.getClage());
		assertEquals("2020-12-27", vacsi.getJour().toString());
	}

}
