package chamette.datasets;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.Test;

public class DatasetFromCsvFileTest {

	@Test
	public void testDatasetFromCsvFile() throws URISyntaxException {
		URI uri = this.getClass().getClassLoader().getResource("files/vacsi-a-fra.csv").toURI();
		String folder = Paths.get(uri).toFile().getParent();
		
		DatasetFromCsvFile dataset = new DatasetFromCsvFile(folder, "vacsi-a-fra");
		
		String[] lines = (String[]) dataset.getData();
		
		assertEquals(5176, lines.length);
		assertEquals("FR;04;2020-12-28;2;1;0;4;1;0;0.0;0.0;0.0", lines[2]);
	}

}
