package chamette.datasets;

import java.text.NumberFormat;

import org.apache.logging.log4j.Logger;

public abstract class DatasetHelper {

	private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getFormatterLogger(DatasetHelper.class);
	
	private Datasets datasets;
	private String myDatasetName;
	private String parentDatasetName;
	
	protected DatasetHelper(Datasets datasets, String myDatasetName, String parentDatasetName) {
		
		this.datasets = datasets;
		this.myDatasetName = myDatasetName;
		this.parentDatasetName = parentDatasetName;
	}
	
	public Object getData() throws Exception {
				
		if (datasets.containsKey(myDatasetName)) {
			LOGGER.info("Return cache: "+myDatasetName);
			return datasets.get(myDatasetName).getData();
		}
		
		if (!datasets.containsKey(parentDatasetName)) {
			return null;
		}
		
		LOGGER.info("Calculate new : "+myDatasetName);
		Dataset myDataset = new CommonDataset(myDatasetName);
		
		long start = System.nanoTime();
		Object data = calculateData(datasets.get(parentDatasetName).getData());
	
		long end = System.nanoTime();
		long duration = Math.round((end - start) / 1_000_000F);
		LOGGER.info("> Done in "+NumberFormat.getInstance().format(duration)+" ms");
		
		if (data == null) {
			throw new Exception("No data");
		}
		
		myDataset.setData(data);
		
		datasets.add(myDataset);
		datasets.get(parentDatasetName).addChildDataset(myDataset);

		return data;

	}
	
	public abstract Object calculateData(Object parentData) throws Exception;
	

}
