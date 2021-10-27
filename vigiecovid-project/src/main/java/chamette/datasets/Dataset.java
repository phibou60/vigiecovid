package chamette.datasets;

import java.util.List;

/**
 * A Dataset is a cache of calculated data.<br>
 * A dataset calculated from a based dataset is a child of the based dataset.<br>
 * If a dataset is refreshed the children datasets must be also refreshed or be deteled.
 *
 */
public interface Dataset {
	
	/**
	 * Return the name of this Dataset.
	 */
	public String getName();

	/**
	 * Add a child dataset.<br>
	 * A child Dataset is a Dataset that is calculated from this Dataset.
	 */
	public Dataset addChildDataset(Dataset child);
	
	/**
	 * Return the children of this dataset.
	 */
	public List<Dataset> getChildren();

	/**
	 * Set the data.
	 */
	public Dataset setData(Object o);

	/**
	 * Get the data.
	 */
	public Object getData();
}
