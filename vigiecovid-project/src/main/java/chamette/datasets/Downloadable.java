package chamette.datasets;

/**
 * This class represents a downloaded DataSet (not calculated from another).
 *
 */
public interface Downloadable extends Dataset {
		
	/**
	 * This function is called by downloader engine to check if data are updated.
	 */
	public boolean checkUpdate() throws Exception;

}
