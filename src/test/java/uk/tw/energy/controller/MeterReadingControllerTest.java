package uk.tw.energy.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
import java.time.Instant;
import uk.tw.energy.builders.MeterReadingsBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.CsvService;
import uk.tw.energy.service.MeterReadingService;

public class MeterReadingControllerTest {

    private static final String SMART_METER_ID = "10101010";
    private MeterReadingController meterReadingController;
    private MeterReadingService meterReadingService;
    private CsvService csvService;

    @BeforeEach
    public void setUp() {
        this.meterReadingService = new MeterReadingService(new HashMap<>());
        this.csvService = new CsvService();
        this.meterReadingController = new MeterReadingController(meterReadingService, csvService);
    }

    @Test
    public void givenNoMeterIdIsSuppliedWhenStoringShouldReturnErrorResponse() {
        MeterReadings meterReadings = new MeterReadings(null, Collections.emptyList());
        assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenEmptyMeterReadingShouldReturnErrorResponse() {
        MeterReadings meterReadings = new MeterReadings(SMART_METER_ID, Collections.emptyList());
        assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenNullReadingsAreSuppliedWhenStoringShouldReturnErrorResponse() {
        MeterReadings meterReadings = new MeterReadings(SMART_METER_ID, null);
        assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenMultipleBatchesOfMeterReadingsShouldStore() {
        MeterReadings meterReadings = new MeterReadingsBuilder()
                .setSmartMeterId(SMART_METER_ID)
                .generateElectricityReadings()
                .build();

        MeterReadings otherMeterReadings = new MeterReadingsBuilder()
                .setSmartMeterId(SMART_METER_ID)
                .generateElectricityReadings()
                .build();

        meterReadingController.storeReadings(meterReadings);
        meterReadingController.storeReadings(otherMeterReadings);

        List<ElectricityReading> expectedElectricityReadings = new ArrayList<>();
        expectedElectricityReadings.addAll(meterReadings.electricityReadings());
        expectedElectricityReadings.addAll(otherMeterReadings.electricityReadings());

        assertThat(meterReadingService.getReadings(SMART_METER_ID).get()).isEqualTo(expectedElectricityReadings);
    }

    @Test
    public void givenMeterReadingsAssociatedWithTheUserShouldStoreAssociatedWithUser() {
        MeterReadings meterReadings = new MeterReadingsBuilder()
                .setSmartMeterId(SMART_METER_ID)
                .generateElectricityReadings()
                .build();

        MeterReadings otherMeterReadings = new MeterReadingsBuilder()
                .setSmartMeterId("00001")
                .generateElectricityReadings()
                .build();

        meterReadingController.storeReadings(meterReadings);
        meterReadingController.storeReadings(otherMeterReadings);

        assertThat(meterReadingService.getReadings(SMART_METER_ID).get())
                .isEqualTo(meterReadings.electricityReadings());
    }

    @Test
    public void givenMeterIdThatIsNotRecognisedShouldReturnNotFound() {
        assertThat(meterReadingController.readReadings(SMART_METER_ID).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void givenMeterIdThatHasReadingsShouldReturnCsv() {
        // Arrange
        Instant now = Instant.now();
        List<ElectricityReading> readings = List.of(
                new ElectricityReading(now.minusSeconds(3600), BigDecimal.valueOf(10.0)),
                new ElectricityReading(now, BigDecimal.valueOf(20.0))
        );
        meterReadingService.storeReadings(SMART_METER_ID, readings);

        // Act
        var response = meterReadingController.exportReadings(SMART_METER_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().toString()).isEqualTo("text/csv");
        assertThat(response.getHeaders().getContentDisposition().toString()).isEqualTo("attachment; filename=\"10101010-readings.csv\"");

        String expectedCsv = "time,reading\n" +
                now.minusSeconds(3600) + ",10.0\n" +
                now + ",20.0";
        assertThat(response.getBody()).isEqualTo(expectedCsv);
    }
}
