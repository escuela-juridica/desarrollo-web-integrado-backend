package pe.edu.utp.escuela.app.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseCommercialStatusService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final Clock clock;

    public CourseCommercialStatus calculate(CourseCommercialData course, long activeEnrollments) {
        if (activeEnrollments < 0) {
            throw new IllegalArgumentException("activeEnrollments no puede ser negativo");
        }
        LocalDate today = LocalDate.now(clock);
        Instant now = clock.instant();
        PriceResult price = calculatePrice(course, now);

        if (!isAdministrativelyOpen(course.courseStatus())) {
            return result(CommercialStatusCode.ENROLLMENT_CLOSED, "Matrícula cerrada",
                    false, course.startDate(), price);
        }
        if (usesEnrollmentClosingDate(course.modality())
                && course.enrollmentClosingDate() != null
                && today.isAfter(course.enrollmentClosingDate())) {
            return result(CommercialStatusCode.ENROLLMENT_CLOSED, "Matrícula cerrada",
                    false, course.startDate(), price);
        }
        if (course.capacity() != null && activeEnrollments >= course.capacity()) {
            return result(CommercialStatusCode.NO_CAPACITY, "Sin cupos",
                    false, course.startDate(), price);
        }
        if ("VIRTUAL".equals(course.modality()) && course.startDate() == null) {
            return result(CommercialStatusCode.IMMEDIATE_START, "Inicio inmediato",
                    true, null, price);
        }
        if (course.startDate() != null && course.startDate().isAfter(today)) {
            return result(CommercialStatusCode.UPCOMING, "Próximo inicio",
                    true, course.startDate(), price);
        }
        return result(CommercialStatusCode.IN_PROGRESS, "En progreso",
                true, course.startDate(), price);
    }

    private boolean isAdministrativelyOpen(String status) {
        return "PUBLICADO".equals(status) || "EN_CURSO".equals(status);
    }

    private boolean usesEnrollmentClosingDate(String modality) {
        return "EN_VIVO".equals(modality) || "HIBRIDO".equals(modality);
    }

    private PriceResult calculatePrice(CourseCommercialData course, Instant now) {
        if ("GRATUITO".equals(course.saleType())) {
            return new PriceResult(ZERO, course.regularPrice(), false, PurchaseAction.ACCESS_FREE);
        }
        boolean promotionActive = course.promotionalPrice() != null
                && (course.promotionStartsAt() == null || !now.isBefore(course.promotionStartsAt()))
                && (course.promotionEndsAt() == null || !now.isAfter(course.promotionEndsAt()));
        BigDecimal currentPrice = promotionActive
                ? course.promotionalPrice()
                : course.regularPrice();
        return new PriceResult(currentPrice, course.regularPrice(), promotionActive,
                PurchaseAction.PAY_NOW);
    }

    private CourseCommercialStatus result(
            CommercialStatusCode code,
            String label,
            boolean enrollmentAllowed,
            LocalDate startDate,
            PriceResult price) {
        return new CourseCommercialStatus(
                code, label, startDate, enrollmentAllowed, price.currentPrice(), price.regularPrice(),
                price.promotionActive(), enrollmentAllowed ? price.action() : PurchaseAction.NONE);
    }

    public record CourseCommercialData(
            String modality,
            String saleType,
            String courseStatus,
            BigDecimal regularPrice,
            BigDecimal promotionalPrice,
            Instant promotionStartsAt,
            Instant promotionEndsAt,
            LocalDate startDate,
            LocalDate enrollmentClosingDate,
            Integer capacity) {

        public CourseCommercialData {
            if (regularPrice == null || regularPrice.signum() < 0) {
                throw new IllegalArgumentException("regularPrice debe ser mayor o igual a cero");
            }
            if (promotionalPrice != null
                    && (promotionalPrice.signum() < 0 || promotionalPrice.compareTo(regularPrice) > 0)) {
                throw new IllegalArgumentException(
                        "promotionalPrice debe estar entre cero y regularPrice");
            }
            if (activeCapacityIsInvalid(capacity)) {
                throw new IllegalArgumentException("capacity debe ser mayor que cero cuando exista");
            }
        }

        private static boolean activeCapacityIsInvalid(Integer capacity) {
            return capacity != null && capacity <= 0;
        }
    }

    public record CourseCommercialStatus(
            CommercialStatusCode code,
            String label,
            LocalDate startDate,
            boolean enrollmentAllowed,
            BigDecimal currentPrice,
            BigDecimal regularPrice,
            boolean promotionActive,
            PurchaseAction action) {
    }

    public enum CommercialStatusCode {
        IMMEDIATE_START,
        UPCOMING,
        IN_PROGRESS,
        ENROLLMENT_CLOSED,
        NO_CAPACITY
    }

    public enum PurchaseAction {
        ACCESS_FREE,
        PAY_NOW,
        NONE
    }

    private record PriceResult(
            BigDecimal currentPrice,
            BigDecimal regularPrice,
            boolean promotionActive,
            PurchaseAction action) {
    }
}
