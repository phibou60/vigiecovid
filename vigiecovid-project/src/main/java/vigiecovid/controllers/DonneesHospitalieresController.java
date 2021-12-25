package vigiecovid.controllers;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import chamette.datascience.Calculs;
import vigiecovid.domain.DonneesHospitalieres;
import vigiecovid.domain.ServletContextWrapper;
import vigiecovid.domain.dh.Dh;
import vigiecovid.domain.dh.DhClAge;
import vigiecovid.domain.dh.DhClAgeDAO;
import vigiecovid.domain.dh.DhDAO;
import vigiecovid.domain.dh.DhTools;
import vigiecovid.domain.testvir.TestVirDAO;

@Controller
public class DonneesHospitalieresController {

	@Autowired
	private ServletContextWrapper servletContextWrapper;	

	@Autowired
	private DhDAO dhDAO;

	@Autowired
	private DhClAgeDAO dhClAgeDAO;

	@Autowired
	private TestVirDAO testVirDAO;
	
	private static final Logger LOGGER = Logger.getLogger(DonneesHospitalieresController.class);

	@GetMapping("/dh-dc")
    public ModelAndView dc() throws Exception {
		ModelAndView modelAndView = new ModelAndView("dh-dc");

		TreeMap<LocalDate, Dh> dhs = dhDAO.getDhByDay();
		
		LocalDate lastDayOfData = dhs.lastKey();
		LocalDate dateMin = dhs.firstKey().minusDays(1);
		LocalDate dateMax = lastDayOfData.plusDays(1);
	
		TreeMap<LocalDate, Dh> deltas = dhDAO.getDeltasDhByDay();
			
		TreeMap<LocalDate, Dh> avgDeltas = DhTools.avgOverAWeek(deltas);

		TreeMap<String, DhClAge> cumulClasseAges = dhClAgeDAO.getCumulClasseAges(lastDayOfData);
	
		TreeMap<LocalDate, Double> proj = DhTools.calculPolynomialProjection(deltas, "dc");
		
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
		
		TreeMap<LocalDate, Dh> nouveaux = DonneesHospitalieres.getNouveauxByDate(servletContextWrapper.getServletContext());
		
		TreeMap<LocalDate, Dh> avgNouveaux = DhTools.avgOverAWeek(nouveaux);

		//---- Alimentation du modèle

		modelAndView.addObject("lastDayOfData", lastDayOfData);
		modelAndView.addObject("dernierHosp", dhs.lastEntry().getValue().getHosp());

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
		
		TreeMap<LocalDate, Dh> nouveaux = DonneesHospitalieres.getNouveauxByDate(servletContextWrapper.getServletContext());
		
		TreeMap<LocalDate, Dh> avgNouveaux = DhTools.avgOverAWeek(nouveaux);

		//---- Alimentation du modèle

		modelAndView.addObject("lastDayOfData", lastDayOfData);
		modelAndView.addObject("dernierRea", dhs.lastEntry().getValue().getHosp());

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
		LocalDate lastKey = dh.lastKey();
		TreeMap<LocalDate, Dh[]> cumulParDatesEtClasseAges = DonneesHospitalieres.getCumulParDatesEtClasseAges(servletContextWrapper.getServletContext());
		
		//---- Alimentation du modèle
		
		model.put("dh", dh);
		model.put("cumulParDatesEtClasseAges", cumulParDatesEtClasseAges);
		
		model.put("dataDateMin", dh.firstKey().toString());
		model.put("dataDateMax", dh.lastKey().toString());

		modelAndView.addObject("model", model);
        return modelAndView;
	}

