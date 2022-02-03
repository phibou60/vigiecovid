package chamette.datasets;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;

/**
 * A collection of Dataset.<br>
 * Dataset may be downloaded. So the Datasets class use a DownloadEngine to check 
 * if Datasets are updated.
 *
 */
public class Datasets extends ConcurrentHashMap<String, Dataset> {

	private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getFormatterLogger(Datasets.class);
	private Timer timer;
	
	/**
	 * Add a Dataset to the collection
	 * @param dataset
	 * @return
	 */
	public Datasets add(Dataset dataset) {
		LOGGER.info("Add Dataset: "+dataset.getName());
		put(dataset.getName(), dataset);
		return this;
	}
	
	/**
	 * Remove a Dataset from the collection.
	 * @param dataset
	 * @return
	 */
	public Datasets remove(Dataset dataset) {
		Dataset removed = remove(dataset.getName());
		LOGGER.info("Remove Dataset: "+dataset.getName()+" => "+(removed==null?"Unknown !!":"done"));
		removeChildrenDatasets(dataset);
		return this;
	}
	
	/**
	 * Delete Children datasets if a new version of data is set for this object.
	 */
	public Datasets removeChildrenDatasets(Dataset dataset) {
		if (dataset.getChildren().isEmpty()) {
			LOGGER.info("Remove child Datasets of "+dataset.getName()+": none");
		} else {
			LOGGER.info("Remove child Datasets of "+dataset.getName()+": ");
			for (Dataset child : dataset.getChildren()) {
				remove(child);
				LOGGER.info(" > "+child.getName());
			}
		}
		return this;
	}
	
	/**
	 * Start the download engine
	 */
	public Datasets startDownloadEngine() {
		LOGGER.info("startDownloadEngine");
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
		LOGGER.info("stopDownloadEngine");
		try {
			if (timer != null) timer.cancel();
		} catch (Exception e) {
			LOGGER.info("Exception in timer.cancel: "+e);
		}
	}
	
}
