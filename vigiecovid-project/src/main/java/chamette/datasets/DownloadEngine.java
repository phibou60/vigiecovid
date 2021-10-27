package chamette.datasets;

import java.util.Date;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class DownloadEngine extends TimerTask { 
	private Datasets datasets;
	private Logger logger;
	private boolean setCancel = false;
	private Date lastUpdate;
	private Date lastCheck;
		
	public DownloadEngine(Datasets datasets) {
		logger = Logger.getLogger(this.getClass().getName());
		this.datasets = datasets;
	}
	
	public void checkUpdate() {
		lastCheck = new Date();
		for (Dataset dataset : datasets.getAllDatasets().values()) {
			if (dataset instanceof Downloadable) {
				try {
					if (setCancel) return;
					boolean update = ((Downloadable) dataset).checkUpdate();
					if (update) {
						lastUpdate = new Date();
						datasets.removeChildrenDatasets(dataset);
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}
	}
	
	public void run() {
		checkUpdate();
	}
	
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