package vigiecovid.domain.vacsi;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.TreeMap;

import org.junit.Test;

import chamette.datasets.DatasetFromCsvFile;
import chamette.datasets.Datasets;

public class VacsiDAOTest {

	@Test
	public void testGetVacsiFranceByDay() throws Exception {
		URI uri = this.getClass().getClassLoader().getResource("files/vacsi-a-fra.csv").toURI();
		String folder = Paths.get(uri).toFile().getParent();
		
		Datasets datasets = new Datasets();
		datasets.add(new DatasetFromCsvFile(folder, "vacsi-a-fra"));
		
		VacsiDAO vacsiDAO =  new VacsiDAO(null);
		vacsiDAO.setDatasets(datasets);
		
		TreeMap<LocalDate, Vacsi> map = vacsiDAO.getVacsiFranceByDay();
		
		assertEquals(345, map.size());

		Vacsi vacsi = map.get(LocalDate.of(2021, 11, 6));
		assertEquals("0", vacsi.getClage());
		assertEquals("2021-11-06", vacsi.getJour().toString());
		
		assertEquals(20748, vacsi.getDose1());
		assertEquals(32626, vacsi.getComplet());
		assertEquals(67973, vacsi.getRappel());
		
		assertEquals(51535581, vacsi.getCumDose1());
		assertEquals(50252899, vacsi.getCumComplet());
		assertEquals(3670580, vacsi.getCumRappel());
		
		assertEquals(7680, Math.round(vacsi.getCouvDose1()*100));
		assertEquals(7490, Math.round(vacsi.getCouvComplet()*100));
		assertEquals(550, Math.round(vacsi.getCouvRappel()*100));

		
	}

}
