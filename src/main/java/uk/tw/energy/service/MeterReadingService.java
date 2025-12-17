package uk.tw.energy.service;

import java.io.PrintWriter;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

@Service
public class MeterReadingService {

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC);
    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    public MeterReadingService(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        meterAssociatedReadings
                .computeIfAbsent(smartMeterId, k -> new ArrayList<>())
                .addAll(electricityReadings);
    }

    public boolean writeReadingsAsCsv(String smartMeterId, PrintWriter writer) {
        List<ElectricityReading> readings = meterAssociatedReadings.get(smartMeterId);
        if (readings == null) {
            return false;
        }

        writer.println("Date,Amount");
        readings.stream()
                .map(reading -> formatter.format(reading.time()) + "," + reading.reading().toPlainString())
                .forEach(writer::println);

        return true;
    }
}