	@GetMapping("/dh-correl")
    public ModelAndView correl(@RequestParam(value="correlId", required=false) String correlId) throws Exception {
		if (correlId == null) {
			correlId = "adm_hosp_rea";
		}
		
		ModelAndView modelAndView = new ModelAndView("dh-correl");
		Map<String, Object> model = new HashMap<>();
		
		List<Long> listValues1 = null;
		List<Long> listValues2 = null;
		
		/*
		 * Ce code teste l'idée de générer directement du code js par le code java comme modèle.
		 */
		JsonBuilderFactory factory = Json.createBuilderFactory(new HashMap<String, Object>());
		JsonArrayBuilder line1Json = factory.createArrayBuilder();
		JsonArrayBuilder line2Json = factory.createArrayBuilder();
		LocalDate dateMin = null;
		LocalDate dateMax = null;
	
		double correl = 0.0d;
		int decall = 0;
		String decallMsg = "?";
		String correlMsg = "?";
		
		if (correlId.equalsIgnoreCase("adm_hosp_rea")) {
			
			correlMsg = "entre les admissions en réanimation et en hospitalisation";
			
			TreeMap<LocalDate, Dh> nouveaux = DonneesHospitalieres.getNouveauxByDate(servletContextWrapper.getServletContext());
	
			dateMin = LocalDate.of(2020, 3, 31);
			dateMax = nouveaux.lastKey().plusDays(1);
			
			for (LocalDate jour : nouveaux.keySet()) {
				line1Json.add(factory.createArrayBuilder().add(jour.toString()).add(nouveaux.get(jour).getHosp()));
				line2Json.add(factory.createArrayBuilder().add(jour.toString()).add(nouveaux.get(jour).getRea()));
			}
			
			List<Long> testListValues1 = new ArrayList<>();
			List<Long> testListValues2 = new ArrayList<>();
			
			for (LocalDate jour : nouveaux.keySet()) {
				if (jour.isAfter(dateMin)) {
					testListValues1.add(nouveaux.get(jour).getHosp());
					testListValues2.add(nouveaux.get(jour).getRea());
				}
			}
	
			double[] values1 = new double[testListValues1.size()];
			double[] values2 = new double[testListValues1.size()];
			
			for (int i=0; i < testListValues1.size(); i++) {
				values1[i] = testListValues1.get(i);
				values2[i] = testListValues2.get(i);
			}
			correl = new PearsonsCorrelation().correlation(values1, values2);
			
			//---- Chosen datas
			
			listValues1 = testListValues1;
			listValues2 = testListValues2;
			if (decall == 0) {
				decallMsg = "le même jour";
			}
			
		} else /* if (correlId.equalsIgnoreCase("total_hosp_rea")) */ {
			
			correlMsg = "entre le nombre de réanimations et d'hospitalisations";
			
			TreeMap<LocalDate, Dh> nouveaux = dhDAO.getDhByDay();
	
			dateMin = LocalDate.of(2020, 3, 31);
			dateMax = nouveaux.lastKey().plusDays(1);
			
			for (LocalDate jour : nouveaux.keySet()) {
				line1Json.add(factory.createArrayBuilder().add(jour.toString()).add(nouveaux.get(jour).getHosp()));
				line2Json.add(factory.createArrayBuilder().add(jour.toString()).add(nouveaux.get(jour).getRea()));
			}
			
			List<Long> testListValues1 = new ArrayList<>();
			List<Long> testListValues2 = new ArrayList<>();
			
			for (LocalDate jour : nouveaux.keySet()) {
				if (jour.isAfter(dateMin)) {
					testListValues1.add(nouveaux.get(jour).getHosp());
					testListValues2.add(nouveaux.get(jour).getRea());
				}
			}
	
			double[] values1 = new double[testListValues1.size()];
			double[] values2 = new double[testListValues1.size()];
			
			for (int i=0; i < testListValues1.size(); i++) {
				values1[i] = testListValues1.get(i);
				values2[i] = testListValues2.get(i);
			}
			correl = new PearsonsCorrelation().correlation(values1, values2);
			
			//---- Chosen datas
			
			listValues1 = testListValues1;
			listValues2 = testListValues2;
			if (decall == 0) {
				decallMsg = "le même jour";
			}
		}
	
		JsonObjectBuilder root = factory.createObjectBuilder();
		
		JsonArrayBuilder nuage = factory.createArrayBuilder();
		for (int i=0; i< listValues1.size(); i++) {
			nuage.add(factory.createArrayBuilder().add(listValues1.get(i)).add(listValues2.get(i)));
		}
		root.add("nuage", nuage);
		
		root.add("line1", line1Json);
		root.add("line2", line2Json);
		root.add("dateMin", dateMin.toString());
		root.add("dateMax", dateMax.toString());
	
		root.add("correl", correl);
		root.add("correlMsg", correlMsg);
		root.add("decall", decall);
		root.add("decallMsg", decallMsg);
		
		JsonObject jsonModel = root.build();

		StringWriter out = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(out);
		jsonWriter.writeObject(jsonModel);
		
		modelAndView.addObject("model", out.toString());
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
