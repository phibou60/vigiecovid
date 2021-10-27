package vigiecovid.domain.vacsi;

import static chamette.tools.CsvTools.getSeparator;
import static chamette.tools.CsvTools.normalizeDate;
import static chamette.tools.CsvTools.unquote;

import java.time.LocalDate;
import java.util.Arrays;

import org.apache.log4j.Logger;

import chamette.datasets.EmptyLineException;
import chamette.datasets.ParseException;

public class VacsiaParser {

	private final static Logger LOGGER = Logger.getLogger(VacsiaParser.class);
	private String sep = null;
	private static final int NB_PARTS = 9;
	
	public Vacsi parse(String line) throws ParseException, EmptyLineException {

		checkLine(line);
		String[] splits = normalizeLine(line);
		
		//-- Create Object Vacsia
		
		Vacsi vacsia;
	
		try {
			vacsia = new Vacsi(splits[1], "", LocalDate.parse(splits[2]),
					Long.parseLong(splits[3]), Long.parseLong(splits[4]),
					Long.parseLong(splits[5]),Long.parseLong(splits[6]),
					Double.parseDouble(splits[7]),
					Double.parseDouble(splits[8]));
		} catch (Exception e) {
			throw new ParseException("Exception "+e+" on line: "+line);
		}

		
		return vacsia;
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
	
		if (sep == null)  {
			sep = getSeparator(line);
		}
	
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
