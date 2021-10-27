package chamette.datasets;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import org.apache.log4j.Logger;

public class DatasetFromCsvFile extends CommonDataset { 

	public DatasetFromCsvFile(String folder, String filename) {
		super(filename);
		logger = Logger.getLogger(this.getClass());
		
		File file = new File(folder, filename+".csv");
		logger.info("Read CSV file: "+file.getAbsolutePath());
		
		try {
			List<String> lines = Files.readAllLines(file.toPath());
			String[] dataLines = lines.toArray(new String[lines.size()]);
			setData(dataLines);
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		
	}
	
}