package chamette.tools;
/**
 * Helper class for javax.json package.
 */
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

public class JsonHelper {

	/**
	 * Dump a Json tree on the System.out console
	 * @param tree A Json tree
	 */
	public static void dumpTreeOnSystemOut(JsonValue tree) {
		dumpTreeOnSystemOut("", tree, null);	
	}
	
	/**
	 * Internal function
	 * @param indent Indentation of output
	 * @param jsonValue Json Tree
	 * @param key The name of the JsonValue
	 */
	private static void dumpTreeOnSystemOut(String indent, JsonValue jsonValue, String key) {
		if (key != null)
		      System.out.print(indent+"\"" + key + "\": ");
		switch(jsonValue.getValueType()) {
		      case OBJECT:
		    	 if (key == null) System.out.print(indent);
		         System.out.println("OBJECT {");
		         JsonObject object = (JsonObject) jsonValue;
		         for (String name : object.keySet())
		            dumpTreeOnSystemOut(indent+"    ", object.get(name), name);
		         System.out.println(indent+"}");
		         break;
		       case ARRAY:
		    	   JsonArray array = (JsonArray) jsonValue;
		    	   System.out.println("ARRAY size="+array.size()+" [");
		           for (JsonValue val : array) 
		               dumpTreeOnSystemOut(indent+"    ", val, null);
		           System.out.println(indent+"]");
		           break;
		      case STRING:
		         JsonString st = (JsonString) jsonValue;
		         System.out.println("(STRING) \"" + st.getString()+"\"");
		         break;
		      case NUMBER:
		         JsonNumber num = (JsonNumber) jsonValue;
		         System.out.println("(NUMBER) " + num.toString());
		         break;
		      case TRUE:
		      case FALSE:
		      case NULL:
		         System.out.println(indent+jsonValue.getValueType().toString());
		         break;
		}
	}
	
	/**
	 * Internal function
	 * @param indent Indentation of output
	 * @param jsonValue Json Tree
	 * @param key The name of the JsonValue
	 */
	/*
	private static void dumpTreeOnSystemOut(String indent, JsonValue jsonValue, String key, ArrayList<String> lines) {
		if (key != null)
		      //System.out.print(indent+"\"" + key + "\": ");
		switch(jsonValue.getValueType()) {
		      case OBJECT:
		    	 if (key == null) System.out.print(indent);
		         //System.out.println("OBJECT {");
		         JsonObject object = (JsonObject) jsonValue;
		         for (String name : object.keySet())
		            dumpTreeOnSystemOut(indent+"    ", object.get(name), name);
		         //System.out.println(indent+"}");
		         break;
		       case ARRAY:
		    	   JsonArray array = (JsonArray) jsonValue;
		    	   //System.out.println("ARRAY size="+array.size()+" [");
		           for (JsonValue val : array) 
		               dumpTreeOnSystemOut(indent+"    ", val, null);
		           System.out.println(indent+"]");
		           break;
		      case STRING:
		         JsonString st = (JsonString) jsonValue;
		         //System.out.println("(STRING) " + st.getString());
		         break;
		      case NUMBER:
		         JsonNumber num = (JsonNumber) jsonValue;
		         //System.out.println("(NUMBER) " + num.toString());
		         break;
		      case TRUE:
		      case FALSE:
		      case NULL:
		         //System.out.println(indent+jsonValue.getValueType().toString());
		         break;
		}
	}
*/
	/**
	 * Dump a Json tree and return a String with html format 
	 * @param jsonObject A Json tree
	 */
	public static String dumpTreeHtml(String json) throws Exception {
	    javax.json.JsonReader reader = javax.json.Json.createReader(new StringReader(json));
	    JsonStructure jsonStructure =reader.read();
	    return dumpTreeHtmlLine(jsonStructure, null, "<br/>", "");
	}
		
	/**
	 * Dump a Json tree and return a String with html format 
	 * @param jsonObject A Json tree
	 */
	public static String dumpTreeHtml(JsonStructure jsonStructure) throws Exception {
		
		StringWriter stWriter = new StringWriter();
		
		Map<String, Object> properties = new HashMap<String, Object>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		
		JsonWriterFactory factory = Json.createWriterFactory(properties);
		JsonWriter jsonWriter = factory.createWriter(stWriter);
		
		//JsonWriter jsonWriter = Json.createWriter(stWriter);
		jsonWriter.write(jsonStructure);
		String jsonString = stWriter.toString();
		
		String[] lines = jsonString.split("\n");
		
		StringBuffer ret = new StringBuffer();
		int l=1;
		for (String line : lines) {
			if (l > 1) {
				//ret.append("["+l+", "+line.length()+"]");
				char[] charArray = line.toCharArray();
				int i=0;
				for (; i < charArray.length && charArray[i] == ' '; i++) ret.append("&nbsp;");
				line = line.substring(i);
				ret.append(line+"<br>\n");
			}
			l++;
		}
		return ret.toString();
	}

	/**
	 * Dump a Json tree and return a String with html format 
	 * @param json A String that contains a json formated text
	 */
	public static String dumpTreeHtmlLine(String json) {
	    javax.json.JsonReader reader = javax.json.Json.createReader(new StringReader(json));
	    JsonStructure jsonStructure =reader.read();
		return dumpTreeHtmlLine(jsonStructure, null, "", "");
	}

