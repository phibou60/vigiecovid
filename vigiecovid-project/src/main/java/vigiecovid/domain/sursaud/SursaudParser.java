package vigiecovid.domain.sursaud;

import static chamette.tools.CsvTools.getSeparator;
import static chamette.tools.CsvTools.normalizeDate;
import static chamette.tools.CsvTools.unquote;

import java.time.LocalDate;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;

import chamette.datasets.EmptyLineException;
import chamette.datasets.ParseException;

public class SursaudParser {

	private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getFormatterLogger(SursaudParser.class);
	private String sep = null;
	
	public Sursaud parse(String line) throws ParseException, EmptyLineException {

		line = line.trim();
		checkLine(line);
		String[] splits = normalizeLine(line);
		
		//-- Create Object Sursaud
		
		Sursaud sursaud;
	
		try {
			LocalDate jour = LocalDate.parse(splits[1]);
			
			long[] valeurs = new long[15];
			for (int i=0; i<valeurs.length; i++) {
				valeurs[i] = 0;
				int k = i+3;
				if (k < splits.length && splits[k].length() > 0) {
					valeurs[i] = Long.parseLong(splits[k]);
				}
			}
			sursaud = new Sursaud(splits[0], jour, splits[2], valeurs[0], valeurs[1], valeurs[2],
					valeurs[3], valeurs[4], valeurs[5], valeurs[6], valeurs[7], valeurs[8], valeurs[9],
					valeurs[10], valeurs[11], valeurs[12], valeurs[13], valeurs[14]);
		} catch (Exception e) {
			for (int i=0; i<splits.length; i++) {
				LOGGER.info("splits["+i+"] = \""+splits[i]+"\"");
			}
			throw new ParseException("Exception "+e+" on line: "+line);				
		}

		
		return sursaud;
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
	
		if (baseSplits.length < 3)  {
			throw new ParseException("Not enough parts ("+baseSplits.length+") with separator \""+sep+"\" on the line: "+line);
		}
	
		String[] splits = new String[18];
		for (int i = 0; i < splits.length; i++) {
			if (i < baseSplits.length) {
				splits[i] = baseSplits[i];
			} else {
				splits[i] = "0";
			}
		}
		
		splits[1] = normalizeDate(unquote(splits[1]));
		
		return splits;
		
	}

}
