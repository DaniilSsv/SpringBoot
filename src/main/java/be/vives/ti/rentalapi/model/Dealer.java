package be.vives.ti.rentalapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "dealer")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String address;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false)
    private String city;

    @NotNull
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Min(0)
    @Max(9999)
    @Column(nullable = false)
    private Integer postcode;

    @Min(10000000) // min 8 digits
    @Max(999999999) // max 9 digits
    private Integer phone;

    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars;

    @Override
    public String toString() {
        return "Dealer {" +
                "   id = " + id +
                "   name = '" + name + '\'' +
                "   address = '" + address + '\'' +
                "   city = '" + city + '\'' +
                "   email = '" + email + '\'' +
                "   postcode = " + postcode +
                "   phone = " + phone +
                '}';
    }
}
