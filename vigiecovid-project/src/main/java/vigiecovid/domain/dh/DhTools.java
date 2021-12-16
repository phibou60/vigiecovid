package vigiecovid.domain.dh;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class DhTools {
	
	/**
	 * Calcul projection de l'évolution de l'incidence sous la forme d'un modèle linéaire
	 * @param incidences
	 * @return
	 * @throws Exception
	 */
	
	public static TreeMap<LocalDate, Double> calculLinearProjection(
			TreeMap<LocalDate, Dh> dhs, String metric) throws Exception {
		
		LocalDate lastDayOfData = dhs.lastKey();
		
		LocalDate dateMinProj = lastDayOfData.minusWeeks(2);
		LocalDate dateMaxProj = lastDayOfData.plusWeeks(2);
	
		SimpleRegression simpleRegression = new SimpleRegression();

		dhs
			.tailMap(dateMinProj)
			.forEach((jour, value) -> 
				simpleRegression.addData((double) jour.toEpochDay(), value.get(metric))
			);
	
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
	
	public static TreeMap<LocalDate, Double> calculPolynomialProjection(
			TreeMap<LocalDate, Dh> dhs, String metric) throws Exception {
		
		LocalDate lastDayOfData = dhs.lastKey();
		
		LocalDate dateMinProj = lastDayOfData.minusWeeks(2);
		LocalDate dateMaxProj = lastDayOfData.plusWeeks(2);
	
		PolynomialCurveFitter polynomialCurveFitter = PolynomialCurveFitter.create(2);
		List<WeightedObservedPoint> points = new ArrayList<>();

		dhs
			.tailMap(dateMinProj)
			.forEach((jour, value) -> 
				points.add(new WeightedObservedPoint(1d, (double) jour.toEpochDay(), value.get(metric)))
			);

		double[] coeffs = polynomialCurveFitter.fit(points);
		PolynomialFunction polynomialFunction = new PolynomialFunction(coeffs);

		TreeMap<LocalDate, Double> proj = new TreeMap<>();

		for (LocalDate jour = dateMinProj; jour.compareTo(dateMaxProj) <= 0; jour = jour.plusDays(1)) {
			proj.put(jour, polynomialFunction.value(jour.toEpochDay()));
		}
		
		return proj;
	}

}
