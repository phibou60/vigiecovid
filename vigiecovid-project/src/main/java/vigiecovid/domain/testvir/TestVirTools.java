package vigiecovid.domain.testvir;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class TestVirTools {
	
	/**
	 * Calcul de l'évolution de l'incidence dans le passé
	 * @param byWeeks
	 * @param population
	 * @return
	 * @throws Exception
	 */
	
	public static TreeMap<LocalDate, Integer> calculEvolIncidence(
			TreeMap<LocalDate, TestVir> byWeeks, long population) throws Exception {
		
		TreeMap<LocalDate, Integer> incidences = new TreeMap<>();
		for (Map.Entry<LocalDate, TestVir> entry : byWeeks.entrySet()) {
			int incidence = Math.round(100_000 * entry.getValue().getPositifs() / population);
			incidences.put(entry.getKey(), incidence);
		}
		return incidences;
		
	}
	
	/**
	 * Calcul projection de l'évolution de l'incidence sous la forme d'un modèle linéaire
	 * @param incidences
	 * @return
	 * @throws Exception
	 */
	public static TreeMap<LocalDate, Double> calculLinearProjectionlIncidence(
			TreeMap<LocalDate, Integer> incidences) throws Exception {
		
		LocalDate lastDayOfData = incidences.lastKey();
		
		LocalDate dateMinProj = lastDayOfData.minusWeeks(2);
		LocalDate dateMaxProj = lastDayOfData.plusWeeks(2);
	
		SimpleRegression simpleRegression = new SimpleRegression();
		for (Map.Entry<LocalDate, Integer> entry : incidences.entrySet()) {
			if (entry.getKey().compareTo(dateMinProj) > 0) {
				simpleRegression.addData((double) entry.getKey().toEpochDay(), (double) entry.getValue());
			}
		}
	
		TreeMap<LocalDate, Double> proj = new TreeMap<>();
		for (LocalDate jour = dateMinProj; jour.compareTo(dateMaxProj) <= 0; jour = jour.plusDays(1)) {
			proj.put(jour, simpleRegression.predict((double) jour.toEpochDay()));
		}
		
		return proj;
	}
	
	/**
	 * Calcul projection de l'évolution de l'incidence sous la forme d'un modèle polynomial
	 * @param incidences
	 * @return
	 * @throws Exception
	 */
	public static TreeMap<LocalDate, Double> calculPolynomialProjectionlIncidence(
			TreeMap<LocalDate, Integer> incidences) throws Exception {
		
		LocalDate lastDayOfData = incidences.lastKey();
		
		LocalDate dateMinProj = lastDayOfData.minusWeeks(2);
		LocalDate dateMaxProj = lastDayOfData.plusWeeks(2);
	
		PolynomialCurveFitter polynomialCurveFitter = PolynomialCurveFitter.create(2);
		List<WeightedObservedPoint> points = new ArrayList<>();

		for (Map.Entry<LocalDate, Integer> entry : incidences.entrySet()) {
			if (entry.getKey().compareTo(dateMinProj) >= 0) {
				points.add(new WeightedObservedPoint(1d, (double) entry.getKey().toEpochDay(),
						(double) entry.getValue()));
			}
		}
		double[] coeffs = polynomialCurveFitter.fit(points);
		PolynomialFunction polynomialFunction = new PolynomialFunction(coeffs);

		TreeMap<LocalDate, Double> proj = new TreeMap<>();

		for (LocalDate jour = dateMinProj; jour.isBefore(dateMaxProj); jour = jour.plusDays(1)) {
			proj.put(jour, polynomialFunction.value(jour.toEpochDay()));
		}
		
		return proj;
	}

}
