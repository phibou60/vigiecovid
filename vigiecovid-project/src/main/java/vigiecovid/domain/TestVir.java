package vigiecovid.domain;

import static chamette.tools.CsvTools.getSeparator;
import static chamette.tools.CsvTools.normalizeDate;
import static chamette.tools.CsvTools.unquote;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import chamette.datasets.CommonDataset;
import chamette.datasets.Dataset;
import chamette.datasets.Datasets;

public class TestVir {

	public long positifs = 0;
	public long tests = 0;

	public TestVir(long positifs, long tests) {
		this.positifs = positifs;
		this.tests = tests;
	}
	
	public long getTests() {
		return tests;
	}
	
	public long getPositifs() {
		return positifs;
	}
	
	public double getPc() {
		return 100D * positifs / tests;
	}
	
	/**
	 * Récupération du dernier jour contenu dans les datas.
	 * 
	 * @param context Permet d'accéder aux Datasets
	 */
	public static LocalDate getLastDay(javax.servlet.ServletContext context) throws Exception {
		TreeMap<LocalDate, TestVir> cumulByDay = TestVir.cumulTestVirByDay(context, null, false);
		return cumulByDay.lastKey();
	}
	
	/**
	 * Récupère les tests virologiques par département pour une semaine.
	 * 
	 * @param context Permet d'accéder aux Datasets
	 * @param lastDay Jour de calcul
	 * @return Un tableau par département des tests virologiques cumulés sur 7 jours
	 * @throws Exception
	 */
	public static TreeMap<String, TestVir> cumulTestVirByDepLastWeek(javax.servlet.ServletContext context, LocalDate lastDay) throws Exception {
		Logger logger = Logger.getLogger("cumulTestVirByDepLastWeek");

		String myDatasetName = "cumulTestVirByDepLastWeek.v0."+lastDay;
		String parentDatasetName = "sp-pos-quot-dep";
	
		Datasets datasets = (Datasets) context.getAttribute("datasets");
		
		if (datasets.exists(myDatasetName)) {
			logger.info("Return cache: "+myDatasetName);
			return (TreeMap<String, TestVir>) datasets.get(myDatasetName).getData();
		}
		
		TreeMap<String, TestVir> ret = new TreeMap<>();
		
		if (!datasets.exists(parentDatasetName)) {
			return ret;
		}
		
		logger.info("Calculate new : "+myDatasetName);

		Dataset myDataset = new CommonDataset(myDatasetName);
		String[] lines = (String[]) datasets.get(parentDatasetName).getData();

		LocalDate fromDate = lastDay.minusDays(6);
		
		long count = 0;
		String sep = "";
		for (String line : lines) {
			line = line.trim();
			if (count == 0) sep = getSeparator(line);
			
			String[] splits = line.split(sep);
			if (count > 0 && splits[4].equals("0")) {
				
				String cle = splits[0];
				LocalDate date = LocalDate.parse(normalizeDate(unquote(splits[1])));
				
				long[] valeurs = new long[2];
				for (int i=0; i<valeurs.length; i++) valeurs[i] = 0;
				if (date.compareTo(fromDate) >= 0 && date.compareTo(lastDay) <= 0) { 
					for (int i=0; i<valeurs.length; i++) {
						try {
							valeurs[i] = Long.parseLong(splits[i+2]);
						} catch (Exception e) {
							logger.error("Exception: "+e+", line: "+line);
							valeurs[i] = 0;
						}
					}
				}
				
				if (ret.containsKey(cle)) {
					TestVir cumul = ret.get(cle);
					cumul.positifs += valeurs[0];
					cumul.tests += valeurs[1];
				} else ret.put(cle, new TestVir(valeurs[0], valeurs[1]));

			}
			count++;
		}
		myDataset.setData(ret);
		
		datasets.add(myDataset);
		datasets.get(parentDatasetName).addChildDataset(myDataset);

		return ret;
	}

	/**
	 * Retourne les tests virologiques par jour (avec possibilité de filtrer sur le département).
	 * 
	 * @param context Permet d'accéder aux Datasets
	 * @param dep Département (facultatif. Peut être null)
	 * @param metropoleSeule true indique que l'on veut uniquement les données de la métropole
	 * @return Un tableau des tests virologiques par jour
	 * @throws Exception
	 */
	public static TreeMap<LocalDate, TestVir> cumulTestVirByDay(javax.servlet.ServletContext context, String dep, boolean metropoleSeule) throws Exception {
		Logger logger = Logger.getLogger("cumulTestVirByDay");

		String myDatasetName = "cumulTestVirByDay."+dep+"."+metropoleSeule;
		String parentDatasetName = "sp-pos-quot-dep";
	
		Datasets datasets = (Datasets) context.getAttribute("datasets");
		
		if (datasets.exists(myDatasetName)) {
			logger.info("Return cache: "+myDatasetName);
			return (TreeMap<LocalDate, TestVir>) datasets.get(myDatasetName).getData();
		}
		
		TreeMap<LocalDate, TestVir> ret = new TreeMap<>();
		
		if (!datasets.exists(parentDatasetName)) {
			return ret;
		}
		
		logger.info("Calculate new : "+myDatasetName);

		Dataset myDataset = new CommonDataset(myDatasetName);
		String[] lines = (String[]) datasets.get(parentDatasetName).getData();

		long count = 0;
		String sep = "";
		for (String line : lines) {
			line = line.trim();
			if (count == 0) sep = getSeparator(line);
			
			String[] splits = line.split(sep);

			if (count > 0 && splits[4].equals("0") && ((dep == null && (!metropoleSeule || (metropoleSeule && splits[0].length() == 2))) || splits[0].equals(dep))) {
				
				LocalDate jour = LocalDate.parse(normalizeDate(unquote(splits[1])));
				
				long[] valeurs = new long[2];
				for (int i=0; i<valeurs.length; i++) {
					try {
						valeurs[i] = Long.parseLong(splits[i+2]);
					} catch (Exception e) {
						logger.error("Exception: "+e+", line: "+line);
						valeurs[i] = 0;
					}
				}
				
				if (ret.containsKey(jour)) {
					TestVir cumul = ret.get(jour);
					cumul.positifs += valeurs[0];
					cumul.tests += valeurs[1];
				} else ret.put(jour, new TestVir(valeurs[0], valeurs[1]));
				
			}
			count++;
		}
		myDataset.setData(ret);
		
		datasets.add(myDataset);
		datasets.get(parentDatasetName).addChildDataset(myDataset);

		return ret;
	}

