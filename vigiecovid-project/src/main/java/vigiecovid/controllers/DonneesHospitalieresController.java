package vigiecovid.controllers;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import chamette.datascience.Calculs;
import vigiecovid.domain.dh.Dh;
import vigiecovid.domain.dh.DhClAge;
import vigiecovid.domain.dh.DhClAgeDAO;
import vigiecovid.domain.dh.DhDAO;
import vigiecovid.domain.dh.DhTools;
import vigiecovid.domain.testvir.TestVir;
import vigiecovid.domain.testvir.TestVirDAO;
import vigiecovid.domain.testvir.TestVirTools;

@Controller
public class DonneesHospitalieresController {
	
	private static final Logger LOGGER = Logger.getLogger(DonneesHospitalieresController.class);

	@Autowired
	private DhDAO dhDAO;

	@Autowired
	private DhClAgeDAO dhClAgeDAO;

	@Autowired
	private TestVirDAO testVirDAO;

	@GetMapping("/dh-dc")
    public ModelAndView dc() throws Exception {
		ModelAndView modelAndView = new ModelAndView("dh-dc");

		SortedMap<LocalDate, Dh> dhs = dhDAO.getDhByDay();
		
		LocalDate lastDayOfData = dhs.lastKey();
		LocalDate dateMin = dhs.firstKey().minusDays(1);
		LocalDate dateMax = lastDayOfData.plusDays(1);
	
		SortedMap<LocalDate, Dh> deltas = dhDAO.getDeltasDhByDay();
			
		SortedMap<LocalDate, Dh> avgDeltas = DhTools.avgOverAWeek(deltas);

		SortedMap<String, DhClAge> cumulClasseAges = dhClAgeDAO.getCumulClasseAges(lastDayOfData);
	
		SortedMap<LocalDate, Double> proj = DhTools.calculPolynomialProjection(deltas, "dc");
		
		//---- Alimentation du modèle
	
		modelAndView.addObject("lastDayOfData", lastDayOfData);
		modelAndView.addObject("totalDc", dhs.get(lastDayOfData).getDc());
	
		modelAndView.addObject("dateMin", dateMin);
		modelAndView.addObject("dateMax", dateMax);
	
		modelAndView.addObject("dateMinProj", proj.firstKey());
		modelAndView.addObject("dateMaxProj", proj.lastKey());
	
		modelAndView.addObject("dhs", dhs);
		modelAndView.addObject("deltas", deltas);
		modelAndView.addObject("avgDeltas", avgDeltas);
		modelAndView.addObject("cumulClasseAges", cumulClasseAges);
		modelAndView.addObject("proj", proj);

		return modelAndView;
    }

	@GetMapping("/dh-hosp")
    public ModelAndView hosp() throws Exception {
		ModelAndView modelAndView = new ModelAndView("dh-hosp");

		TreeMap<LocalDate, Dh> dhs = dhDAO.getDhByDay();
		
		LocalDate lastDayOfData = dhs.lastKey();
		LocalDate dateMin = dhs.firstKey().minusDays(1);
		LocalDate dateMax = lastDayOfData.plusDays(1);
	
		TreeMap<LocalDate, Dh> deltas = dhDAO.getDeltasDhByDay();
			
		TreeMap<LocalDate, Dh> avgDeltas = DhTools.avgOverAWeek(deltas);

		TreeMap<String, DhClAge> cumulClasseAges = dhClAgeDAO.getCumulClasseAges(lastDayOfData);
	
		TreeMap<LocalDate, Double> proj = DhTools.calculPolynomialProjection(dhs, "hosp");
		
		TreeMap<LocalDate, Dh> nouveaux = dhDAO.getNouveauxByDate();
		
		TreeMap<LocalDate, Dh> avgNouveaux = DhTools.avgOverAWeek(nouveaux);

		//---- Alimentation du modèle

		modelAndView.addObject("lastDayOfData", lastDayOfData);
		modelAndView.addObject("dernierHosp", dhs.lastEntry().getValue().getHosp());
		modelAndView.addObject("dc", deltas.lastEntry().getValue().getDc());

		modelAndView.addObject("dateMin", dateMin);
		modelAndView.addObject("dateMax", dateMax);

		modelAndView.addObject("dateMinProj", proj.firstKey());
		modelAndView.addObject("dateMaxProj", proj.lastKey());

		modelAndView.addObject("dhs", dhs);
		modelAndView.addObject("deltas", deltas);
		modelAndView.addObject("avgDeltas", avgDeltas);
		modelAndView.addObject("cumulClasseAges", cumulClasseAges);
		modelAndView.addObject("proj", proj);
		modelAndView.addObject("nouveaux", nouveaux);
		modelAndView.addObject("avgNouveaux", avgNouveaux);
		
        return modelAndView;
	}

