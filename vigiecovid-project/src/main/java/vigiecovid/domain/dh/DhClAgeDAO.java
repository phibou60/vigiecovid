package vigiecovid.domain.dh;

import java.time.LocalDate;
import java.util.List;
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
public class DhClAgeDAO {

	private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getFormatterLogger(DhClAgeDAO.class);

	private Datasets datasets;
	
	public DhClAgeDAO(@Autowired Datasets datasets) {
		super();
		LOGGER.info("Instanciate with context: "+datasets);
		this.datasets = datasets;
	}
	
	/**
	 * Cumule les hosp, rea, dc pour une date.
	 */

	public TreeMap<String, DhClAge> getCumulClasseAges(LocalDate jourSelection)
			throws Exception {
		
		DatasetHelper helper = new DatasetHelper(datasets, "getCumulClasseAges_"+jourSelection,
				"donnees-hospitalieres-classe-age-covid19") {
			
			@Override
			public Object calculateData(Object parentData) throws Exception {
				String[] lines = (String[]) parentData;
				
				DhClAgeParser parser = new DhClAgeParser(lines[0]);
				
				Map<String, DhClAge> map = Stream.of(lines)
					.skip(1)
					.flatMap(parser::parseToStream)
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
	
	public Map<String, List<DhClAge>> getCumulParDatesEtClasseAges() throws Exception {
		
		DatasetHelper helper = new DatasetHelper(datasets, "getCumulParDatesEtClasseAges",
				"donnees-hospitalieres-classe-age-covid19") {
			
			@Override
			public Object calculateData(Object parentData) throws Exception {
				String[] lines = (String[]) parentData;
				
				DhClAgeParser parser = new DhClAgeParser(lines[0]);
				
				Map<String, DhClAge> map = Stream.of(lines)
					.skip(1)
					.flatMap(parser::parseToStream)
					.filter(d -> !d.getClAge().equals("0"))
					.collect(Collectors.toMap(
							d -> d.getClAge()+";"+d.getJour(),
							dh -> dh,
							(d1, d2) -> d1.plus(d2)));
				
				map.entrySet().stream().limit(10).forEach(LOGGER::debug); 
				
				Map<String, List<DhClAge>> ret = map.values().stream()
					.collect(Collectors.groupingBy(DhClAge::getClAge));
				
				ret.entrySet().stream().limit(10).forEach(e -> {
					LOGGER.debug(e.getKey());
					e.getValue().stream().limit(10).forEach(LOGGER::debug);
				}); 
				
				return ret;
			}
		};
		return (Map<String, List<DhClAge>>) helper.getData();
		
	}
	
}