	/**
	 * Retourne les tests virologiques par semaine (avec possibilité de filtrer sur le département).
	 * 
	 * @param context Permet d'accéder aux Datasets
	 * @param dep Département (facultatif. Peut être null)
	 * @param metropoleSeule true indique que l'on veut uniquement les données de la métropole
	 * @return Un tableau des tests virologiques par semaine (la journée indique le dernier jour)
	 * @throws Exception
	 */
	public static TreeMap<LocalDate, TestVir> cumulTestVirByWeeks(javax.servlet.ServletContext context, String dep, boolean metropoleSeule) throws Exception {
		
		TreeMap<LocalDate, TestVir> byDays = cumulTestVirByDay(context, dep, metropoleSeule);
		TreeMap<LocalDate, TestVir> ret = new TreeMap<>();
		
		for (Map.Entry<LocalDate, TestVir> entry : byDays.entrySet()) {
			TestVir testVir = new TestVir(entry.getValue().positifs, entry.getValue().tests);
			for (int i = 1; i <= 6; i++) {
				LocalDate key = entry.getKey().minusDays(i);
				TestVir testVirNew = byDays.get(key);
				if (testVirNew != null) {
					testVir.tests += testVirNew.tests;
					testVir.positifs += testVirNew.positifs;
				}
			}
			ret.put(entry.getKey(), testVir);
		}
		
		return ret;
	}

	/**
	 * Calcule le taux de variation par semaine (taux de reproduction)
	 * (avec possibilité de filtrer sur le département).
	 * 
	 * @param context Permet d'accéder aux Datasets
	 * @param dep Département (facultatif. Peut être null)
	 * @param metropoleSeule true indique que l'on veut uniquement les données de la métropole
	 * @return Un tableau du taux de variation d'une semaine à l'autre
	 * @throws Exception
	 */
	public static TreeMap<LocalDate, Double> reproductionTestVirByWeeks(javax.servlet.ServletContext context, String dep, boolean metropoleSeule) throws Exception {
		Logger logger = Logger.getLogger("variationTestVirByWeeks");
		logger.setLevel(Level.DEBUG);
		
		TreeMap<LocalDate, TestVir> byDays = cumulTestVirByDay(context, dep, metropoleSeule);
		TreeMap<LocalDate, Double> ret = new TreeMap<>();
		
		for (Map.Entry<LocalDate, TestVir> entry : byDays.entrySet()) {
			LocalDate keyPrevWeek = entry.getKey().minusDays(7);
			TestVir testVirPrevWeek = byDays.get(keyPrevWeek);
			if (testVirPrevWeek != null) {
				//double taux = ( (double) entry.getValue().getPositifs() - testVirPrevWeek.getPositifs() ) / testVirPrevWeek.getPositifs();
				double taux = (double) entry.getValue().getPositifs() / testVirPrevWeek.getPositifs();
				ret.put(entry.getKey(), taux);
			}
		}
		
		logger.debug("ret.size(): " + ret.size());
		return ret;
	}

	/**
	 * Calcule la variation d'incidence d'une semaine à l'autre par départements.
	 * 
	 * @param context Permet d'accéder aux Datasets
	 * @param lastDay Jour de calcul
	 * @return Un tableau avec en clé le numéro de département (en String) et en valeur le % de variation.
	 * @throws Exception
	 */
	public static TreeMap<String, Double> getVariations(javax.servlet.ServletContext context, LocalDate lastDay) throws Exception {
		Logger logger = Logger.getLogger("getVariation");

		TreeMap<String, TestVir> lastWeek = cumulTestVirByDepLastWeek(context, lastDay);
		TreeMap<String, TestVir> previousWeek = cumulTestVirByDepLastWeek(context, lastDay.minusDays(7));
		
		TreeMap<String, Double> ret = new TreeMap<>();
		for (Map.Entry<String, TestVir> entry : lastWeek.entrySet()) {
			if (previousWeek.get(entry.getKey()) != null) {
				long positifWeek = entry.getValue().getPositifs();
				long positifPrev = previousWeek.get(entry.getKey()).getPositifs();
				double pc = 0;
				if (positifPrev > 0) {
					pc = 100.0D * (positifWeek - positifPrev) / positifPrev;
				}
				ret.put(entry.getKey(), pc);
			}
		}
		return ret;
	}
	
}