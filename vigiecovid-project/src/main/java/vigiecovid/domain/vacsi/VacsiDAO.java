package vigiecovid.domain.vacsi;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chamette.datasets.DatasetHelper;
import chamette.datasets.Datasets;

@Component
public class VacsiDAO {

	private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getFormatterLogger(VacsiDAO.class);

	private Datasets datasets;
	
	public VacsiDAO(@Autowired Datasets datasets) {
		super();
		LOGGER.info("Instanciate with context: "+datasets);
		this.datasets = datasets;
	}
	
	/**
	 * Retourne les cumuls de vaccination par jour au niveau france.
	 */
	public TreeMap<LocalDate, Vacsi> getVacsiFranceByDay() throws Exception {
		
		DatasetHelper helper
				= new DatasetHelper(datasets, "getVacsiFranceByDay", "vacsi-a-fra") {
			
			@Override
			public Object calculateData(Object parentData) throws Exception {
				String[] lines = (String[]) parentData;
				
				VacsiaParser parser = new VacsiaParser(lines[0]);
				
				Map<LocalDate, Vacsi> map = Stream.of(lines)
					.skip(1)
					.filter(l -> l.startsWith("FR;0;"))
					.flatMap(parser::parseToStream)
					.collect(Collectors.toMap(Vacsi::getJour, vacsi -> vacsi));
				
				return new TreeMap<LocalDate, Vacsi>(map);
			}
		};
		return (TreeMap<LocalDate, Vacsi>) helper.getData();
		
	}
	
}
