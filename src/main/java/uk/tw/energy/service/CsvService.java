package uk.tw.energy.service;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

@Service
public class CsvService {
    private static final String CSV_HEADER = "Time,Reading\n";

    public String readingsToCsv(List<ElectricityReading> readings) {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append(CSV_HEADER);

        for (ElectricityReading reading : readings) {
            csvBuilder.append(reading.time()).append(",").append(reading.reading()).append("\n");
        }

        return csvBuilder.toString();
    }
}
