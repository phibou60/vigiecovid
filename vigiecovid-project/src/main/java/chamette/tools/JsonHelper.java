package chamette.tools;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;

/**
 * Helper class for javax.json package.
 */

public class JsonHelper {

	private JsonHelper() {
		// Can't be instantiated
	}

	/**
	 * Generate a String in json Format from a JsonObject.
	 * @param tree
	 * @return A valid String json representation of the object 
	 */
	public static String getStringFromJsonObject(JsonObject tree) {
		StringWriter stWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stWriter)) {
			jsonWriter.writeObject(tree);
			return stWriter.toString();
		}
	}

}
