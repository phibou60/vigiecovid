package chamette.datasets;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class DatasetFromCsvFile extends CommonDataset { 

	private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getFormatterLogger(DatasetFromCsvFile.class);

	public DatasetFromCsvFile(String folder, String filename) {
		super(filename);
		
		File file = new File(folder, filename+".csv");
		LOGGER.info("Read CSV file: "+file.getAbsolutePath());
		
		try {
			List<String> lines = Files.readAllLines(file.toPath());
			String[] dataLines = lines.toArray(new String[lines.size()]);
			setData(dataLines);
		} catch(Exception e) {
			LOGGER.error("Exception: ", e);
		}
		
	}
	
}