	@GetMapping("/dh-rea")
    public ModelAndView rea() throws Exception {
		ModelAndView modelAndView = new ModelAndView("dh-rea");
		Map<String, Object> model = new HashMap<>();

		TreeMap<LocalDate, Dh> dhs = dhDAO.getDhByDay();
		
		LocalDate lastDayOfData = dhs.lastKey();
		LocalDate dateMin = dhs.firstKey().minusDays(1);
		LocalDate dateMax = lastDayOfData.plusDays(1);
	
		TreeMap<LocalDate, Dh> deltas = dhDAO.getDeltasDhByDay();
			
		TreeMap<LocalDate, Dh> avgDeltas = DhTools.avgOverAWeek(deltas);

		TreeMap<String, DhClAge> cumulClasseAges = dhClAgeDAO.getCumulClasseAges(lastDayOfData);
	
		TreeMap<LocalDate, Double> proj = DhTools.calculPolynomialProjection(dhs, "rea");
		
		TreeMap<LocalDate, Dh> nouveaux = dhDAO.getNouveauxByDate();
		
		TreeMap<LocalDate, Dh> avgNouveaux = DhTools.avgOverAWeek(nouveaux);

		//---- Alimentation du modèle

		modelAndView.addObject("lastDayOfData", lastDayOfData);
		modelAndView.addObject("dernierRea", dhs.lastEntry().getValue().getRea());

		modelAndView.addObject("dateMin", dateMin);
		modelAndView.addObject("dateMax", dateMax);

		modelAndView.addObject("dateMinProj", proj.firstKey());
		modelAndView.addObject("dateMaxProj", proj.lastKey());

		modelAndView.addObject("dhs", dhs);
		modelAndView.addObject("deltas", deltas);
		modelAndView.addObject("avgDeltas", avgDeltas);
		modelAndView.addObject("cumulClasseAges", cumulClasseAges);
		modelAndView.addObject("proj", proj);
		modelAndView.addObject("nouveaux", nouveaux);
		modelAndView.addObject("avgNouveaux", avgNouveaux);
        return modelAndView;
	}

	@GetMapping("/dh-ages")
    public ModelAndView ages() throws Exception {
		ModelAndView modelAndView = new ModelAndView("dh-ages");
		Map<String, Object> model = new HashMap<>();

		TreeMap<LocalDate, Dh> dh = dhDAO.getDhByDay();
		TreeMap<LocalDate, Dh[]> cumulParDatesEtClasseAges
		        = dhDAO.getCumulParDatesEtClasseAges();
		
		//---- Alimentation du modèle
		
		model.put("dh", dh);
		model.put("cumulParDatesEtClasseAges", cumulParDatesEtClasseAges);
		
		model.put("dataDateMin", dh.firstKey().toString());
		model.put("dataDateMax", dh.lastKey().toString());

		modelAndView.addObject("model", model);
        return modelAndView;
	}

