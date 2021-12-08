package vigiecovid.domain.vacsi;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chamette.datasets.DatasetHelper;
import chamette.datasets.Datasets;

@Component
public class VacsiDAO {

	private final Logger LOGGER = Logger.getLogger(VacsiDAO.class);

	private ServletContext context;
	private Datasets datasets;
	
	public VacsiDAO(@Autowired ServletContext context) {
		super();
		LOGGER.info("Instanciate with context: "+context);
		this.context = context;
	}
	
	/**
	 * Retourne les cumuls de vaccination par jour au niveau france.
	 */
	public TreeMap<LocalDate, Vacsi> getVacsiFranceByDay() throws Exception {
		
		DatasetHelper helper
				= new DatasetHelper(getDatasets(), "getVacsiFranceByDay", "vacsi-a-fra") {
			
			@Override
			public Object calculateData(Object parentData) throws Exception {
				String[] lines = (String[]) parentData;
				
				VacsiaParser parser = new VacsiaParser(lines[0]);
				
				Map<LocalDate, Vacsi> map = Stream.of(lines)
					.filter(l -> l.startsWith("FR;0;"))
					.flatMap(l -> parser.parseToStream(l))
					.collect(Collectors.toMap(Vacsi::getJour, vacsi -> vacsi));
				
				return new TreeMap<LocalDate, Vacsi>(map);
			}
		};
		return (TreeMap<LocalDate, Vacsi>) helper.getData();
		
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
