package vigiecovid.domain.dh;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;

public class DhTools {
	
	private static final Logger LOGGER = Logger.getLogger(DhTools.class); 
	
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
			SortedMap<LocalDate, Dh> dhs, String metric) throws Exception {
		
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
	
	/**
	 * Calcule la moyenne mobile sur 7 jours
	 */

	public static TreeMap<LocalDate, Dh> avgOverAWeek(SortedMap<LocalDate, Dh> parentData)
			throws Exception {
		
		TreeMap<LocalDate, Dh> ret = new TreeMap<>();
		
		parentData.entrySet().stream().skip(6).forEach(e -> {
			LOGGER.debug("e: "+e);
			Dh sum = e.getValue().clone();

			for (int i = -6; i < 0; i++) {
				LocalDate k = e.getKey().plusDays(i);
				Dh toAdd = parentData.get(k);
				if (toAdd != null) {
					sum.plus(toAdd);
				}
			}
			ret.put(e.getKey(), sum.avg());
		});		
		return ret;
	}

}
