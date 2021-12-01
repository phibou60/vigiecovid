package chamette.datascience;

public class Calculs {

	/**
	 * Calcul taux de variation d'une valeur à une autre
	 */

	public static double tauxDeVariation(long from, long to) {
		return ((double) to - from) / from;
	}

	/**
	 * Calcul taux de variation d'une valeur à une autre
	 */

	public static double tauxDeVariation(double from, double to) {
		return (to - from) / from;
	}

	/**
	 * Calcul ratio entre 2 valeurs.<br>
	 * Le taux de variation est égal au ratio moins 1.
	 */

	public static double ratio(long from, long to) {
		return (double) to / from;
	}

	/**
	 * Calcul ratio entre 2 valeurs.<br>
	 * Le taux de variation est égal au ratio moins 1.
	 */

	public static double ratio(double from, double to) {
		return to / from;
	}

	/**
	 * Calcul de l'application d'un taux sur n périodes.
	 */
	
	public static double evolutionSurPlusieursPeriodes(double from, double taux, long periodes) {
		return from * Math.pow(1D + taux, (double) periodes);
	}

	/**
	 * Calcul du taux par période à partir du taux sur l'ensemble des n périodes.
	 */
	
	public static double tauxParPeriodes(double taux, long periodes) {
		return Math.pow(1D + taux, 1D / periodes) - 1;
	}

	/**
	 * Calcul du nb de périodes pour atteindre, à partir d'un taux par période,
	 * un taux de variation global.
	 */
	
	public static double nbDePeriodes(double txInit, double txFinal) {
		if (txFinal < txInit) {
			return Double.NaN;
		}
	
		double txParDix = tauxParPeriodes(txInit, 10);
		double ret = 0;
		double value = 100;
		double valueFinal = value * (1 + txFinal);
		while (value < valueFinal) {
			value = value * (1 + txParDix);
			ret += 0.1D;
		}
				
		return ret;
	}
	
}
