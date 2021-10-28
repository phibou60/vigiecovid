package vigiecovid.domain;

import static chamette.tools.CsvTools.getSeparator;
import static chamette.tools.CsvTools.normalizeDate;
import static chamette.tools.CsvTools.unquote;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

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
	
	public long getPc() {
		return Math.round((double)positifs * 100 /tests);
	}
	
	/**
	 * Récupération du dernier jour contenu dans les datas
	 * @param context Permet d'accéder aux Datasets
	 */
	public static LocalDate getLastDay(javax.servlet.ServletContext context) throws Exception {
		TreeMap<LocalDate, TestVir> cumulByDay = TestVir.cumulTestVirByDay(context, null, false);
		return cumulByDay.lastKey();
	}
	
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
	 * Calcule la variation d'incidence d'une semaine à l'autre
	 * @param context
	 * @param lastDay
	 * @return
	 * @throws Exception
	 */
	public static TreeMap<String, Double> getVariations(javax.servlet.ServletContext context, LocalDate lastDay) throws Exception {
		Logger logger = Logger.getLogger("getVariation");

		TreeMap<String, TestVir> lastWeek = cumulTestVirByDepLastWeek(context, lastDay);
		TreeMap<String, TestVir> previousWeek = cumulTestVirByDepLastWeek(context, lastDay.minusDays(7));
		
		TreeMap<String, Double> ret = new TreeMap<String, Double>();
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