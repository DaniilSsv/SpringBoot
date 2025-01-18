package be.vives.ti.rentalapi.repository;

import be.vives.ti.rentalapi.model.Dealer;
import be.vives.ti.rentalapi.model.Popularity;
import be.vives.ti.rentalapi.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PopularityRepositoryTests {

    @Autowired
    private PopularityRepository popularityRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private DealerRepository dealerRepository;

    private Car testCar;

    @BeforeEach
    public void setUp() {
        // Create and save a dealer
        Dealer testDealer = dealerRepository.save(Dealer.builder()
                .id(1)
                .name("AutoWorld")
                .address("123 Main Street")
                .city("Antwerp")
                .email("contact@autoworld.com")
                .postcode(2000)
                .phone(12345678)
                .build());

        // Create and save a car
        testCar = carRepository.save(Car.builder()
                .id(1)
                .brand("Toyota")
                .model("Corolla")
                .power(200)
                .year(2020)
                .color("Blue")
                .imageUri("/images/toyota-corolla.png")
                .description("A reliable car.")
                .dealer(testDealer)
                .build());
    }

    @Test
    public void findByCarId_ShouldReturnPopularity_WhenPopularityExists() {
        // Arrange: Create and save a Popularity entity for the car
        Popularity popularity = popularityRepository.save(Popularity.builder()
                .car(testCar)
                .likes(10)
                .build());

        // Act: Query for Popularity by car ID
        Optional<Popularity> result = popularityRepository.findByCarId(testCar.getId());

        // Assert: Ensure the result is present and matches the saved entity
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(popularity.getId());
        assertThat(result.get().getLikes()).isEqualTo(10);
    }

    @Test
    public void findByCarId_ShouldReturnEmpty_WhenPopularityOrCarDoesNotExist() {
        // Act: Query for a non-existent Popularity or CarID
        Optional<Popularity> result = popularityRepository.findByCarId(999);

        // Assert: Ensure the result is empty
        assertThat(result).isEmpty();
    }

    @Test
    public void deleteByCarId_ShouldDeletePopularity_WhenCarIdMatches() {
        // Arrange: Create and save a Popularity entity for the car
        popularityRepository.save(Popularity.builder()
                .car(testCar)
                .likes(10)
                .build());

        // Act: Delete Popularity by car ID
        popularityRepository.deleteByCarId(testCar.getId());

        // Assert: Ensure Popularity no longer exists in the database
        Optional<Popularity> result = popularityRepository.findByCarId(testCar.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void deleteByCarId_ShouldDoNothing_WhenCarIdDoesNotExist() {
        // Act: Attempt to delete a non-existent Popularity
        popularityRepository.deleteByCarId(999);

        // Assert: Database remains unaffected
        long count = popularityRepository.count();
        assertThat(count).isEqualTo(0);
    }
}
