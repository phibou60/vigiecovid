package chamette.datascience;

import static org.junit.Assert.*;

import org.junit.Test;

public class CalculsTest {

	@Test
	public void testTauxDeVariation() {
		double result = Calculs.tauxDeVariation(100, 120);
		assertEquals(20, Math.round(result*100));
		
		result = Calculs.tauxDeVariation(100D, 120D);
		assertEquals(20, Math.round(result*100));
		
		result = Calculs.tauxDeVariation(200, 220);
		assertEquals(10, Math.round(result*100));
		
		result = Calculs.tauxDeVariation(200D, 220D);
		assertEquals(10, Math.round(result*100));
	}

	@Test
	public void testRatioDeVariation() {
		double result = Calculs.ratio(100, 120);
		assertEquals(120, Math.round(result*100));
		
		result = Calculs.ratio(100D, 120D);
		assertEquals(120, Math.round(result*100));
		
		result = Calculs.ratio(200, 220);
		assertEquals(110, Math.round(result*100));
		
		result = Calculs.ratio(200D, 220D);
		assertEquals(110, Math.round(result*100));
	}

	@Test
	public void testEvolutionSurPlusieursPeriodes() {
		double result = Calculs.evolutionSurPlusieursPeriodes(100, 0.10D, 2);
		assertEquals(121, Math.round(result));
	}

	@Test
	public void testTauxParPeriodes() {
		double result = Calculs.tauxParPeriodes(0.33, 3);
		assertEquals(10, Math.round(result*100));
		
		result = Calculs.tauxParPeriodes(2.138428377, 12);
		assertEquals(10, Math.round(result*100));
	}

	@Test
	public void testNbDePeriodes() {
		double result = Calculs.nbDePeriodes(0.10, 1);
		assertEquals(730, Math.round(result*100));
	}
	
}
