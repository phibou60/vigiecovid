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
public class DhDAO {

	private final Logger LOGGER = Logger.getLogger(DhDAO.class);

	private ServletContext context;
	private Datasets datasets;
	
	public DhDAO(@Autowired ServletContext context) {
		super();
		LOGGER.info("Instanciate with context: "+context);
		this.context = context;
	}
	
	/**
	 * Retourne les cumuls par jour au niveau france.
	 */

	public TreeMap<LocalDate, Dh> getDhByDay() throws Exception {
		
		DatasetHelper helper
				= new DatasetHelper(getDatasets(), "getDhByDay", "donnees-hospitalieres-covid19") {
			
			@Override
			public Object calculateData(Object parentData) throws Exception {
				String[] lines = (String[]) parentData;
				
				DhParser parser = new DhParser(lines[0]);
				
				Map<LocalDate, Dh> map = Stream.of(lines)
					.skip(1)
					.flatMap(l -> parser.parseToStream(l))
					.filter(d -> d.getSexe().equals("0"))
					.collect(Collectors.toMap(
							Dh::getJour,
							dh -> dh,
							(d1, d2) -> d1.plus(d2)));
				
				return new TreeMap<LocalDate, Dh>(map);
			}
		};
		return (TreeMap<LocalDate, Dh>) helper.getData();
		
	}
	
	/**
	 * Calcule le deltas d'un jour à l'autre
	 */

	public TreeMap<LocalDate, Dh> getDeltasDhByDay() throws Exception {
		
		DatasetHelper helper
				= new DatasetHelper(getDatasets(), "getDeltasDhByDay", "getDhByDay") {
			
			@Override
			public Object calculateData(Object parentData) throws Exception {
				TreeMap<LocalDate, Dh> from = (TreeMap<LocalDate, Dh>) parentData;
				
				TreeMap<LocalDate, Dh> ret = new TreeMap<>();
				Dh prev = null;
				
				for (Map.Entry<LocalDate, Dh> entry : from.entrySet()) {
					if (prev != null) {
						ret.put(entry.getKey(), new Dh("", "", entry.getKey(), 
							entry.getValue().hosp - prev.hosp,
							entry.getValue().rea  - prev.rea,
							entry.getValue().rad  - prev.rad,
							entry.getValue().dc   - prev.dc
						));
					}
					prev = entry.getValue();
				}
				return ret;
			}
		};
		return (TreeMap<LocalDate, Dh>) helper.getData();
		
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
