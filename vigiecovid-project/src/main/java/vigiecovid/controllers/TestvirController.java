package vigiecovid.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vigiecovid.domain.Departement;
import vigiecovid.domain.DepartementsDAO;
import vigiecovid.domain.testvir.TestVir;
import vigiecovid.domain.testvir.TestVirDAO;
import vigiecovid.domain.testvir.TestVirTools;

@Controller
public class TestvirController {

	private static final Logger LOGGER = Logger.getLogger(TestvirController.class);

	@Autowired
	private TestVirDAO testVirDAO;	

	@Autowired
	private DepartementsDAO departementsDAO;	

	@GetMapping("/testvir-day")
    public ModelAndView day(@RequestParam(value="dep", required=false) String dep,
    		@RequestParam(value="lib", required=false) String lib) throws Exception {
		ModelAndView modelAndView = new ModelAndView("testvir-day");
		
		Map<String, Object> model = new HashMap<>();
	
		TreeMap<LocalDate, TestVir> byDays;
		if (dep != null) {
			byDays = testVirDAO.cumulTestVirByDay(dep, true);
		} else {
			byDays = testVirDAO.cumulTestVirByDay();
		}
	
		LocalDate lastDayOfData = byDays.lastKey();
		LocalDate dateMin = byDays.firstKey().minusDays(1);
		LocalDate dateMax = lastDayOfData.plusDays(1);
	
		// Population servant au calcul de l'incidence.
		// C'est la population Française par défaut.
		long population = 67_000_000L;
	
		// Si le département a été reçu en paramètre, la population est alors celle qui 
		// est prise en compte.
		if (dep != null) {
			Departement departement = departementsDAO.getById(dep);
			if (departement != null) {
				population = departement.getPopulationTotale();
			}
		}
	
		// Calcul de l'évolution de l'incidence
		
		TreeMap<LocalDate, TestVir> byWeeks = testVirDAO.cumulTestVirByWeeks(byDays);
		TreeMap<LocalDate, Long> incidences
				= TestVirTools.calculEvolIncidence(byWeeks, population);
		
		TreeMap<LocalDate, Double> proj
				= TestVirTools.calculPolynomialProjectionlIncidence(incidences);
		
		//---- Alimentation du modèle
	
		model.put("byDays", byDays);
		model.put("byWeeks", byWeeks);
		model.put("incidences", incidences);
		model.put("proj", proj);
		model.put("lastDayOfData", lastDayOfData);
		model.put("dateMin", dateMin);
		model.put("dateMax", dateMax);

		modelAndView.addObject("model", model);
        return modelAndView;
    }
	
	@GetMapping("/testvir-dep")
    public ModelAndView dep(@RequestParam(value="day", required=false) String paramDay) throws Exception {
		ModelAndView modelAndView = new ModelAndView("testvir-dep");

		Map<String, Object> model = new HashMap<>();

		LocalDate day = null;
		if (paramDay != null) {
			day = LocalDate.parse(paramDay);
		}
	
		TreeMap<LocalDate, TestVir> cumulByDay = testVirDAO.cumulTestVirByDay(null, false);
		model.put("cumulByDay", cumulByDay);
		
		if (day == null) {
			day = cumulByDay.lastKey();
		}
		model.put("day", day);
		
		TreeMap<String, Double> variations = testVirDAO.getVariations(day);
		model.put("variations", variations);
		
		TestVir testVir = cumulByDay.get(day);
		model.put("testVir", testVir);
		
		TreeMap<String, TestVir> cumulTestVirByDepLastWeek
				= testVirDAO.cumulTestVirByDepLastWeek(day);
		
		TreeMap<String, Map> depStats = new TreeMap<>(); 
		
		for (Map.Entry<String, TestVir> entry : cumulTestVirByDepLastWeek.entrySet()) {
			Map<String, Object> dep = new HashMap<>();
	
			dep.put("tests", entry.getValue().getTests());
			dep.put("positifs",	entry.getValue().getPositifs());
			dep.put("pc", entry.getValue().getPc());
			
			Departement departement = departementsDAO.getById(entry.getKey());
			
			if (departement != null) {
				dep.put("lib", departement.getLib());
				long population = departement.getPopulationTotale();
				double incid = (double) entry.getValue().getPositifs() * 100_000 / population;
				dep.put("incid", Math.round(incid));
				depStats.put(entry.getKey(), dep);
			}
		}
		model.put("depStats", depStats);
		
		//---- Recherche jours précédent et suivant le jour en cours 
		LocalDate jourSuivant = day.plusDays(1);
		if (jourSuivant != null) {
			model.put("jourSuivant", jourSuivant);
		}
		LocalDate jourPrec = day.minusDays(1);
		if (jourPrec != null) {
			model.put("jourPrec", jourPrec);
		}
	
		modelAndView.addObject("model", model);
        return modelAndView;
    }

}
