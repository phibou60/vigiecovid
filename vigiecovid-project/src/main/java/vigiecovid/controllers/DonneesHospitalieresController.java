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
import vigiecovid.domain.DepartementsDAO;
import vigiecovid.domain.DonneesHospitalieres;
import vigiecovid.domain.ServletContextWrapper;
import vigiecovid.domain.dh.Dh;
import vigiecovid.domain.testvir.TestVirDAO;

@Controller
public class DonneesHospitalieresController {

	@Autowired
	private ServletContextWrapper servletContextWrapper;	

	@Autowired
	private TestVirDAO testVirDAO;
	
	private static final Logger LOGGER = Logger.getLogger(DonneesHospitalieresController.class);

	@GetMapping("/dh-dc")
    public ModelAndView dc() throws Exception {
		ModelAndView modelAndView = new ModelAndView("dh-dc");
		Map<String, Object> model = new HashMap<>();

		TreeMap<LocalDate, Dh> dh = DonneesHospitalieres.getByDates(servletContextWrapper.getServletContext());
		LocalDate lastDayOfData = dh.lastKey();
		LocalDate dateMin = dh.firstKey().minusDays(1);
		LocalDate dateMax = lastDayOfData.plusDays(1);
	
		TreeMap<LocalDate, Dh> variations = DonneesHospitalieres.getDeltas(dh);
	
		TreeMap<String, Dh> cumulClasseAges = DonneesHospitalieres.getCumulClasseAges(servletContextWrapper.getServletContext(), lastDayOfData);
	
		//---- Projection
	
		LocalDate dateMinProj = lastDayOfData.minusWeeks(2);
		LocalDate dateMaxProj = lastDayOfData.plusWeeks(2);
	
		SimpleRegression simpleRegression = new SimpleRegression();
		for (LocalDate jour : variations.keySet()) {
			if (jour.compareTo(dateMinProj) > 0) {
				simpleRegression.addData((double) jour.toEpochDay(), (double) variations.get(jour).dc);
			}
		}
	
		Map<LocalDate, Double> proj = new HashMap<>();
		for (LocalDate jour = dateMinProj; jour.compareTo(dateMaxProj) <= 0; jour = jour.plusDays(1)) {
			proj.put(jour, simpleRegression.predict((double) jour.toEpochDay()));
		}
	
		//---- Calculate total dc
	
		Dh lastCumul = dh.get(lastDayOfData);
	
		//---- Alimentation du modèle
	
		model.put("lastDayOfData", lastDayOfData);
		model.put("totalDc", lastCumul.dc);
	
		model.put("dateMin", dateMin);
		model.put("dateMax", dateMax);
	
		model.put("dateMinProj", dateMinProj);
		model.put("dateMaxProj", dateMaxProj);
	
		model.put("dh", dh);
		model.put("variations", variations);
		model.put("cumulClasseAges", cumulClasseAges);
		model.put("proj", proj);

		modelAndView.addObject("model", model);
        return modelAndView;
    }

	@GetMapping("/dh-hosp")
    public ModelAndView hosp() throws Exception {
		ModelAndView modelAndView = new ModelAndView("dh-hosp");
		Map<String, Object> model = new HashMap<>();

		TreeMap<LocalDate, Dh> dh = DonneesHospitalieres.getByDates(servletContextWrapper.getServletContext());
		LocalDate lastDayOfData = dh.lastKey();
		LocalDate dateMin = dh.firstKey().minusDays(1);
		LocalDate dateMax = lastDayOfData.plusDays(1);
		
		TreeMap<LocalDate, Dh> nouveaux = DonneesHospitalieres.getNouveauxByDate(servletContextWrapper.getServletContext());

		TreeMap<String, Dh> cumulClasseAges = DonneesHospitalieres.getCumulClasseAges(servletContextWrapper.getServletContext(), lastDayOfData);

		//---- Projection

		LocalDate dateMinProj = lastDayOfData.minusWeeks(2);
		LocalDate dateMaxProj = lastDayOfData.plusWeeks(2);

		//---- Projection Linéaire

		SimpleRegression simpleRegression = new SimpleRegression();
		for (LocalDate jour : dh.keySet()) {
			if (jour.compareTo(dateMinProj) > 0) {
				simpleRegression.addData((double) jour.toEpochDay(), (double) dh.get(jour).hosp);
			}
		}

		Map<LocalDate, Double> proj = new HashMap<>();
		for (LocalDate jour = dateMinProj; jour.compareTo(dateMaxProj) <= 0; jour = jour.plusDays(1)) {
			proj.put(jour, simpleRegression.predict((double) jour.toEpochDay()));
		}

		//---- Projection Polynomiale

		PolynomialCurveFitter polynomialCurveFitter = PolynomialCurveFitter.create(2);
		List<WeightedObservedPoint> points = new ArrayList<>();

		for (LocalDate jour : dh.keySet()) {
			if (jour.compareTo(dateMinProj) >= 0) {
				points.add(new WeightedObservedPoint(1d, (double) jour.toEpochDay(), (double) dh.get(jour).hosp));
			}
		}
		double[] coeffs = polynomialCurveFitter.fit(points);
		PolynomialFunction polynomialFunction = new PolynomialFunction(coeffs);

		Map<LocalDate, Double> proj2 = new HashMap<>();

		for (LocalDate jour = dateMinProj; jour.isBefore(dateMaxProj); jour = jour.plusDays(1)) {
			proj2.put(jour, polynomialFunction.value(jour.toEpochDay()));
		}

		//---- Calculate total dc

		Dh lastCumul = dh.get(lastDayOfData);

		//---- Alimentation du modèle

		model.put("lastDayOfData", lastDayOfData);
		model.put("dernierHosp", lastCumul.hosp);

		model.put("dateMin", dateMin);
		model.put("dateMax", dateMax);

		model.put("dateMinProj", dateMinProj);
		model.put("dateMaxProj", dateMaxProj);

		model.put("dh", dh);
		model.put("nouveaux", nouveaux);
		model.put("cumulClasseAges", cumulClasseAges);
		model.put("proj", proj);
		model.put("proj2", proj2);
		
		modelAndView.addObject("model", model);
        return modelAndView;
	}

