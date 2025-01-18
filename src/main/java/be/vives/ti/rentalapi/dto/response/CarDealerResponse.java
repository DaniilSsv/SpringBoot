package be.vives.ti.rentalapi.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CarDealerResponse {
    private Integer carId;
    private String brand;
    private String model;
    private Integer power;
    private Integer year;
    private String color;
    private String imageUri;
    private String description;

    private Integer dealerId;
    private String dealerName;
    private String dealerAddress;
    private String dealerCity;
    private String dealerEmail;
    private Integer dealerPostcode;
    private Integer dealerPhone;

    public CarDealerResponse(Integer carId, String brand, String model, Integer power, Integer year, String color, String imageUri, String description, Integer dealerId, String dealerName, String dealerAddress, String dealerCity, String dealerEmail, Integer dealerPostcode, Integer dealerPhone) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.power = power;
        this.year = year;
        this.color = color;
        this.imageUri = imageUri;
        this.description = description;
        this.dealerId = dealerId;
        this.dealerName = dealerName;
        this.dealerAddress = dealerAddress;
        this.dealerCity = dealerCity;
        this.dealerEmail = dealerEmail;
        this.dealerPostcode = dealerPostcode;
        this.dealerPhone = dealerPhone;
    }
}