	@GetMapping("/dh-correl")
    public ModelAndView correl() throws Exception {
		
		ModelAndView modelAndView = new ModelAndView("dh-correl");

		TreeMap<LocalDate, Dh> dhs = dhDAO.getDhByDay();
		
		LocalDate dateMin = LocalDate.of(2020, 5, 31);
		LocalDate dateMax = dhs.lastKey().plusDays(1);
		
		/*
		 * Ce code teste l'idée de générer directement du code js par le code java avec l'API JSON.
		 */
		JsonBuilderFactory factory = Json.createBuilderFactory(new HashMap<String, Object>());
		JsonArrayBuilder line1Json = factory.createArrayBuilder();
		JsonArrayBuilder line2Json = factory.createArrayBuilder();
		
		for (LocalDate jour : dhs.keySet()) {
			line1Json.add(factory.createArrayBuilder().add(jour.toString()).add(dhs.get(jour).getHosp()));
			line2Json.add(factory.createArrayBuilder().add(jour.toString()).add(dhs.get(jour).getRea()));
		}

		//---- Essais de plusieurs correlations avec un decallage de jours
		
		Map<Integer, Double> scores = new TreeMap<>();
		
		for (int decall = -7 ; decall < 7; decall++) {
			Integer decallJour = decall;
				
			List<Double> testListValues1 = new ArrayList<>();
			List<Double> testListValues2 = new ArrayList<>();
			
			dhs.tailMap(dateMin).keySet().stream()
				.filter(jour -> dhs.containsKey(jour.plusDays(decallJour)))
				.forEach(jour -> {
					testListValues1.add((double) dhs.get(jour).getHosp());
					testListValues2.add((double) dhs.get(jour.plusDays(decallJour)).getRea());
				});
			
			double[] values1 = new double[testListValues1.size()];
			double[] values2 = new double[testListValues1.size()];
			
			for (int i = 0; i < values1.length; i++) {
				values1[i] = testListValues1.get(i);
				values2[i] = testListValues2.get(i);
			}
			
			scores.put(decallJour, new PearsonsCorrelation().correlation(values1, values2));
		}
		
		//---- Chose the best correlation
		
		double bestCorrel = -99999D;
		int bestDecall = 0;
		
		for (Map.Entry<Integer, Double> e : scores.entrySet()) {
			if (e.getValue() > bestCorrel) {
				bestCorrel = e.getValue();
				bestDecall = e.getKey();
			}
		}
		
		//---- Information about the best correlation
		
		String decallMsg = "?";

		if (bestDecall == 0) {
			decallMsg = "le même jour entre les réanimations et les hospitalisations";
		} else if (bestDecall < 0) {
			decallMsg = "les réanimations en avance de " + -bestDecall + " jour(s) "
					+ "sur les hospitalisations";
		} else if (bestDecall > 0) {
			decallMsg = "les réanimations en retard de " + bestDecall + " jour(s) "
					+ "sur les hospitalisations";
		}
		
		// ---- Calcul de l'évolution de l'incidence
		
		// Population servant au calcul de l'incidence.
		// C'est la population Française par défaut.
		long population = 67_000_000l;
		
		TreeMap<LocalDate, TestVir> byWeeks = testVirDAO.cumulTestVirByWeeks(null, true);
		TreeMap<LocalDate, Integer> incidences
				= TestVirTools.calculEvolIncidence(byWeeks, population);
		
		JsonArrayBuilder incidencesJson = factory.createArrayBuilder();
		incidences.tailMap(dateMin).forEach((jour, incidence) -> 
			incidencesJson.add(factory.createArrayBuilder().add(jour.toString()).add(incidence))
		);
		
		//---- Generate the model
	
		JsonObjectBuilder root = factory.createObjectBuilder();
		
		root.add("line1", line1Json);
		root.add("line2", line2Json);
		root.add("incidences", incidencesJson);
		root.add("dateMin", dateMin.toString());
		root.add("dateMax", dateMax.toString());
	
		root.add("correl", bestCorrel);
		root.add("decall", bestDecall);
		root.add("decallMsg", decallMsg);
		
		JsonObject jsonModel = root.build();

		StringWriter out = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(out)) {
			jsonWriter.writeObject(jsonModel);
		}
		modelAndView.addObject("model", out.toString());
		modelAndView.addObject("scores", scores);
		modelAndView.addObject("decallMsg", decallMsg);
		modelAndView.addObject("dateMin", dateMin);
		return modelAndView;
	}

	@GetMapping("/dh-repro")
    public ModelAndView repro() throws Exception {
		ModelAndView modelAndView = new ModelAndView("dh-repro");
		
		TreeMap<LocalDate, Double> reproductionTestVirByWeeks = 
				testVirDAO.reproductionTestVirByWeeks(null, true);
		modelAndView.addObject("reproductionTestVirByWeeks", reproductionTestVirByWeeks);
		
		modelAndView.addObject("dateMin", reproductionTestVirByWeeks.firstKey());
		modelAndView.addObject("dateMax", reproductionTestVirByWeeks.lastKey());
		
		double tvDernierRatio = reproductionTestVirByWeeks.lastEntry().getValue();
		double txParJour = Calculs.tauxParPeriodes(tvDernierRatio - 1, 7);
		double tvDoubleDesCas = Calculs.nbDePeriodes(txParJour, 1);
		
		modelAndView.addObject("tvDernierRatio", tvDernierRatio);
		modelAndView.addObject("tvDoubleDesCas", tvDoubleDesCas);
		
		LOGGER.debug("variationTestVirByWeeks.size(): " + reproductionTestVirByWeeks.size());

		return modelAndView;
	}
}
