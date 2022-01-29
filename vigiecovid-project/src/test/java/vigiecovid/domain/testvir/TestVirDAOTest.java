package vigiecovid.domain.testvir;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.TreeMap;

import org.junit.Test;

import chamette.datasets.DatasetFromCsvFile;
import chamette.datasets.Datasets;

public class TestVirDAOTest {

	@Test
	public void testCumulTestVirByDay() throws Exception {
		
		ClassLoader cl = this.getClass().getClassLoader();
		URI uri = cl.getResource("files/sp-pos-quot-fra.csv").toURI();
		String folder = Paths.get(uri).toFile().getParent();
		
		Datasets datasets = new Datasets();
		datasets.add(new DatasetFromCsvFile(folder, "sp-pos-quot-fra"));
		
		TestVirDAO dao =  new TestVirDAO(datasets);
		
		TreeMap<LocalDate, TestVir> map = dao.cumulTestVirByDay();
		
		assertEquals(602, map.size());

		TestVir t = map.get(LocalDate.of(2021, 12, 7));
		
		assertEquals(57991, t.getPositifs());
		assertEquals(909709, t.getTests());
		assertEquals(637, Math.round(t.getPc()*100));
	}

}
