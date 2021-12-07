package chamette.datasets;

import org.apache.log4j.Logger;

public abstract class DatasetHelper {

	private final Logger LOGGER = Logger.getLogger(DatasetHelper.class);
	
	private Datasets datasets;
	private String myDatasetName;
	private String parentDatasetName;
	
	public DatasetHelper(Datasets datasets, String myDatasetName, String parentDatasetName) {
		
		this.datasets = datasets;
		this.myDatasetName = myDatasetName;
		this.parentDatasetName = parentDatasetName;
	}
	
	public Object getData() throws Exception {
				
		if (datasets.exists(myDatasetName)) {
			LOGGER.info("Return cache: "+myDatasetName);
			return datasets.get(myDatasetName).getData();
		}
		
		if (!datasets.exists(parentDatasetName)) {
			return null;
		}
		
		LOGGER.info("Calculate new : "+myDatasetName);
		Dataset myDataset = new CommonDataset(myDatasetName);
		
		Object data = calculateData(datasets.get(parentDatasetName).getData());

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
