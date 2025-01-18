package be.vives.ti.rentalapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RentalRequest {

    @NotNull(message = "Car ID is required")
    private Integer carId;

    @NotNull(message = "Rental price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rental price must be greater than 0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal rentalPrice;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @NotNull(message = "Deposit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Deposit must be greater than 0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal deposit;

    @NotNull(message = "Pickup location is required")
    @Size(min = 1, max = 255, message = "Pickup location must be between 1 and 255 characters")
    private String pickupLocation;

    @NotNull(message = "Email is required")
    @Email(message = "Email has to be valid")
    private String email;
}