	@GetMapping("/dh-rea")
    public ModelAndView rea() throws Exception {
		ModelAndView modelAndView = new ModelAndView("dh-rea");
		Map<String, Object> model = new HashMap<>();
		
		TreeMap<LocalDate, Dh> dh = DonneesHospitalieres.getByDates(servletContextWrapper.getServletContext());
		LocalDate lastDayOfData = dh.lastKey();
		LocalDate dateMin = dh.firstKey().minusDays(1);
		LocalDate dateMax = lastDayOfData.plusDays(1);
		
		TreeMap<LocalDate, Dh> nouveaux = DonneesHospitalieres.getNouveauxByDate(servletContextWrapper.getServletContext());

		TreeMap<String, Dh> cumulClasseAges = DonneesHospitalieres.getCumulClasseAges(servletContextWrapper.getServletContext(), lastDayOfData);

		//---- Projection

		LocalDate dateMinProj = lastDayOfData.minusWeeks(2);
		LocalDate dateMaxProj = lastDayOfData.plusWeeks(2);

		//---- Projection Linéaire
		
		SimpleRegression simpleRegression = new SimpleRegression();
		for (LocalDate jour : dh.keySet()) {
			if (jour.compareTo(dateMinProj) > 0) {
				simpleRegression.addData((double) jour.toEpochDay(), (double) dh.get(jour).rea);
			}
		}

		Map<LocalDate, Double> proj = new HashMap<>();
		for (LocalDate jour = dateMinProj; jour.compareTo(dateMaxProj) <= 0; jour = jour.plusDays(1)) {
			proj.put(jour, simpleRegression.predict((double) jour.toEpochDay()));
		}

		//---- Projection Polynomiale

		PolynomialCurveFitter polynomialCurveFitter = PolynomialCurveFitter.create(2);
		List<WeightedObservedPoint> points = new ArrayList<>();

		for (LocalDate jour : dh.keySet()) {
			if (jour.compareTo(dateMinProj) >= 0) {
				points.add(new WeightedObservedPoint(1d, (double) jour.toEpochDay(), (double) dh.get(jour).rea));
			}
		}
		double[] coeffs = polynomialCurveFitter.fit(points);
		PolynomialFunction polynomialFunction = new PolynomialFunction(coeffs);

		Map<LocalDate, Double> proj2 = new HashMap<>();

		for (LocalDate jour = dateMinProj; jour.isBefore(dateMaxProj); jour = jour.plusDays(1)) {
			proj2.put(jour, polynomialFunction.value(jour.toEpochDay()));
		}

		//---- Calculate total dc

		Dh lastCumul = dh.get(lastDayOfData);

		//---- Alimentation du modèle

		model.put("lastDayOfData", lastDayOfData);
		model.put("dernierRea", lastCumul.rea);

		model.put("dateMin", dateMin);
		model.put("dateMax", dateMax);

		model.put("dateMinProj", dateMinProj);
		model.put("dateMaxProj", dateMaxProj);

		model.put("dh", dh);
		model.put("nouveaux", nouveaux);
		model.put("cumulClasseAges", cumulClasseAges);
		model.put("proj", proj);
		model.put("proj2", proj2);

		modelAndView.addObject("model", model);
        return modelAndView;
	}

	@GetMapping("/dh-ages")
    public ModelAndView ages() throws Exception {
		ModelAndView modelAndView = new ModelAndView("dh-ages");
		Map<String, Object> model = new HashMap<>();

		TreeMap<LocalDate, Dh> dh = DonneesHospitalieres.getByDates(servletContextWrapper.getServletContext());
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
				line1Json.add(factory.createArrayBuilder().add(jour.toString()).add(nouveaux.get(jour).hosp));
				line2Json.add(factory.createArrayBuilder().add(jour.toString()).add(nouveaux.get(jour).rea));
			}
			
			List<Long> testListValues1 = new ArrayList<>();
			List<Long> testListValues2 = new ArrayList<>();
			
			for (LocalDate jour : nouveaux.keySet()) {
				if (jour.isAfter(dateMin)) {
					testListValues1.add(nouveaux.get(jour).hosp);
					testListValues2.add(nouveaux.get(jour).rea);
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
			
			TreeMap<LocalDate, Dh> nouveaux = DonneesHospitalieres.getByDates(servletContextWrapper.getServletContext());
	
			dateMin = LocalDate.of(2020, 3, 31);
			dateMax = nouveaux.lastKey().plusDays(1);
			
			for (LocalDate jour : nouveaux.keySet()) {
				line1Json.add(factory.createArrayBuilder().add(jour.toString()).add(nouveaux.get(jour).hosp));
				line2Json.add(factory.createArrayBuilder().add(jour.toString()).add(nouveaux.get(jour).rea));
			}
			
			List<Long> testListValues1 = new ArrayList<>();
			List<Long> testListValues2 = new ArrayList<>();
			
			for (LocalDate jour : nouveaux.keySet()) {
				if (jour.isAfter(dateMin)) {
					testListValues1.add(nouveaux.get(jour).hosp);
					testListValues2.add(nouveaux.get(jour).rea);
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
