package vigiecovid.controllers;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vigiecovid.domain.sursaud.Sursaud;
import vigiecovid.domain.sursaud.SursaudsDAO;

@Controller
public class SursauController {

	@Autowired
	private SursaudsDAO sursaudsDAO;	
	
	private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getFormatterLogger(SursauController.class);


	@GetMapping("/sursau-day")
    public ModelAndView day(@RequestParam(value="dep", required=false) String dep,
    		@RequestParam(value="lib", required=false) String lib) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sursau-day");

		LOGGER.info("dep: " + dep + ", lib: " + lib);
		
		Map<String, Object> model = new HashMap<>();
	
		TreeMap<LocalDate, Sursaud> cumul = sursaudsDAO.cumulSursaudByDay(dep);
		
		TreeMap<String, Map> cumulByDates = new TreeMap<>(); 
		
		for (Map.Entry<LocalDate, Sursaud> entry : cumul.entrySet()) {
			Map<String, Object> element = new HashMap<>();
			Sursaud sursaud = entry.getValue();
			
			double pcPassgeCorona = 0.0d;
			double pcHospitCorona = 0.0d;
			if (sursaud.getNbrePassTot() > 0) {
				pcPassgeCorona = 100.0d * sursaud.getNbrePassCorona() / sursaud.getNbrePassTot();
				pcHospitCorona = 100.0d * sursaud.getNbreHospitCorona() / sursaud.getNbrePassTot();
			}
			element.put("pc_pass_corona", pcPassgeCorona);
			element.put("pc_hospit_corona", pcHospitCorona);
			
			double pcActeCorona = 0.0d;
			if (sursaud.getNbreActeTot() > 0) {
				pcActeCorona = 100.0d * sursaud.getNbreActeCorona() / sursaud.getNbreActeTot();
			}
			element.put("pc_acte_corona", pcActeCorona);
			
			cumulByDates.put(entry.getKey().toString(), element);
		}
		
		//---- Alimentation du modèle
		LOGGER.info("cumul.size(): "+cumul.size());
		
		model.put("cumulByDates", cumulByDates);
		model.put("lastDayOfData", cumul.lastKey().toString());
		model.put("dataDateMin", LocalDate.parse(cumul.firstKey().toString()).minusDays(1));
		model.put("dataDateMax", LocalDate.parse(cumul.lastKey().toString()).plusDays(1));

		modelAndView.addObject("model", model);
        return modelAndView;
    }

	@GetMapping("/sursau-dep")
    public ModelAndView dep() throws Exception {
		
		ModelAndView modelAndView = new ModelAndView("sursau-dep");
		
		JsonBuilderFactory factory = Json.createBuilderFactory(new HashMap<>());
		JsonObjectBuilder root = factory.createObjectBuilder();
		
		TreeMap<LocalDate, Sursaud> cumulByDay = sursaudsDAO.cumulSursaudByDay(null);
		
		LocalDate lastDay = cumulByDay.lastKey();
		root.add("lastDay", lastDay.toString());
		LocalDate fromDate = lastDay.minusDays(6);
		
		TreeMap<String, Sursaud> cumulByDep = sursaudsDAO.cumulSursaudByDep(fromDate, lastDay);
		
		JsonObjectBuilder deps = factory.createObjectBuilder();
		
		for (Map.Entry<String, Sursaud> entry : cumulByDep.entrySet()) {
			if (entry.getValue().getNbreActeTot() > 0) {
				double pc = (100.0d *entry.getValue().getNbreActeCorona() / entry.getValue().getNbreActeTot());
				deps.add(entry.getKey(), pc);
			}
		}
		root.add("deps", deps);
		
		JsonObject jsonModel = root.build();

		StringWriter out = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(out)) {
			jsonWriter.writeObject(jsonModel);
		}
		modelAndView.addObject("model", out.toString());

        return modelAndView;
    }

}
