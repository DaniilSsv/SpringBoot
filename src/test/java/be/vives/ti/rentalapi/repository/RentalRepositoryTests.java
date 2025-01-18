package be.vives.ti.rentalapi.repository;

import be.vives.ti.rentalapi.model.Dealer;
import be.vives.ti.rentalapi.model.Rental;
import be.vives.ti.rentalapi.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RentalRepositoryTests {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private DealerRepository dealerRepository;

    private Car testCar;
    private Dealer testDealer;

    @BeforeEach
    public void setUp() {
        testDealer = dealerRepository.save(Dealer.builder()
                .id(1)
                .name("AutoWorld")
                .address("123 Main Street")
                .city("Antwerp")
                .email("contact@autoworld.com")
                .postcode(2000)
                .phone(12345678)
                .build()
        );

        testCar = carRepository.save(
                Car.builder()
                        .id(1)
                        .brand("Toyota")
                        .model("Corolla")
                        .power(200)
                        .year(2005)
                        .color("Blue")
                        .imageUri("/")
                        .description("Description")
                        .dealer(testDealer)
                        .build()
        );
    }

    @Test
    public void createRental_ShouldReturn409Conflict_WhenStartDatesOverlapWithExistingRental() {
        // Create the first rental using the provided syntax
        rentalRepository.save(
                Rental.builder()
                        .car(testCar)
                        .rentalPrice(new BigDecimal("100.0"))
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(3))
                        .deposit(new BigDecimal("500.0"))
                        .pickupLocation("Antwerp")
                        .email("user1@example.com")
                        .build()
        );

        assertThat(rentalRepository.findAll()).hasSize(1);

        // testen de datums om te weten of die bruikbaar zijn zonder een nieuwe object te saven.
        boolean overlapteDatums = rentalRepository.existsByCarIdAndDateRange(
                testCar.getId(),
                LocalDate.now().plusDays(3),  // Overlapping start date
                LocalDate.now().plusDays(7)  // none overlapping end date
        );

        assertThat(overlapteDatums).isTrue();
    }

    @Test
    public void createRental_ShouldReturn409Conflict_WhenEndDatesOverlapWithExistingRental() {
        // Create the first rental using the provided syntax
        rentalRepository.save(
                Rental.builder()
                        .car(testCar)
                        .rentalPrice(new BigDecimal("100.0"))
                        .startDate(LocalDate.now().plusDays(4))
                        .endDate(LocalDate.now().plusDays(8))
                        .deposit(new BigDecimal("500.0"))
                        .pickupLocation("Antwerp")
                        .email("user1@example.com")
                        .build()
        );

        // testen de datums om te weten of die bruikbaar zijn zonder een nieuwe object te saven.
        boolean overlapteDatums = rentalRepository.existsByCarIdAndDateRange(
                testCar.getId(),
                LocalDate.now().plusDays(1),  // none overlapping start date
                LocalDate.now().plusDays(4)  // overlapping end date
        );

        assertThat(overlapteDatums).isTrue();
    }

    @Test
    public void createRental_ShouldReturn409Conflict_WhenBothDatesOverlapWithExistingRental() {
        // Create the first rental using the provided syntax
        rentalRepository.save(
                Rental.builder()
                        .car(testCar)
                        .rentalPrice(new BigDecimal("100.0"))
                        .startDate(LocalDate.now().plusDays(4))
                        .endDate(LocalDate.now().plusDays(8))
                        .deposit(new BigDecimal("500.0"))
                        .pickupLocation("Antwerp")
                        .email("user1@example.com")
                        .build()
        );

        // testen de datums om te weten of die bruikbaar zijn zonder een nieuwe object te saven.
        boolean overlapteDatums = rentalRepository.existsByCarIdAndDateRange(
                testCar.getId(),
                LocalDate.now().plusDays(4),  // none overlapping start date
                LocalDate.now().plusDays(8)  // overlapping end date
        );

        assertThat(overlapteDatums).isTrue();
    }

    @Test
    public void createRental_ShouldReturnFalseInCaseCarIdIsWrong() {
        // Create the first rental using the provided syntax
        rentalRepository.save(
                Rental.builder()
                        .car(testCar)
                        .rentalPrice(new BigDecimal("100.0"))
                        .startDate(LocalDate.now().plusDays(4))
                        .endDate(LocalDate.now().plusDays(8))
                        .deposit(new BigDecimal("500.0"))
                        .pickupLocation("Antwerp")
                        .email("user1@example.com")
                        .build()
        );

        // testen de datums om te weten of die bruikbaar zijn zonder een nieuwe object te saven.
        boolean overlapteDatums = rentalRepository.existsByCarIdAndDateRange(
                50,
                LocalDate.now().plusDays(4),  // none overlapping start date
                LocalDate.now().plusDays(8)  // overlapping end date
        );

        assertThat(overlapteDatums).isFalse();
    }

    @Test
    public void createRental_ShouldReturn201Created_WhenStartDatesDontOverlapWithExistingRental() {
        // Create the first rental using the provided syntax
        rentalRepository.save(
                Rental.builder()
                        .car(testCar)
                        .rentalPrice(new BigDecimal("100.0"))
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(3))
                        .deposit(new BigDecimal("500.0"))
                        .pickupLocation("Antwerp")
                        .email("user1@example.com")
                        .build()
        );

        assertThat(rentalRepository.findAll()).hasSize(1);

        // testen de datums om te weten of die bruikbaar zijn zonder een nieuwe object te saven.
        boolean overlapteDatums = rentalRepository.existsByCarIdAndDateRange(
                testCar.getId(),
                LocalDate.now().plusDays(4),  // none overlapping start date
                LocalDate.now().plusDays(7)  // none overlapping end date
        );

        assertThat(overlapteDatums).isFalse();
    }

    @Test
    public void deleteByIdExisting() {
        Rental rental = rentalRepository.save(
                Rental.builder()
                        .car(testCar)
                        .rentalPrice(new BigDecimal("100.0"))
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(5))
                        .deposit(new BigDecimal("500.0"))
                        .pickupLocation("Antwerp")
                        .email("user@example.com")
                        .build()
        );

        rentalRepository.deleteById(rental.getId());

        assertThat(rentalRepository.findById(rental.getId())).isEmpty();
    }

    @Test
    public void deleteByIdNotExisting() {
        int nonExistentId = 999;

        rentalRepository.deleteById(nonExistentId);

        assertThat(rentalRepository.findById(nonExistentId)).isEmpty();
        assertThat(rentalRepository.findAll()).isEmpty();
    }
}