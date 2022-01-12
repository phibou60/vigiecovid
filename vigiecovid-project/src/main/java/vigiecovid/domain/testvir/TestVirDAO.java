package vigiecovid.domain.testvir;

import static chamette.tools.CsvTools.getSeparator;
import static chamette.tools.CsvTools.normalizeDate;
import static chamette.tools.CsvTools.unquote;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chamette.datascience.Calculs;
import chamette.datasets.CommonDataset;
import chamette.datasets.Dataset;
import chamette.datasets.DatasetHelper;
import chamette.datasets.Datasets;

import vigiecovid.domain.vacsi.VacsiDAO;

@Component
public class TestVirDAO {

	private static final Logger LOGGER = Logger.getLogger(VacsiDAO.class);

	private ServletContext context;
	private Datasets datasets;

	public TestVirDAO(@Autowired ServletContext context) {
		super();
		LOGGER.info("Instanciate with context: "+context);
		this.context = context;
	}
	
	/**
	 * Récupération du dernier jour contenu dans les datas.
	 * 
	 * @param context Permet d'accéder aux Datasets
	 */
	public LocalDate getLastDay() throws Exception {
		TreeMap<LocalDate, TestVir> cumulByDay = cumulTestVirByDay(null, false);
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
	public TreeMap<String, TestVir> cumulTestVirByDepLastWeek(LocalDate lastDay)
			throws Exception {
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
			if (count > 0 && splits.length > 4 && splits[4].equals("0")) {
				
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
	public TreeMap<LocalDate, TestVir> cumulTestVirByDay(String dep, boolean metropoleSeule)
			throws Exception {
		
		String myDatasetName = "cumulTestVirByDay."+dep+"."+metropoleSeule;
		String parentDatasetName = "sp-pos-quot-dep";
	
		Datasets datasets = (Datasets) context.getAttribute("datasets");
		
		if (datasets.exists(myDatasetName)) {
			LOGGER.info("Return cache: "+myDatasetName);
			return (TreeMap<LocalDate, TestVir>) datasets.get(myDatasetName).getData();
		}
		
		TreeMap<LocalDate, TestVir> ret = new TreeMap<>();
		
		if (!datasets.exists(parentDatasetName)) {
			return ret;
		}
		
		LOGGER.info("Calculate new : "+myDatasetName);

		Dataset myDataset = new CommonDataset(myDatasetName);
		String[] lines = (String[]) datasets.get(parentDatasetName).getData();

		long count = 0;
		String sep = "";
		for (String line : lines) {
			line = line.trim();
			if (count == 0) sep = getSeparator(line);
			
			String[] splits = line.split(sep);

			if (count > 0 && splits.length > 4 && splits[4].equals("0") 
					&& ((dep == null && (!metropoleSeule || (metropoleSeule && splits[0].length() == 2))) || splits[0].equals(dep))) {
				
				LocalDate jour = LocalDate.parse(normalizeDate(unquote(splits[1])));
				
				long[] valeurs = new long[2];
				for (int i=0; i<valeurs.length; i++) {
					try {
						valeurs[i] = Long.parseLong(splits[i+2]);
					} catch (Exception e) {
						LOGGER.error("Exception: "+e+", line: "+line);
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
	 * @param byDays Tests virologiques par jour
	 */
	public TreeMap<LocalDate, TestVir> cumulTestVirByWeeks(TreeMap<LocalDate, TestVir> byDays)
			throws Exception {
		
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
	 * Retourne les tests virologiques par semaine (avec possibilité de filtrer sur le département).
	 * 
	 * @param context Permet d'accéder aux Datasets
	 * @param dep Département (facultatif. Peut être null)
	 * @param metropoleSeule true indique que l'on veut uniquement les données de la métropole
	 * @return Un tableau des tests virologiques par semaine (la journée indique le dernier jour)
	 * @throws Exception
	 */
	public TreeMap<LocalDate, TestVir> cumulTestVirByWeeks(String dep,
			boolean metropoleSeule) throws Exception {
		
		TreeMap<LocalDate, TestVir> byDays = cumulTestVirByDay(dep, metropoleSeule);
				
		return cumulTestVirByWeeks(byDays);
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
	public TreeMap<LocalDate, Double> reproductionTestVirByWeeks(
			String dep, boolean metropoleSeule) throws Exception {

		TreeMap<LocalDate, TestVir> byWeeks = cumulTestVirByWeeks(dep, metropoleSeule);
		TreeMap<LocalDate, Double> ret = new TreeMap<>();

		for (Map.Entry<LocalDate, TestVir> entry : byWeeks.entrySet()) {
			LocalDate keyPrevWeek = entry.getKey().minusDays(7);
			TestVir testVirPrevWeek = byWeeks.get(keyPrevWeek);
			if (testVirPrevWeek != null) {
				double ratio = Calculs.ratio(testVirPrevWeek.getPositifs(), entry.getValue().getPositifs());
				ret.put(entry.getKey(), ratio);
				LOGGER.debug(keyPrevWeek + ": " + testVirPrevWeek.getPositifs() + " / " + entry.getKey() + ": "
						+ entry.getValue().getPositifs() + " = " + ratio);
			}
		}

		LOGGER.debug("ret.size(): " + ret.size());
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
	public TreeMap<String, Double> getVariations(LocalDate lastDay) throws Exception {

		TreeMap<String, TestVir> lastWeek = cumulTestVirByDepLastWeek(lastDay);
		TreeMap<String, TestVir> previousWeek = cumulTestVirByDepLastWeek(lastDay.minusDays(7));
		
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
	
	/**
	 * Liste des tests virologiques par jour.
	 * @throws Exception 
	 */

	public TreeMap<LocalDate, TestVir> cumulTestVirByDay() throws Exception {
		
		DatasetHelper helper = new DatasetHelper(getDatasets(), "cumulTestVirByDay",
				"sp-pos-quot-fra") {
			
			@Override
			public Object calculateData(Object parentData) throws Exception {
				String[] lines = (String[]) parentData;
				
				TestVirQuotFraParser parser = new TestVirQuotFraParser(lines[0]);
				
				Map<LocalDate, TestVir> map = Stream.of(lines)
					.skip(1)
					.filter(l -> l.indexOf(";0;") > 20)
					.flatMap(l -> parser.parseToStream(l))
					.collect(Collectors.toMap(t -> t.getJour(), t -> t));
				
				return new TreeMap<LocalDate, TestVir>(map);
			}
		};
		return (TreeMap<LocalDate, TestVir>) helper.getData();
	}
	
	public void setDatasets(Datasets datasets) {
		this.datasets = datasets;
	}

	private Datasets getDatasets() {
		if (datasets == null && context != null) { 
			datasets = (Datasets) context.getAttribute("datasets");
		}
		return datasets;
	}
	
}