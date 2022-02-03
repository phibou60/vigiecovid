package vigiecovid.domain.vacsi;

import static chamette.tools.CsvTools.getSeparator;
import static chamette.tools.CsvTools.normalizeDate;
import static chamette.tools.CsvTools.unquote;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

import chamette.datasets.EmptyLineException;
import chamette.datasets.ParseException;

public class VacsiaParser {

	private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getFormatterLogger(VacsiaParser.class);
	
	private String sep = null;
	private long parseExceptionCount = 0;
	private static final int NB_PARTS = 12;
	
	public VacsiaParser(String firstLine) {
		
		sep = getSeparator(firstLine);

	}
	
	public Vacsi parse(String line) throws ParseException, EmptyLineException {

		checkLine(line);
		String[] splits = normalizeLine(line);

		return new Vacsi(
				splits[1], "", LocalDate.parse(splits[2]),
				Long.parseLong(splits[3]), Long.parseLong(splits[4]), Long.parseLong(splits[5]),
				Long.parseLong(splits[6]),Long.parseLong(splits[7]), Long.parseLong(splits[8]),
				Double.parseDouble(splits[9]),
				Double.parseDouble(splits[10]),
				Double.parseDouble(splits[11]));
		
	}
	
	public Stream<Vacsi> parseToStream(String line) {

		try {
			return Stream.of(parse(line));
		} catch (Exception e) {
			parseExceptionCount++;
			if (parseExceptionCount < 5) {
				LOGGER.error("Exception " + e + " on line: " + line);
			}
			return Stream.empty();	
		}
			
	}
	
	private void checkLine(String line) throws ParseException, EmptyLineException {

		if (line == null)  {
			throw new ParseException("Line is null");
		}
	
		line = line.trim();
	
		if (line.length() == 0 || line.startsWith("dep"))  {
			throw new EmptyLineException();
		}
		
	}
	
	private String[] normalizeLine(String line) throws ParseException {
	
		String[] baseSplits = line.split(sep);
		LOGGER.debug("baseSplits: "+Arrays.toString(baseSplits));
	
		if (baseSplits.length < NB_PARTS)  {
			throw new ParseException("Not enough parts ("+baseSplits.length+") with separator \""+sep+"\" on the line: "+line);
		}
	
		String[] splits = new String[NB_PARTS];
		for (int i = 0; i < splits.length; i++) {
			if (i < baseSplits.length) {
				splits[i] = baseSplits[i];
			} else {
				splits[i] = "0";
			}
		}
		
		splits[2] = normalizeDate(unquote(splits[2]));
		
		return splits;
		
	}

}
