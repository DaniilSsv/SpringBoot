package be.vives.ti.rentalapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "popularity")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Popularity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    private Car car;

    @NotNull
    @Min(0)
    private Integer likes;

    @Override
    public String toString() {
        return "Popularity {" +
                "   id = " + id +
                "   car = " + car +
                "   likes = " + likes +
                '}';
    }
}
