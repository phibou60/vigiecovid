package vigiecovid.domain;

import static chamette.tools.CsvTools.getSeparator;
import static chamette.tools.CsvTools.normalizeDate;
import static chamette.tools.CsvTools.unquote;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import chamette.datasets.CommonDataset;
import chamette.datasets.Dataset;
import chamette.datasets.Datasets;
import vigiecovid.domain.dh.Dh;

public class DonneesHospitalieres {
/*	
	public static TreeMap<LocalDate, Dh> getByDates(ServletContext context) throws Exception {
		Logger logger = Logger.getLogger("lectureDonneesHospitalieres");

		String myDatasetName = "dh-cumul-byDates";
		String parentDatasetName = "donnees-hospitalieres-covid19";
	
		Datasets datasets = (Datasets) context.getAttribute("datasets");
		
		if (datasets.exists(myDatasetName)) {
			logger.info("Return cache: "+myDatasetName);
			return (TreeMap<LocalDate, Dh>) datasets.get(myDatasetName).getData();
		}
		
		TreeMap<LocalDate, Dh> ret = new TreeMap<>();
		
		if (!datasets.exists(parentDatasetName)) {
			return ret;
		}
		
		logger.info("Calculate new : "+myDatasetName);

		Dataset myDataset = new CommonDataset(myDatasetName);
		String[] lines = (String[]) datasets.get(parentDatasetName).getData();

		// Attention : certains jours l'ordre de colonnes change ou
		// des colonnes sont insérées.
		// Il faut donc se basser sur les entêtes de colonnes
		int colHosp = 3;
		int colRea  = 4;
		int colRad  = 5;
		int colDc   = 6;

		long count = 0;
		String sep = "";
		for (String line : lines) {
			line = line.trim();
			if (count == 0) {
				sep = getSeparator(line);
				String[] splits = line.split(sep);
				for (int i=0; i<splits.length; i++) {
					if (unquote(splits[i]).equals("hosp")) colHosp = i;
					if (unquote(splits[i]).equals("rea"))  colRea = i;
					if (unquote(splits[i]).equals("rad"))  colRad = i;
					if (unquote(splits[i]).equals("dc"))   colDc = i;
				}
			}
			String[] splits = line.split(sep);
			if (count > 0 && unquote(splits[1]).equals("0")) {
				LocalDate jour = LocalDate.parse(normalizeDate(unquote(splits[2])));
				
				long[] valeurs = new long[4];
				try {
					valeurs[0] = Long.parseLong(unquote(splits[colHosp]));
					valeurs[1] = Long.parseLong(unquote(splits[colRea]));
					valeurs[2] = Long.parseLong(unquote(splits[colRad]));
					valeurs[3] = Long.parseLong(unquote(splits[colDc]));
				} catch (Exception e) {
					System.out.println("Exception: "+e);
					System.out.println("line: "+line);
					throw e;
				}
				
				if (ret.containsKey(jour)) {
					Dh cumul = ret.get(jour);
					cumul.hosp += valeurs[0];
					cumul.rea  += valeurs[1];
					cumul.rad  += valeurs[2];
					cumul.dc   += valeurs[3];
				} else ret.put(jour, new Dh("", "", jour, valeurs[0], valeurs[1], valeurs[2], valeurs[3]));
				
			}
			count++;
		}
		myDataset.setData(ret);
		
		datasets.add(myDataset);
		datasets.get(parentDatasetName).addChildDataset(myDataset);

		return ret;
	}
*/	
	/**
	 * Calcule le deltas d'un jour à l'autre
	 * @param from
	 * @return
	 * @throws Exception
	 */
/*
	public static TreeMap<LocalDate, Dh> getDeltas(TreeMap<LocalDate, Dh> from) throws Exception {
		TreeMap<LocalDate, Dh> ret = new TreeMap<>();
		Dh prev = null;
		
		for (Map.Entry<LocalDate, Dh> entry : from.entrySet()) {
			if (prev != null) {
				ret.put(entry.getKey(), new Dh("", "", null, 
					entry.getValue().hosp - prev.hosp,
					entry.getValue().rea  - prev.rea,
					entry.getValue().rad  - prev.rad,
					entry.getValue().dc   - prev.dc
				));
			}
			prev = entry.getValue();
		}
		return ret;
	}
*/	
	/**
	 * Calcule la moyenne sur une semaine
	 * @param from
	 * @return
	 * @throws Exception
	 */
/*
	public static TreeMap<LocalDate, Dh> calculMoyenne(TreeMap<LocalDate, Dh> from) throws Exception {
		TreeMap<LocalDate, Dh> ret = new TreeMap<>();
		LocalDate startDate = from.firstKey();
		long cumulhosp = 0;
		for (LocalDate jour : from.keySet()) {
			
		}
		
		return ret;
	}
*/	
	public static TreeMap<LocalDate, Dh> getNouveauxByDate(ServletContext context) throws Exception {
		Logger logger = Logger.getLogger("getNouveauxByDate");
	
		String myDatasetName = "dh-nouveaux";
		String parentDatasetName = "donnees-hospitalieres-nouveaux-covid19";
	
		Datasets datasets = (Datasets) context.getAttribute("datasets");
		
		if (datasets.exists(myDatasetName)) {
			logger.info("Return cache: "+myDatasetName);
			return (TreeMap<LocalDate, Dh>) datasets.get(myDatasetName).getData();
		}
		
		TreeMap<LocalDate, Dh> ret = new TreeMap<>();
		
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
			if (count > 0) {
	
				LocalDate jour = LocalDate.parse(normalizeDate(unquote(splits[1])));
				
				long[] valeurs = new long[4];
				for (int i=0; i<4; i++) {
					valeurs[i] = Long.parseLong(unquote(splits[i+2]));
				}
				
				if (ret.containsKey(jour)) {
					Dh cumul = ret.get(jour);
					cumul.hosp += valeurs[0];
					cumul.rea  += valeurs[1];
					cumul.dc   += valeurs[2];
					cumul.rad  += valeurs[3];
				} else {
					logger.debug("jour: "+jour);
					ret.put(jour, new Dh("", "", null, valeurs[0], valeurs[1], valeurs[2], valeurs[3]));
				}
			}
			count++;
		}
		
