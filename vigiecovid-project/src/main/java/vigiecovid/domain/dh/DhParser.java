package vigiecovid.domain.dh;

import static chamette.tools.CsvTools.getSeparator;
import static chamette.tools.CsvTools.normalizeDate;
import static chamette.tools.CsvTools.unquote;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import chamette.datasets.EmptyLineException;
import chamette.datasets.ParseException;

public class DhParser {

	private final static Logger LOGGER = Logger.getLogger(DhParser.class);
	
	private String sep = null;
	private long parseExceptionCount = 0;
	
	private int colHosp = 3;
	private int colRea  = 4;
	private int colRad  = 5;
	private int colDc   = 6;

	public DhParser(String firstLine) {
		
		sep = getSeparator(firstLine);
		String[] splits = firstLine.split(sep);
		for (int i=0; i<splits.length; i++) {
			if (unquote(splits[i]).equals("hosp")) colHosp = i;
			if (unquote(splits[i]).equals("rea"))  colRea = i;
			if (unquote(splits[i]).equals("rad"))  colRad = i;
			if (unquote(splits[i]).equals("dc"))   colDc = i;
		}

	}
	
	public Dh parse(String line) throws ParseException, EmptyLineException {

		checkLine(line);
		String[] splits = normalizeLine(line);
		
		LocalDate jour = LocalDate.parse(normalizeDate(unquote(splits[2])));
		
		return new Dh(unquote(splits[0]), unquote(splits[1]), jour,
				Long.parseLong(splits[colHosp]),
				Long.parseLong(splits[colRea]),
				Long.parseLong(splits[colRad]),
				Long.parseLong(splits[colDc])
			);
		
	}
	
	public Stream<Dh> parseToStream(String line) {

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
	
		if (line.length() == 0)  {
			throw new EmptyLineException();
		}
		
	}
	
	private String[] normalizeLine(String line) throws ParseException {
	
		String[] splits = line.split(sep);
		
		if (splits.length < 3)  {
			throw new ParseException("Not enough parts ("+splits.length+") with separator \""
					+sep+"\" on the line: "+line);
		}

		LOGGER.debug("splits: "+Arrays.toString(splits));
		
		splits[2] = normalizeDate(unquote(splits[2]));
		
		return splits;
		
	}

}
