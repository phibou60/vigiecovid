package chamette.datasets;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * A collection of Dataset.<br>
 * Dataset may be downloaded. So the Datasets class use a DownloadEngine to check 
 * if Datasets are updated.
 *
 */
public class Datasets {

	 // We use ConcurrentHashMap because of concurrent access
	private Map<String, Dataset> datasets = new ConcurrentHashMap<>();
	private Timer timer;
	private Logger logger;
	
	public Datasets() {
		logger = Logger.getLogger(this.getClass());
	}
	
	/**
	 * Check if this Dataset exists in the collection
	 * @param key
	 * @return true or false
	 */
	public boolean exists(String key) {
		return datasets.containsKey(key);
	}
	
	/**
	 * Get a Dataset by its name.
	 * @param key
	 * @return
	 */
	public Dataset get(String key) {
		return datasets.get(key);
	}
	
	/**
	 * Get all Datasets.
	 * @return
	 */
	public Map<String, Dataset> getAllDatasets() {
		return datasets;
	}
	
	/**
	 * Add a Dataset to the collection
	 * @param dataset
	 * @return
	 */
	public Dataset add(Dataset dataset) {
		logger.info("Add Dataset: "+dataset.getName());
		return datasets.put(dataset.getName(), dataset);
	}
	
	/**
	 * Remove a Dataset from the collection.
	 * @param dataset
	 * @return
	 */
	public Datasets remove(Dataset dataset) {
		Dataset removed = datasets.remove(dataset.getName());
		logger.info("Remove Dataset: "+dataset.getName()+" => "+(removed==null?"Unknown !!":"done"));
		return this;
	}
	
	/**
	 * Delete Children datasets if a new version of data is set for this object.
	 */
	public Datasets removeChildrenDatasets(Dataset dataset) {
		if (dataset.getChildren().isEmpty()) {
			logger.info("Remove child Datasets of "+dataset.getName()+": none");
		} else {
			logger.info("Remove child Datasets of "+dataset.getName()+": ");
			for (Dataset child : dataset.getChildren()) {
				remove(child);
				logger.info(" > "+child.getName());
			}
		}
		return this;
	}
	
	/**
	 * Start the download engine
	 */
	public Datasets startRefreshEngine() {
		DownloadEngine downloadEngine = new DownloadEngine(this);
		downloadEngine.checkUpdate();
		
		//--------------------------------------------------
		// ---- Create Scheduler 
		//--------------------------------------------------
		
		timer = new Timer(true); 
		long delay = 1000L*60*60; // one hour
		long period = delay;

		timer.scheduleAtFixedRate(downloadEngine, delay, period);
		return this;
		
	}
	
	/**
	 * Stop the download engine
	 */
	public void stopDownloadEngine() {
		try {
			if (timer != null) timer.cancel();
		} catch (Exception e) {
			logger.info("Exception in timer.cancel: "+e);
		}
	}
	
}
