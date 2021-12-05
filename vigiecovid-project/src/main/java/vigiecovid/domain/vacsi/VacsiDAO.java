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
import chamette.datasets.EmptyLineException;
import chamette.datasets.ParseException;

@Component
public class VacsiDAO {

	private final Logger LOGGER = Logger.getLogger(VacsiDAO.class);

	private ServletContext context;
	
	public VacsiDAO(@Autowired ServletContext context) {
		super();
		LOGGER.info("Instanciate with context: "+context);
		this.context = context;
	}
	
	/**
	 * Retourne les cumuls de vaccination par jour ai niveau france.
	 * @param context Obligatoire
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public TreeMap<LocalDate, Vacsi> getVacsiFranceByDay() throws Exception {
		
		return (TreeMap<LocalDate, Vacsi>)
				new DatasetHelper(context, "getVacsiFranceByDay", "vacsi-a-fra") {
			
			@Override
			public Object calculateData(Object parentData) throws Exception {
				String[] lines = (String[]) parentData;
				
				VacsiaParser parser = new VacsiaParser(lines[0]);
				
				Map<LocalDate, Vacsi> map = Stream.of(lines)
					.filter(l -> l.startsWith("FR;0;"))
					.map(l -> parser.parse(l))
					.filter(vacsi -> vacsi.getJour() != null)
					.collect(Collectors.toMap(Vacsi::getJour, vacsi -> vacsi));
				
				return new TreeMap<LocalDate, Vacsi>(map);
			}
		}.getData();
		
	}

}
