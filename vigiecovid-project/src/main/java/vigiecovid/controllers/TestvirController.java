package vigiecovid.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import chamette.datasets.Datasets;
import vigiecovid.domain.ServletContextWrapper;
import vigiecovid.domain.TestVir;

@Controller
public class TestvirController {

	@Autowired
	private ServletContextWrapper servletContextWrapper;	

	@GetMapping("/testvir-day")
    public ModelAndView day(@RequestParam(value="dep", required=false) String dep,
    		@RequestParam(value="lib", required=false) String lib) throws Exception {
		ModelAndView modelAndView = new ModelAndView("testvir-day");
		ServletContext application = servletContextWrapper.getServletContext();

		Logger logger = Logger.getLogger(this.getClass());
		
		Map<String, Object> model = new HashMap<>();
	
		TreeMap<LocalDate, TestVir> byDays = TestVir.cumulTestVirByDay(application, dep, true);
	
		LocalDate lastDayOfData = byDays.lastKey();
		LocalDate dateMin = byDays.firstKey().minusDays(1);
		LocalDate dateMax = lastDayOfData.plusDays(1);
	
		// Population servant au calcul de l'incidence.
		// C'est la population Française par défaut.
		long population = 67000000l;
	
		// Si le département a été reçu en paramètre, la population est alors celle qui 
		// est prise en compte.
		if (dep != null) {
			Datasets datasets = (Datasets) application.getAttribute("datasets");
	
			HashMap<String, HashMap> departements
					= (HashMap<String, HashMap>) datasets.get("departements").getData();
			if (departements.get(dep) != null) {
				HashMap<String, Object> departement = (HashMap<String, Object>) departements.get(dep);
				population = (long) departement.get("PTOT");
			}
		}
	
		// Calcul de l'évolution de l'incidence
		TreeMap<LocalDate, TestVir> byWeeks = TestVir.cumulTestVirByWeeks(application, dep, true);
		TreeMap<LocalDate, Integer> incidences = new TreeMap<>();
		for (Map.Entry<LocalDate, TestVir> entry : byWeeks.entrySet()) {
			int incidence = Math.round(100_000 * entry.getValue().getPositifs() / population);
			incidences.put(entry.getKey(), incidence);
		}
		
		//---- Alimentation du modèle
	
		model.put("byDays", byDays);
		model.put("byWeeks", byWeeks);
		model.put("incidences", incidences);
		model.put("lastDayOfData", lastDayOfData);
		model.put("dateMin", dateMin);
		model.put("dateMax", dateMax);

		modelAndView.addObject("model", model);
        return modelAndView;
    }
	
	@GetMapping("/testvir-dep")
    public ModelAndView day(@RequestParam(value="day", required=false) String paramDay) throws Exception {
		ModelAndView modelAndView = new ModelAndView("testvir-dep");
		ServletContext application = servletContextWrapper.getServletContext();

		Logger logger = Logger.getLogger(this.getClass());

		Map<String, Object> model = new HashMap<>();

		LocalDate day = null;
		if (paramDay != null) {
			day = LocalDate.parse(paramDay);
		}
	
		TreeMap<LocalDate, TestVir> cumulByDay = TestVir.cumulTestVirByDay(application, null, false);
		model.put("cumulByDay", cumulByDay);
		
		if (day == null) {
			day = cumulByDay.lastKey();
		}
		model.put("day", day);
		
		TreeMap<String, Double> variations = TestVir.getVariations(application, day);
		model.put("variations", variations);
		
		TestVir testVir = cumulByDay.get(day);
		model.put("testVir", testVir);
		//model.put("incid", Math.round(((double)testVir.getPositifs() * 100) / testVir.getTests()));
		
		TreeMap<String, TestVir> cumulTestVirByDepLastWeek
				= TestVir.cumulTestVirByDepLastWeek(application, day);
		Datasets datasets = (Datasets) application.getAttribute("datasets");
		HashMap<String, HashMap> departements
				= (HashMap<String, HashMap>) datasets.get("departements").getData();
		
		TreeMap<String, Map> depStats = new TreeMap<>(); 
		
		for (Map.Entry<String, TestVir> entry : cumulTestVirByDepLastWeek.entrySet()) {
			Map<String, Object> dep = new HashMap<>();
	
			dep.put("tests", entry.getValue().getTests());
			dep.put("positifs",	entry.getValue().getPositifs());
			dep.put("pc", entry.getValue().getPc());
			
			if (departements.containsKey(entry.getKey())) {
				Map<String, Object> departement = departements.get(entry.getKey());
				dep.put("lib", departement.get("DEP"));
				long population = (Long) departement.get("PTOT");
				double incid = (double) entry.getValue().getPositifs() * 100_000 / population;
				dep.put("incid", Math.round(incid));
				depStats.put(entry.getKey(), dep);
			}
		}
		model.put("depStats", depStats);
		
		//---- Recherche jours précédent et suivant le jour en cours 
		LocalDate jourSuivant = cumulByDay.higherKey(day);
		if (jourSuivant != null) {
			model.put("jourSuivant", jourSuivant);
		}
		LocalDate jourPrec = cumulByDay.lowerKey(day);
		if (jourPrec != null) {
			model.put("jourPrec", jourPrec);
		}
	
		modelAndView.addObject("model", model);
        return modelAndView;
    }

}
