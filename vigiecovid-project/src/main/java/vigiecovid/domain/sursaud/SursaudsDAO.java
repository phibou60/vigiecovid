package vigiecovid.domain.sursaud;

import java.time.LocalDate;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import chamette.datasets.CommonDataset;
import chamette.datasets.Dataset;
import chamette.datasets.Datasets;
import chamette.datasets.EmptyLineException;
import chamette.datasets.ParseException;

/**
 * Classe permettant de faire des cumuls de données d'appel à SOS Médecins et aux passages aux urgences.  
 *
 */
public class SursaudsDAO {

	private final Logger LOGGER = Logger.getLogger(SursaudsDAO.class);

	private Datasets datasets;
	
	public SursaudsDAO(ServletContext context) {
		super();
		this.datasets = (Datasets) context.getAttribute("datasets");
	}
	
	public SursaudsDAO(Datasets datasets) {
		super();
		this.datasets = datasets;
	}
	
	/**
	 * Retourne les cumuls de Sursaud par jour.
	 * @param context Obligatoire
	 * @param dep Facultatifs
	 * @return
	 * @throws Exception
	 */
	public TreeMap<LocalDate, Sursaud> cumulSursaudByDay(String dep) throws Exception {

		String myDatasetName = "cumulSursaudByDay.dep="+dep;
		String parentDatasetName = "sursaud-covid19-quotidien-departement";
		
		if (datasets.exists(myDatasetName)) {
			LOGGER.info("Return cache: "+myDatasetName);
			return (TreeMap<LocalDate, Sursaud>) datasets.get(myDatasetName).getData();
		}
		
		TreeMap<LocalDate, Sursaud> ret = new TreeMap<>();
		
		if (!datasets.exists(parentDatasetName)) {
			return ret;
		}
		
		LOGGER.info("Calculate new : "+myDatasetName);

		Dataset myDataset = new CommonDataset(myDatasetName);
		String[] lines = (String[]) datasets.get(parentDatasetName).getData();
		
		SursaudParser parser = new SursaudParser();
		long parseExceptionCount = 0;
		
		for (String line : lines) {
			try {
				Sursaud sursaud = parser.parse(line);

				if (dep != null && !sursaud.getDep().equals(parser)) {
					continue;
				}
	
				LocalDate jour = sursaud.getJour();
				Sursaud sursaudCumul = ret.get(jour);
				if (sursaudCumul == null) {
					sursaudCumul = new Sursaud(dep, jour, "");
					ret.put(jour, sursaudCumul);
				}
				sursaudCumul.plus(sursaud);
				
			} catch (EmptyLineException e) {
				// Not a problem
				LOGGER.trace("Exception: "+e);			
			} catch (ParseException e) {
				LOGGER.warn("Exception: "+e);
				parseExceptionCount++;
				if (parseExceptionCount > 10) {
					throw e;
				}
			}
		}
		
		myDataset.setData(ret);
		
		datasets.add(myDataset);
		datasets.get(parentDatasetName).addChildDataset(myDataset);

		return ret;
	}
	
	/**
	 * Retourne les cumuls de Sursaud par département.
	 * @param dep Obligatoire
	 * @param fromDate Facultatif
	 * @param toDate Facultatif
	 * @return
	 * @throws Exception
	 */
	public TreeMap<String, Sursaud> cumulSursaudByDep(LocalDate fromDate, LocalDate toDate) throws Exception {

		String myDatasetName = "cumulSursaudByDep.fromDate="+fromDate+",toDate="+toDate;
		String parentDatasetName = "sursaud-covid19-quotidien-departement";
		
		if (datasets.exists(myDatasetName)) {
			LOGGER.info("Return cache: "+myDatasetName);
			return (TreeMap<String, Sursaud>) datasets.get(myDatasetName).getData();
		}
		
		TreeMap<String, Sursaud> ret = new TreeMap<>();
		
		if (!datasets.exists(parentDatasetName)) {
			return ret;
		}
		
		LOGGER.info("Calculate new : "+myDatasetName);

		Dataset myDataset = new CommonDataset(myDatasetName);
		String[] lines = (String[]) datasets.get(parentDatasetName).getData();
		
		SursaudParser parser = new SursaudParser();
		long parseExceptionCount = 0;
		
		for (String line : lines) {
			try {
				Sursaud sursaud = parser.parse(line);

				if (fromDate != null && (sursaud.getJour().isBefore(fromDate))) {
					continue;
				}

				if (toDate != null && (sursaud.getJour().isAfter(toDate))) {
					continue;
				}

				String key = sursaud.getDep();
				Sursaud sursaudCumul = ret.get(key);
				if (sursaudCumul == null) {
					sursaudCumul = new Sursaud(key, toDate, "");
					ret.put(key, sursaudCumul);
				}
				sursaudCumul.plus(sursaud);

			} catch (EmptyLineException e) {
				// Not a problem
				LOGGER.trace("Exception: "+e);			
			} catch (ParseException e) {
				LOGGER.warn("Exception: "+e);
				parseExceptionCount++;
				if (parseExceptionCount > 10) {
					throw e;
				}
			}
		}
		
		myDataset.setData(ret);
		
		datasets.add(myDataset);
		datasets.get(parentDatasetName).addChildDataset(myDataset);

		return ret;
	}

}