		myDataset.setData(ret);
		
		datasets.add(myDataset);
		datasets.get(parentDatasetName).addChildDataset(myDataset);
	
		return ret;
	}
	
	public static TreeMap<String, Dh> getCumulClasseAges(ServletContext context, LocalDate jourSelection) throws Exception {
		Logger logger = Logger.getLogger("getCumulClasseAges");
		logger.info("jourSelection: "+jourSelection);
		
		String parentDatasetName = "donnees-hospitalieres-classe-age-covid19";
		String myDatasetName = "dh-classes_ages_jour."+jourSelection;
	
		Datasets datasets = (Datasets) context.getAttribute("datasets");
		
		if (datasets.exists(myDatasetName)) {
			logger.info("Return cache: "+myDatasetName);
			return (TreeMap<String, Dh>) datasets.get(myDatasetName).getData();
		}
		
		TreeMap<String, Dh> ret = new TreeMap<>();
		
		if (!datasets.exists(parentDatasetName)) {
			return ret;
		}

		Dataset myDataset = new CommonDataset(myDatasetName);
		String[] lines = (String[]) datasets.get(parentDatasetName).getData();
		
		logger.info("Calculate new : "+myDatasetName);

		// Attention : certains jours l'ordre de colonnes change ou
		// des colonnes sont insérées.
		// Il faut donc se basser sur les entêtes de colonnes
		int colHosp = 3;
		int colRea  = 4;
		int colRad  = 5;
		int colDc   = 6;

		long count = 0;
		String sep = "";
		for (String line : lines) {
			line = line.trim();
			if (count == 0) {
				sep = getSeparator(line);
				String[] splits = line.split(sep);
				for (int i=0; i<splits.length; i++) {
					if (unquote(splits[i]).equals("hosp")) colHosp = i;
					if (unquote(splits[i]).equals("rea"))  colRea = i;
					if (unquote(splits[i]).equals("rad"))  colRad = i;
					if (unquote(splits[i]).equals("dc"))   colDc = i;
				}
			}
			
			String[] splits = line.split(sep);
			if (count > 0) {
				LocalDate jour = LocalDate.parse(normalizeDate(unquote(splits[2])));
				String classeAge = unquote(splits[1]);
				
				logger.debug("jour: "+jour);
				if (jour.equals(jourSelection) && !classeAge.equals("0")) {
					
					Dh cumul = ret.get(classeAge);
					if (cumul == null) {
						cumul = new Dh();
						ret.put(classeAge, cumul);
					}
					cumul.hosp += Long.parseLong(unquote(splits[colHosp]));
					cumul.rea  += Long.parseLong(unquote(splits[colRea]));
					cumul.rad  += Long.parseLong(unquote(splits[colRad]));
					cumul.dc   += Long.parseLong(unquote(splits[colDc]));
				}
			}
			count++;
		}
		
		myDataset.setData(ret);
		
		datasets.add(myDataset);
		datasets.get(parentDatasetName).addChildDataset(myDataset);

		return ret;
	}

	public static TreeMap<LocalDate, Dh[]> getCumulParDatesEtClasseAges(javax.servlet.ServletContext context) throws Exception {
		Logger logger = Logger.getLogger("getCumulParDatesEtClasseAges");
		
		String parentDatasetName = "donnees-hospitalieres-classe-age-covid19";
		String myDatasetName = "dh-jout_et_classe_age_dernier_jour";
	
		Datasets datasets = (Datasets) context.getAttribute("datasets");
		
		if (datasets.exists(myDatasetName)) {
			logger.info("Return cache: "+myDatasetName);
			return (TreeMap<LocalDate, Dh[]>) datasets.get(myDatasetName).getData();
		}
		
		TreeMap<LocalDate, Dh[]> ret = new TreeMap<>();
		
		if (!datasets.exists(parentDatasetName)) {
			return ret;
		}

		Dataset myDataset = new CommonDataset(myDatasetName);
		String[] lines = (String[]) datasets.get(parentDatasetName).getData();
		
		logger.info("Calculate new : "+myDatasetName);

		int[] trans = new int[91];
		trans[0] = 0; trans[9] = 1; trans[19] = 2; trans[29] = 3; trans[39] = 4; trans[49] = 5; trans[59] = 6; trans[69] = 7; trans[79] = 8; trans[89] = 9; trans[90] = 10;
	
		int colHosp = 3;
		int colRea  = 4;
		int colRad  = 5;
		int colDc   = 6;
		
		long count = 0;
		String sep = "";
		for (String line : lines) {
			line = line.trim();
			if (count == 0) {
				sep = getSeparator(line);
				String[] splits = line.split(sep);
				for (int i=0; i<splits.length; i++) {
					if (unquote(splits[i]).equals("hosp")) colHosp = i;
					if (unquote(splits[i]).equals("rea"))  colRea = i;
					if (unquote(splits[i]).equals("rad"))  colRad = i;
					if (unquote(splits[i]).equals("dc"))   colDc = i;
				}
			}
			
			String[] splits = line.split(sep);
			if (count > 0) {
				LocalDate jour = LocalDate.parse(normalizeDate(unquote(splits[2])));
				int classeAge = Integer.parseInt(unquote(splits[1]));
				
				logger.debug("jour: "+jour);
					
				Dh[] cumuls = ret.get(jour);
				if (cumuls == null) {
					cumuls = new Dh[11];
					for (int i=0; i<cumuls.length; i++) cumuls[i] = new Dh();
					ret.put(jour, cumuls);
				}
				
				int k = trans[classeAge];
				cumuls[k].hosp += Long.parseLong(unquote(splits[colHosp]));
				cumuls[k].rea  += Long.parseLong(unquote(splits[colRea]));
				cumuls[k].rad  += Long.parseLong(unquote(splits[colRad]));
				cumuls[k].dc   += Long.parseLong(unquote(splits[colDc]));
			}
			count++;
		}
		
		myDataset.setData(ret);
		
		datasets.add(myDataset);
		datasets.get(parentDatasetName).addChildDataset(myDataset);

		return ret;
	}
	
}