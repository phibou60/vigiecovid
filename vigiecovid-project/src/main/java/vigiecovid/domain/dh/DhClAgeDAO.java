package vigiecovid.domain.dh;

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
public class DhClAgeDAO {

	private final Logger LOGGER = Logger.getLogger(DhClAgeDAO.class);

	private ServletContext context;
	private Datasets datasets;
	
	public DhClAgeDAO(@Autowired ServletContext context) {
		super();
		LOGGER.info("Instanciate with context: "+context);
		this.context = context;
	}
	
	/**
	 * Cumule les hosp, rea, dc pour une date.
	 */

	public TreeMap<String, DhClAge> getCumulClasseAges(LocalDate jourSelection)
			throws Exception {
		
		DatasetHelper helper = new DatasetHelper(getDatasets(), "getCumulClasseAges_"+jourSelection,
				"donnees-hospitalieres-classe-age-covid19") {
			
			@Override
			public Object calculateData(Object parentData) throws Exception {
				String[] lines = (String[]) parentData;
				
				DhClAgeParser parser = new DhClAgeParser(lines[0]);
				
				Map<String, DhClAge> map = Stream.of(lines)
					.skip(1)
					.flatMap(l -> parser.parseToStream(l))
					.filter(d -> !d.getClAge().equals("0"))
					.filter(d -> d.getJour().equals(jourSelection))
					.collect(Collectors.toMap(
							DhClAge::getClAge,
							d -> d,
							(d1, d2) -> d1.plus(d2)));
				
				return new TreeMap<String, DhClAge>(map);
			}
		};
		return (TreeMap<String, DhClAge>) helper.getData();
		
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
