package chamette.datasets;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A basic implementation of a Dataset that can be instantiated directly.<br>
 * The datas must be provided by calling the method setDate(). 
 *
 */
public class CommonDataset implements Dataset {
	
	private String name;
	protected Object data;
	private List<Dataset> children = new ArrayList<>();
	protected Logger logger;

	public CommonDataset(String name) {
		this.name = name;
	}
	
	/**
	 * Return the name of this Dataset.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Add a child dataset.<br>
	 * A child Dataset is a Dataset that is calculated from this Dataset.
	 */
	public Dataset addChildDataset(Dataset child) {
		logger.info("Add child Dataset ("+child.getName()+") to "+name);
		children.add(child);
		return this;
	}
	
	/**
	 * Return the children of this dataset.
	 */
	public List<Dataset> getChildren() {
		return children;
	}

	/**
	 * Set the data.
	 */
	public Dataset setData(Object o) {
		data = o;
		return this;
	}

	/**
	 * Get the data.
	 */
	public Object getData() {
		return data;
	}

}
