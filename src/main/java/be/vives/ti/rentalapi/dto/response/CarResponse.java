// CarResponse.java
package be.vives.ti.rentalapi.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CarResponse {

    private Integer id;
    private String brand;
    private String model;
    private Integer power;
    private Integer year;
    private String color;
    private String imageUri;
    private String description;

    public CarResponse(Integer id, String brand, String model, Integer power, Integer year, String color, String imageUri, String description) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.power = power;
        this.year = year;
        this.color = color;
        this.imageUri = imageUri;
        this.description = description;
    }
}
