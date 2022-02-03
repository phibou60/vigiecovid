package vigiecovid.domain.dh;

import static chamette.tools.CsvTools.getSeparator;
import static chamette.tools.CsvTools.normalizeDate;
import static chamette.tools.CsvTools.unquote;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chamette.datasets.CommonDataset;
import chamette.datasets.Dataset;
import chamette.datasets.DatasetHelper;
import chamette.datasets.Datasets;

@Component
public class DhDAO {

	private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getFormatterLogger(DhDAO.class);

	private Datasets datasets;
	
	public DhDAO(@Autowired Datasets datasets) {
		super();
		LOGGER.info("Instanciate with context: "+datasets);
		this.datasets = datasets;
	}
	
	/**
	 * Retourne les cumuls par jour au niveau france.
	 */

	public TreeMap<LocalDate, Dh> getDhByDay() throws Exception {
		
		DatasetHelper helper
				= new DatasetHelper(datasets, "getDhByDay", "donnees-hospitalieres-covid19") {
			
			@Override
			public Object calculateData(Object parentData) throws Exception {
				String[] lines = (String[]) parentData;
				
				DhParser parser = new DhParser(lines[0]);
				
				Map<LocalDate, Dh> map = Stream.of(lines)
					.skip(1)
					.flatMap(parser::parseToStream)
					.filter(d -> d.getSexe().equals("0"))
					.collect(Collectors.toMap(
							Dh::getJour,
							dh -> dh,
							(d1, d2) -> d1.plus(d2)));
				
				return new TreeMap<LocalDate, Dh>(map);
			}
		};
		return (TreeMap<LocalDate, Dh>) helper.getData();
		
	}
	
	/**
	 * Calcule le deltas d'un jour à l'autre
	 */

	public TreeMap<LocalDate, Dh> getDeltasDhByDay() throws Exception {
		
		DatasetHelper helper
				= new DatasetHelper(datasets, "getDeltasDhByDay", "getDhByDay") {
			
			@Override
			public Object calculateData(Object parentData) throws Exception {
				TreeMap<LocalDate, Dh> from = (TreeMap<LocalDate, Dh>) parentData;
				
				TreeMap<LocalDate, Dh> ret = new TreeMap<>();
				Dh prev = null;
				
				for (Map.Entry<LocalDate, Dh> entry : from.entrySet()) {
					if (prev != null) {
						ret.put(entry.getKey(), new Dh("", "", entry.getKey(), 
							entry.getValue().getHosp() - prev.getHosp(),
							entry.getValue().getRea()  - prev.getRea(),
							entry.getValue().getRad()  - prev.getRad(),
							entry.getValue().getDc()   - prev.getDc()
						));
					}
					prev = entry.getValue();
				}
				return ret;
			}
		};
		return (TreeMap<LocalDate, Dh>) helper.getData();
		
	}
	
	// TODO : à réécrire
	public TreeMap<LocalDate, Dh> getNouveauxByDate() throws Exception {
		Logger logger = org.apache.logging.log4j.LogManager.getFormatterLogger("getNouveauxByDate");
	
		String myDatasetName = "dh-nouveaux";
		String parentDatasetName = "donnees-hospitalieres-nouveaux-covid19";
		
		if (datasets.containsKey(myDatasetName)) {
			logger.info("Return cache: "+myDatasetName);
			return (TreeMap<LocalDate, Dh>) datasets.get(myDatasetName).getData();
		}
		
		TreeMap<LocalDate, Dh> ret = new TreeMap<>();
		
		if (!datasets.containsKey(parentDatasetName)) {
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
					cumul.plus(new Dh("", "", null, valeurs[0], valeurs[1], valeurs[2], valeurs[3])); 
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

	// TODO : à réécrire
	public TreeMap<LocalDate, Dh[]> getCumulParDatesEtClasseAges() throws Exception {
		Logger logger = org.apache.logging.log4j.LogManager.getFormatterLogger("getCumulParDatesEtClasseAges");
		
		String parentDatasetName = "donnees-hospitalieres-classe-age-covid19";
		String myDatasetName = "dh-jout_et_classe_age_dernier_jour";
		
		if (datasets.containsKey(myDatasetName)) {
			logger.info("Return cache: "+myDatasetName);
			return (TreeMap<LocalDate, Dh[]>) datasets.get(myDatasetName).getData();
		}
		
		TreeMap<LocalDate, Dh[]> ret = new TreeMap<>();
		
		if (!datasets.containsKey(parentDatasetName)) {
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
					for (int i=0; i<cumuls.length; i++) cumuls[i] = new Dh("", "", null, 0, 0, 0, 0);
					ret.put(jour, cumuls);
				}
				
				int k = trans[classeAge];
				cumuls[k].plus(new Dh("", "", null,
						Long.parseLong(unquote(splits[colHosp])),
						Long.parseLong(unquote(splits[colRea])),
						Long.parseLong(unquote(splits[colRad])),
						Long.parseLong(unquote(splits[colDc]))));
			}
			count++;
		}
		
		myDataset.setData(ret);
		
		datasets.add(myDataset);
		datasets.get(parentDatasetName).addChildDataset(myDataset);

		return ret;
	}
	
}
