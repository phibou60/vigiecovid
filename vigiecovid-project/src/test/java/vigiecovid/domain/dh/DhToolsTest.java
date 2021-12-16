package vigiecovid.domain.dh;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.junit.Test;

public class DhToolsTest {
	private static final Logger LOGGER = Logger.getLogger(DhToolsTest.class);

	@Test
	public void testCalculLinearProjection() throws Exception {
		LocalDate fromDate = LocalDate.of(2021, 11, 1);
		TreeMap<LocalDate, Dh> dhs = new TreeMap<>();
		for (int i = 1; i< 15; i++) {
			LocalDate jour = fromDate.plusDays(i);
			dhs.put(jour, new Dh("01", "1", jour, i, i*10, i*100, i*1000));
		}
		
		TreeMap<LocalDate, Double> proj = DhTools.calculLinearProjection(dhs, "hosp");
		
		for (int i = 1; i< 29; i++) {
			LocalDate jour = fromDate.plusDays(i);
			LOGGER.debug(jour +" = " + Math.round(proj.get(jour)*100));
			assertEquals(i*100, Math.round(proj.get(jour)*100));
		}

		proj = DhTools.calculLinearProjection(dhs, "rea");

		for (int i = 1; i< 29; i++) {
			LocalDate jour = fromDate.plusDays(i);
			LOGGER.debug(jour +" = " + Math.round(proj.get(jour)*100));
			assertEquals(i*1000, Math.round(proj.get(jour)*100));
		}

		proj = DhTools.calculLinearProjection(dhs, "dc");
		
		for (int i = 1; i< 29; i++) {
			LocalDate jour = fromDate.plusDays(i);
			LOGGER.debug(jour +" = " + Math.round(proj.get(jour)*100));
			assertEquals(i*100000, Math.round(proj.get(jour)*100));
		}
	}

	@Test
	public void testCalculPolynomialProjection() throws Exception {
		LocalDate fromDate = LocalDate.of(2021, 11, 1);
		TreeMap<LocalDate, Dh> dhs = new TreeMap<>();
		for (int i = 1; i< 15; i++) {
			LocalDate jour = fromDate.plusDays(i);
			dhs.put(jour, new Dh("01", "1", jour, i, i*10, i*100, i*1000));
		}
		
		TreeMap<LocalDate, Double> proj = DhTools.calculPolynomialProjection(dhs, "hosp");
		
		for (int i = 1; i< 29; i++) {
			LocalDate jour = fromDate.plusDays(i);
			LOGGER.debug(jour +" = " + Math.round(proj.get(jour)*100));
			assertEquals(i*100, Math.round(proj.get(jour)*100));
		}

		proj = DhTools.calculPolynomialProjection(dhs, "rea");

		for (int i = 1; i< 29; i++) {
			LocalDate jour = fromDate.plusDays(i);
			LOGGER.debug(jour +" = " + Math.round(proj.get(jour)*100));
			assertEquals(i*1000, Math.round(proj.get(jour)*100));
		}

		proj = DhTools.calculPolynomialProjection(dhs, "dc");
		
		for (int i = 1; i< 29; i++) {
			LocalDate jour = fromDate.plusDays(i);
			LOGGER.debug(jour +" = " + Math.round(proj.get(jour)*100));
			assertEquals(i*100000, Math.round(proj.get(jour)*100));
		}
		
	}

}
