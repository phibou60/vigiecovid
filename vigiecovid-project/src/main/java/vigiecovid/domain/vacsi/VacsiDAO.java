package vigiecovid.domain.vacsi;

import java.time.LocalDate;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chamette.datasets.CommonDataset;
import chamette.datasets.Dataset;
import chamette.datasets.Datasets;
import chamette.datasets.EmptyLineException;
import chamette.datasets.ParseException;

@Component
public class VacsiDAO {

	private final Logger LOGGER = Logger.getLogger(VacsiDAO.class);

	private ServletContext context;
	
	public VacsiDAO(@Autowired ServletContext context) {
		super();
		LOGGER.info("Instanciate with context: "+context);
		this.context = context;
	}
	
	/**
	 * Retourne les cumuls de vaccination par jour ai niveau france.
	 * @param context Obligatoire
	 * @return
	 * @throws Exception
	 */
	public TreeMap<LocalDate, Vacsi> getVacsiFranceByDay() throws Exception {
		Datasets datasets = (Datasets) context.getAttribute("datasets");

		String myDatasetName = "getVacsiFranceByDay";
		String parentDatasetName = "vacsi-a-fra";
		
		if (datasets.exists(myDatasetName)) {
			LOGGER.info("Return cache: "+myDatasetName);
			return (TreeMap<LocalDate, Vacsi>) datasets.get(myDatasetName).getData();
		}
		
		TreeMap<LocalDate, Vacsi> ret = new TreeMap<>();
		
		if (!datasets.exists(parentDatasetName)) {
			return ret;
		}
		
		LOGGER.info("Calculate new : "+myDatasetName);

		Dataset myDataset = new CommonDataset(myDatasetName);
		String[] lines = (String[]) datasets.get(parentDatasetName).getData();
		
		VacsiaParser parser = new VacsiaParser();
		long parseExceptionCount = 0;
		
		boolean firstLine = true;
		for (String line : lines) {
			try {
				if (!firstLine) {
					Vacsi vacsi = parser.parse(line);
				
					if (!vacsi.getClage().equals("0")) {
						continue;
					}
		
					ret.put(vacsi.getJour(), vacsi);
				}
				firstLine = false;
				
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
