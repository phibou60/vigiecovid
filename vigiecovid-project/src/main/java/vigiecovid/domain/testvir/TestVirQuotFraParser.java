package vigiecovid.domain.testvir;

import static chamette.tools.CsvTools.getSeparator;
import static chamette.tools.CsvTools.normalizeDate;
import static chamette.tools.CsvTools.unquote;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

import chamette.datasets.EmptyLineException;
import chamette.datasets.ParseException;

public class TestVirQuotFraParser {

	private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getFormatterLogger(TestVirQuotFraParser.class);
	
	private String sep = null;
	private long parseExceptionCount = 0;
	
	private int colJour = 1;
	private int colPositifs = 4;
	private int colTests = 7;

	public TestVirQuotFraParser(String firstLine) {
		
		sep = getSeparator(firstLine);
		String[] splits = firstLine.split(sep);
		for (int i=0; i<splits.length; i++) {
			if (unquote(splits[i]).equals("jour")) colJour = i;
			if (unquote(splits[i]).equals("P")) colPositifs = i;
			if (unquote(splits[i]).equals("T")) colTests = i;
		}

	}
	
	public TestVirQuotFra parse(String line) throws ParseException, EmptyLineException {

		checkLine(line);
		String[] splits = line.split(sep);
		
		LocalDate jour = LocalDate.parse(normalizeDate(unquote(splits[colJour])));
		
		return new TestVirQuotFra(jour,
				Long.parseLong(splits[colPositifs]),
				Long.parseLong(splits[colTests]));
		
	}
	
	public Stream<TestVirQuotFra> parseToStream(String line) {

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

}
