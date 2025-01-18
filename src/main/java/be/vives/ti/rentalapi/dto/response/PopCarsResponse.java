package be.vives.ti.rentalapi.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PopCarsResponse {

    private Integer id;
    private String brand;
    private String model;
    private Integer power;
    private Integer year;
    private String color;
    private String imageUri;
    private String description;
    private Integer likes;

    public PopCarsResponse(Integer id, String brand, String model, Integer power, Integer year, String color, String imageUri, String description, Integer likes) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.power = power;
        this.year = year;
        this.color = color;
        this.imageUri = imageUri;
        this.description = description;
        this.likes = likes;
    }
}