	/**
	 * Dump a Json tree and return a String with html format 
	 * @param jsonObject A Json tree
	 */
	public static String dumpTreeHtmlLine(JsonStructure jsonStructure) {
		return dumpTreeHtmlLine(jsonStructure, null, "", "");
	}

	private static String dumpTreeHtmlLine(JsonValue jsonValue, String key, String endLine, String indent) {
		StringBuffer result = new StringBuffer();
		String realIndent = (endLine.length()>0 && key != null ? indent : "");
		if (key != null)
			result.append(realIndent+"\"<span style='color:navy'>"+key+"</span>\":\n");
		switch(jsonValue.getValueType()) {
			case OBJECT:
				result.append(" {"+endLine);
				JsonObject object = (JsonObject) jsonValue;
				int nb = object.keySet().size();
				for (String name : object.keySet()) {
					result.append(dumpTreeHtmlLine(object.get(name), name, endLine, indent+"&nbsp;&nbsp;&nbsp;&nbsp;"));
					nb--;
					if (nb > 0)result.append(", "+endLine);
				}
				result.append(endLine+realIndent+"}\n");
				break;
			case ARRAY:
				JsonArray array = (JsonArray) jsonValue;
				result.append(" [");
				boolean first = true;
				for (JsonValue val : array){
					if (first) first = false;
					else result.append(", "+endLine);
					result.append(dumpTreeHtmlLine(val, null, endLine, indent+"&nbsp;&nbsp;&nbsp;&nbsp;"));
				}
				result.append(realIndent+"]\n"+endLine);
				break;
			case STRING:
				JsonString st = (JsonString) jsonValue;
				result.append("\"<span style='color:green'>"+st.getString()+"</span>\"\n");
				break;
			case NUMBER:
				JsonNumber num = (JsonNumber) jsonValue;
				result.append("<span style='color:fuchsia'>"+num.toString()+"</span>\n");
				break;
			case TRUE:
			case FALSE:
			case NULL:
				result.append(jsonValue.getValueType().toString()+"\n");
				break;
		}
		return result.toString();
	}
	
	/**
	 * Transform a JsonObject to a Map of String for lightweight code
	 * 
	 * @param jsonValue A JsonObject
	 * @return Map of String
	 * @throws Exception
	 */
	public static Map<String, String> toStringMap(JsonValue jsonValue) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
			JsonObject jsonObject = (JsonObject) jsonValue;
	         for (String name : jsonObject.keySet()) {
	        	 JsonValue val = jsonObject.get(name);
	        	 if (val.getValueType() == JsonValue.ValueType.STRING) {
	        		 result.put(name, ((JsonString) val).getString());
	        	 } else if (val.getValueType() == JsonValue.ValueType.NUMBER
						|| val.getValueType() == JsonValue.ValueType.TRUE
						|| val.getValueType() == JsonValue.ValueType.FALSE
						|| val.getValueType() == JsonValue.ValueType.NULL) {
	        		 result.put(name, val.toString());
	         	 }
			}
		}
		return result;
	}
	/**
	 * Generate a String in json Format from a JsonObject.
	 * @param tree
	 * @return A valid String json representation of the object 
	 */
	public static String getStringFromJsonObject(JsonObject tree) {
		StringWriter stWriter = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(stWriter);
		jsonWriter.writeObject(tree);
		String jsonString = stWriter.toString();
		return jsonString;

	}
	
	/**
	 * Test
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		System.out.println("---------- demo json contruction ------------");
		
		JsonObject model = Json.createObjectBuilder()
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
		
		System.out.println("Done");
		
		System.out.println("---------- dump Tree On System Out ------------");
				
		dumpTreeOnSystemOut(model);
		
		System.out.println("---------- dummp tree with javax.json package Writer ------------");
		
		StringWriter stWriter = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(stWriter);
		jsonWriter.writeObject(model);
		String jsonString = stWriter.toString();
		System.out.println(jsonString);
		
		System.out.println("---------- demo parsing de texte ------------");
		
		String jsonData = 
				" {\"name\":\"Doe\", "+
				" \"surname\":\"John\", "+
				" \"age\":22,		"+
		
				" \"male\":true,		"+
				" \"female\":false,		"+
				" \"trans\":null,		"+
						
				"     \"address\" : {		"+
				"	         \"streetAddress\": \"21 2nd Street\",		"+
				"	         \"city\": \"New York\",		"+
				"	         \"state\": \"NY\",		"+
				"	         \"postalCode\": \"10021\"		"+
				"	     },				"+

				"  \"orders\" : [{\"order\":{\"num\":1, \"title\":\"game\"}},           "+
				"                {\"order\":{\"num\":2, \"title\":\"software\"}}],		"+
						
				" \"phoneNumber\": [         "+
			    "                 { \"type\": \"home\", \"number\": \"212 555-1234\" },         "+
			    "                 { \"type\": \"fax\", \"number\": \"646 555-4567\" }         "+
			    "             ]}";

		
		JsonReader reader = Json.createReader(new StringReader(jsonData));
		JsonStructure jsonst = reader.read();
		
		System.out.println("Done");
		
		System.out.println("---------- dump Tree On System Out ------------");
		
		//dumpTreeOnSystemOut(jsonst);
		System.out.println(dumpTreeHtmlLine(jsonst));
	}
	
}
