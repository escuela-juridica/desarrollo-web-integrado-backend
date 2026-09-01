package pe.edu.utp.escuela.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.utp.escuela.app.service.CourseCommercialStatusService.CommercialStatusCode;
import pe.edu.utp.escuela.app.service.CourseCommercialStatusService.CourseCommercialData;
import pe.edu.utp.escuela.app.service.CourseCommercialStatusService.PurchaseAction;

class CourseCommercialStatusServiceTests {

    private CourseCommercialStatusService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-31T15:00:00Z"), ZoneId.of("America/Lima"));
        service = new CourseCommercialStatusService(clock);
    }

    @Test
    void virtualCourseWithoutDateStartsImmediately() {
        var result = service.calculate(course("VIRTUAL", "PAGADO", "PUBLICADO",
                null, null, null), 0);

        assertEquals(CommercialStatusCode.IMMEDIATE_START, result.code());
        assertTrue(result.enrollmentAllowed());
        assertEquals(PurchaseAction.PAY_NOW, result.action());
    }

    @Test
    void onlyActiveEnrollmentsOccupyConfiguredCapacity() {
        var result = service.calculate(course("HIBRIDO", "PAGADO", "PUBLICADO",
                LocalDate.of(2026, 9, 15), null, 2), 2);

        assertEquals(CommercialStatusCode.NO_CAPACITY, result.code());
        assertFalse(result.enrollmentAllowed());
        assertEquals(PurchaseAction.NONE, result.action());
    }

    @Test
    void virtualCourseIgnoresEnrollmentClosingDate() {
        var result = service.calculate(course("VIRTUAL", "GRATUITO", "PUBLICADO",
                null, LocalDate.of(2026, 8, 1), null), 0);

        assertEquals(CommercialStatusCode.IMMEDIATE_START, result.code());
        assertEquals(BigDecimal.ZERO, result.currentPrice());
        assertEquals(PurchaseAction.ACCESS_FREE, result.action());
    }

    @Test
    void liveCourseClosesAfterCommercialClosingDate() {
        var result = service.calculate(course("EN_VIVO", "PAGADO", "PUBLICADO",
                LocalDate.of(2026, 9, 15), LocalDate.of(2026, 8, 30), null), 0);

        assertEquals(CommercialStatusCode.ENROLLMENT_CLOSED, result.code());
        assertFalse(result.enrollmentAllowed());
    }

    private CourseCommercialData course(
            String modality,
            String saleType,
            String status,
            LocalDate startDate,
            LocalDate closingDate,
            Integer capacity) {
        return new CourseCommercialData(
                modality,
                saleType,
                status,
                new BigDecimal("200.00"),
                new BigDecimal("150.00"),
                Instant.parse("2026-08-01T05:00:00Z"),
                Instant.parse("2026-09-01T04:59:59Z"),
                startDate,
                closingDate,
                capacity);
    }
}
