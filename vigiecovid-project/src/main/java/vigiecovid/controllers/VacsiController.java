package vigiecovid.controllers;

import java.time.LocalDate;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import vigiecovid.domain.vacsi.Vacsi;
import vigiecovid.domain.vacsi.VacsiDAO;

@Controller
public class VacsiController {

	private static final Logger LOGGER = Logger.getLogger(VacsiController.class);

	@Autowired
	private VacsiDAO vacsiDAO;	
	
	@GetMapping("/vacsi")
    public ModelAndView vacsi() throws Exception {

		ModelAndView modelAndView = new ModelAndView("vacsi");
		
		TreeMap<LocalDate, Vacsi> franceByDay = vacsiDAO.getVacsiFranceByDay();
		LOGGER.info("franceByDay.size(): "+franceByDay.size());

		LocalDate lastDayOfData = franceByDay.lastKey();
		LocalDate dateMin = franceByDay.firstKey().minusDays(1);
		LocalDate dateMax = lastDayOfData.plusDays(1);

		//---- Alimentation du modèle

		modelAndView.addObject("lastDayOfData", lastDayOfData);

		modelAndView.addObject("dateMin", dateMin);
		modelAndView.addObject("dateMax", dateMax);

		modelAndView.addObject("franceByDay", franceByDay);

        return modelAndView;
    }
}
