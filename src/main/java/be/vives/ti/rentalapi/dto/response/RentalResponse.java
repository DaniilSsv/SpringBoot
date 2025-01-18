package be.vives.ti.rentalapi.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RentalResponse {
    private Integer id;
    private Integer carId;
    private BigDecimal rentalPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal deposit;
    private String pickupLocation;
    private String email;

    public RentalResponse(Integer id, Integer carId, BigDecimal rentalPrice, LocalDate startDate, LocalDate endDate, BigDecimal deposit, String pickupLocation, String email) {
        this.id = id;
        this.carId = carId;
        this.rentalPrice = rentalPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deposit = deposit;
        this.pickupLocation = pickupLocation;
        this.email = email;
    }
}
