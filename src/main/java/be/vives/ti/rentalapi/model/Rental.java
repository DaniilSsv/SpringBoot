package be.vives.ti.rentalapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "Rental")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @NotNull
    @DecimalMin("0.0")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false)
    private BigDecimal rentalPrice;

    @NotNull
    @FutureOrPresent
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull
    @Future
    @Column(nullable = false)
    private LocalDate endDate;

    @NotNull
    @DecimalMin("0.0")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false)
    private BigDecimal deposit;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String pickupLocation;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String email;

    @Override
    public String toString() {
        return "Rental {" +
                "   id = " + id +
                "   car = " + car +
                "   rentalPrice = " + rentalPrice +
                "   startDate = " + startDate +
                "   endDate = " + endDate +
                "   deposit = " + deposit +
                "   email = " + email +
                "   pickupLocation = '" + pickupLocation + '\'' +
                '}';
    }
}