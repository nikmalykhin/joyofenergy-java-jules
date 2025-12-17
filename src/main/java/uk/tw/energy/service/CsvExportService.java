package uk.tw.energy.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

@Service
public class CsvExportService {

    private static final String[] HEADERS = {"Time", "Reading"};

    public String export(List<ElectricityReading> electricityReadings) throws IOException {
        StringWriter stringWriter = new StringWriter();
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader(HEADERS)
            .build();
        try (CSVPrinter csvPrinter = new CSVPrinter(stringWriter, csvFormat)) {
            for (ElectricityReading reading : electricityReadings) {
                csvPrinter.printRecord(reading.time(), reading.reading());
            }
        }
        return stringWriter.toString();
    }
}
