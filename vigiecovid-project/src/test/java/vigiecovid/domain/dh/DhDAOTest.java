package vigiecovid.domain.dh;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.TreeMap;

import org.junit.Test;

import chamette.datasets.DatasetFromCsvFile;
import chamette.datasets.Datasets;

public class DhDAOTest {

	@Test
	public void testGetDhByDay() throws Exception {
		URI uri = this.getClass().getClassLoader().getResource("files/donnees-hospitalieres-covid19.csv").toURI();
		String folder = Paths.get(uri).toFile().getParent();
		
		Datasets datasets = new Datasets();
		datasets.add(new DatasetFromCsvFile(folder, "donnees-hospitalieres-covid19"));
		
		DhDAO vacsiDAO =  new DhDAO(null);
		vacsiDAO.setDatasets(datasets);
		
		TreeMap<LocalDate, Dh> map = vacsiDAO.getDhByDay();
		
		assertEquals(630, map.size()); // not verified

		Dh dh = map.get(LocalDate.of(2021, 10, 15));
		
		assertEquals("2021-10-15", dh.getJour().toString());
		assertEquals(6470, dh.getHosp());
		assertEquals(1051, dh.getRea());
		assertEquals(424693, dh.getRad()); // not verified
		assertEquals(90352, dh.getDc());
		
		// test getDeltasByDay
		
		TreeMap<LocalDate, Dh> deltas = vacsiDAO.getDeltasDhByDay();
		
		assertEquals(629, deltas.size()); // not verified

		Dh dh2 = deltas.get(LocalDate.of(2021, 10, 15));
		
		assertEquals("2021-10-15", dh2.getJour().toString());
		assertEquals(-53, dh2.getHosp());
		assertEquals(-24, dh2.getRea());
		assertEquals(229, dh2.getRad()); // not verified
		assertEquals(34, dh2.getDc());

	}

}
