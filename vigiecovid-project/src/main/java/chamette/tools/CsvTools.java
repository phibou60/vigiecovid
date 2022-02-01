package chamette.tools;

public class CsvTools {

	/**
	 * CSV helpers tools.
	 */
	
	private CsvTools() {
		// Can't be instantiated
	}
	
	public static String getSeparator(String line) {
		line = line.trim();
		String[] splits1 = line.split(";");
		String[] splits2 = line.split(",");
		String[] splits3 = line.split("\t");
		if (splits1.length > splits2.length && splits1.length > splits3.length) {
			return ";";
		}
		if (splits2.length > splits1.length && splits2.length > splits3.length) {
			return ",";
		}
		return "\t";
	}

	public static String unquote(String s) {
		s = s.trim();
		if (s.startsWith("\"") && s.endsWith("\"")) {
			return s.substring(1, s.length()-1);
		}
		return s;
	}

	public static String normalizeDate(String s) {
		s = unquote(s);
		if (s.matches("[0-9][0-9][0-9][0-9].[0-9][0-9].[0-9][0-9]")) {
			s = s.replace('/', '-');
			return s;
		}
		if (s.matches("[0-9][0-9].[0-9][0-9].[0-9][0-9][0-9][0-9]")) {
			s = s.substring(6) + "-" + s.substring(3, 5) + "-" + s.substring(0, 2);
			return s;
		}
		return s;
	}

}
