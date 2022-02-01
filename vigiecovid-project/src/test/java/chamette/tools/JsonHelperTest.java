package chamette.tools;

import static org.junit.Assert.*;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Test;

public class JsonHelperTest {

	@Test
	public void testGetStringFromJsonObject() {
		
		JsonObject tree = Json.createObjectBuilder()
				   .add("firstName", "Duke")
				   .add("lastName", "Java")
				   .add("age", 18)
				   .add("streetAddress", "100 Internet Dr")
				   .add("city", "JavaTown")
				   .add("state", "JA")
				   .add("postalCode", "12345")
				   .add("phoneNumbers", Json.createArrayBuilder()
				      .add(Json.createObjectBuilder()
				         .add("type", "mobile")
				         .add("number", "111-111-1111"))
				      .add(Json.createObjectBuilder()
				         .add("type", "home")
				         .add("number", "222-222-2222")))
				   .build();
		
		String text = JsonHelper.getStringFromJsonObject(tree);
		assertTrue(text.startsWith("{\"firstName\":\"Duke\",\"lastName\":\"Java\""));
		assertTrue(text.endsWith("\"number\":\"222-222-2222\"}]}"));
		
	}

}
