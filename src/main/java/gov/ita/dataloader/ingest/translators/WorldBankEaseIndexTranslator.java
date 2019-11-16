package gov.ita.dataloader.ingest.translators;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.CharSequenceReader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorldBankEaseIndexTranslator implements Translator {

  @Override
  public byte[] translate(byte[] bytes) {
    StringWriter stringWriter = new StringWriter();
    CSVPrinter csvPrinter;

    try {
      csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT
        .withHeader("Country_Name", "Country_Code", "Indicator_Name", "Indicator_Code", "Year", "Val"));

      Reader reader = new CharSequenceReader(new String(bytes));
      CSVParser csvParser;
      csvParser = new CSVParser(
        reader,
        CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim().withIgnoreHeaderCase());

      Map<String, Integer> headers = csvParser.getHeaderMap();

      List<String> nonValueFields = Arrays.asList("Country Name", "Country Code", "Indicator Name", "Indicator Code", "");

      List<String> valueFields = headers.keySet().stream()
        .filter(header -> !nonValueFields.contains(header))
        .collect(Collectors.toList());

      for (CSVRecord csvRecord : csvParser) {
        String countryName = csvRecord.get("Country Name");
        String countryCode = csvRecord.get("Country Code");
        String indicatorName = csvRecord.get("Indicator Name");
        String indicatorCode = csvRecord.get("Indicator Code");

        for (String header : valueFields) {

          csvPrinter.printRecord(
            countryName, countryCode, indicatorName, indicatorCode, header, csvRecord.get(header)
          );
        }
      }

      reader.close();
      csvPrinter.flush();

      return csvPrinter.getOut().toString().getBytes();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public int pageSize() {
    return -1;
  }
}
