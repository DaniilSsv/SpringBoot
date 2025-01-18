package be.vives.ti.rentalapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CarRequest {

    @NotNull(message = "Brand is required")
    @Size(min = 1, max = 255, message = "Brand must be between 1 and 255 characters")
    private String brand;

    @NotNull(message = "Model is required")
    @Size(min = 1, max = 255, message = "Model must be between 1 and 255 characters")
    private String model;

    @NotNull(message = "Power is required")
    @Min(value = 1, message = "Power must be greater than 0")
    private Integer power;

    @NotNull(message = "Year is required")
    @Min(value = 1886, message = "Year must be at least 1886")
    private Integer year;

    @NotNull(message = "Color is required")
    @Size(min = 1, max = 255, message = "Color must be between 1 and 255 characters")
    private String color;

    @NotNull(message = "Image URI is required")
    @Size(min = 1, max = 2048, message = "Image URI must be between 1 and 2048 characters")
    private String imageUri;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Dealer ID is required")
    private Integer dealerId;
}
