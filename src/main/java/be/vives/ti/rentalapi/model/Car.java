package be.vives.ti.rentalapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "car")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String brand;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String model;

    @NotNull
    @Column(nullable = false)
    private Integer power;

    @NotNull
    @Min(1886)
    @Column(nullable = false, name = "\"year\"")
    private Integer year;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String color;

    @NotNull
    @Size(min = 1, max = 2048) // max length for URIs
    @Column(nullable = false)
    private String imageUri;

    @Size(max = 3000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @PrePersist
    @PreUpdate
    private void validateYear() {
        int currentYear = LocalDate.now().getYear();
        if (this.year > currentYear) {
            throw new IllegalArgumentException("Year cannot be greater than the current year: " + currentYear);
        }
    }

    @Override
    public String toString() {
        return "Car {" +
                "   id = " + id +
                "   brand = '" + brand + '\'' +
                "   model = '" + model + '\'' +
                "   power = " + power + '\'' +
                "   year = " + year + '\'' +
                "   color = '" + color + '\'' +
                "   imageUri = '" + imageUri + '\'' +
                "   description = '" + description + '\'' +
                "   dealer = " + dealer +
                '}';
    }
}
