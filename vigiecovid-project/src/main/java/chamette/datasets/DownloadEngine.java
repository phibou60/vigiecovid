package chamette.datasets;

import java.util.Date;
import java.util.TimerTask;
import org.apache.logging.log4j.Logger;

/**
 * Class that check if Datasets are updated a,nd must be downloaded again.
 *
 */
public class DownloadEngine extends TimerTask { 
	private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getFormatterLogger(DownloadEngine.class);
	private Datasets datasets;
	private boolean setCancel = false;
	private Date lastUpdate;
	private Date lastCheck;
		
	public DownloadEngine(Datasets datasets) {
		this.datasets = datasets;
	}
	
	public void checkUpdate() {
		LOGGER.info("**** Check Update ****");
		lastCheck = new Date();
		for (Dataset dataset : datasets.values()) {
			if (dataset instanceof Downloadable) {
				try {
					if (setCancel) return;
					boolean update = ((Downloadable) dataset).checkUpdate();
					if (update) {
						lastUpdate = new Date();
						datasets.removeChildrenDatasets(dataset);
					}
				} catch (Exception e) {
					LOGGER.error("Exception: ", e);
				}
			}
		}
	}
	
	public void run() {
		checkUpdate();
	}
	
	@Override
	public boolean cancel() {
		setCancel = true;
		return super.cancel();
	}
	
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public Date getLastCheck() {
		return lastCheck;
	}
	
}