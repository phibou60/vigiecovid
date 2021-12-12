package vigiecovid.domain.dh;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.TreeMap;

import org.junit.Test;

import chamette.datasets.DatasetFromCsvFile;
import chamette.datasets.Datasets;

public class DhClAgeDAOTest {

	@Test
	public void testGetDhByDay() throws Exception {
		ClassLoader cl = this.getClass().getClassLoader();
		URI uri = cl.getResource("files/donnees-hospitalieres-classe-age-covid19.csv").toURI();
		String folder = Paths.get(uri).toFile().getParent();
		
		Datasets datasets = new Datasets();
		datasets.add(new DatasetFromCsvFile(folder, "donnees-hospitalieres-classe-age-covid19"));
		
		DhClAgeDAO dhClAgeDAO =  new DhClAgeDAO(null);
		dhClAgeDAO.setDatasets(datasets);
		
		TreeMap<String, DhClAge> map = dhClAgeDAO.getCumulClasseAges(LocalDate.of(2021, 12, 7));
		
		assertEquals(10, map.size());

		DhClAge dhClAge = map.get("69");
		
		assertEquals(2390, dhClAge.getHosp());
		assertEquals(729, dhClAge.getRea());
		assertEquals(76930, dhClAge.getRad()); // not verified
		assertEquals(725, dhClAge.getDc());

		assertEquals(107, map.get("09").getHosp());
		assertEquals(56, map.get("19").getHosp());
		assertEquals(211, map.get("29").getHosp());
		assertEquals(423, map.get("39").getHosp());
		assertEquals(676, map.get("49").getHosp());
		assertEquals(1364, map.get("59").getHosp());
		assertEquals(2390, map.get("69").getHosp());
		assertEquals(2993, map.get("79").getHosp());
		assertEquals(2942, map.get("89").getHosp());
		assertEquals(1478, map.get("90").getHosp());

	}

}
