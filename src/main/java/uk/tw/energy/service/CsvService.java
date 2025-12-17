package uk.tw.energy.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

@Service
public class CsvService {

    private static final String CSV_HEADER = "time,reading";

    public String generateCsv(List<ElectricityReading> readings) {
        return Stream.concat(
                Stream.of(CSV_HEADER),
                readings.stream().map(this::toCsvRow)
        ).collect(Collectors.joining("\n"));
    }

    private String toCsvRow(ElectricityReading reading) {
        return reading.time() + "," + reading.reading();
    }
}
