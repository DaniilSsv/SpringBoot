package be.vives.ti.rentalapi.service;

import be.vives.ti.rentalapi.dto.request.CarRequest;
import be.vives.ti.rentalapi.dto.response.CarDealerResponse;
import be.vives.ti.rentalapi.dto.response.CarResponse;
import be.vives.ti.rentalapi.dto.response.PopCarsResponse;
import be.vives.ti.rentalapi.model.Car;
import be.vives.ti.rentalapi.model.Dealer;
import be.vives.ti.rentalapi.model.Popularity;
import be.vives.ti.rentalapi.repository.CarRepository;
import be.vives.ti.rentalapi.repository.DealerRepository;
import be.vives.ti.rentalapi.repository.PopularityRepository;
import be.vives.ti.rentalapi.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class CarServiceTests {

    @Mock
    private CarRepository carRepository;

    @Mock
    private PopularityRepository popularityRepository;

    @Mock
    private DealerRepository dealerRepository;

    @Mock
    private RentalRepository rentalRepository;

    @InjectMocks
    private CarService carService;

    private Dealer dealer;
    private Car car;
    private Popularity popularity;
    private CarRequest carRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dealer = Dealer.builder()
                .id(1)
                .name("AutoWorld")
                .address("123 Main Street")
                .city("Antwerp")
                .email("contact@autoworld.com")
                .postcode(2000)
                .phone(12345678)
                .build();

        car = Car.builder()
                .id(1)
                .brand("Toyota")
                .model("Corolla")
                .power(150)
                .year(2020)
                .color("Red")
                .imageUri("http://example.com/car.jpg")
                .description("A reliable car")
                .dealer(dealer)
                .build();

        carRequest = CarRequest.builder()
                .brand("Honda")
                .model("Civic")
                .power(180)
                .year(2021)
                .color("Blue")
                .imageUri("http://example.com/car.jpg")
                .description("Sporty and efficient")
                .dealerId(1)
                .build();

        popularity = new Popularity();
        popularity.setCar(car);
        popularity.setLikes(100);
    }

    @Test
    public void getAllCarsShouldReturnListOfCars() {
        when(carRepository.findAll()).thenReturn(Arrays.asList(car));

        ResponseEntity<List<CarResponse>> response = carService.getAllCars();

        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getBrand()).isEqualTo("Toyota");
        verify(carRepository, times(1)).findAll();
    }

    @Test
    public void getAllCarsShouldReturnEmptyListIfNoCars() {
        when(carRepository.findAll()).thenReturn(Arrays.asList());

        ResponseEntity<List<CarResponse>> response = carService.getAllCars();

        assertThat(response.getBody()).isEmpty();
        verify(carRepository, times(1)).findAll();
    }

    @Test
    public void getCarByIdShouldReturnCarIfExists() {
        when(carRepository.findById(1)).thenReturn(Optional.of(car));
        when(popularityRepository.findByCarId(1)).thenReturn(Optional.of(popularity));

        ResponseEntity<CarResponse> response = carService.getCarById(1);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBrand()).isEqualTo("Toyota");
        verify(carRepository, times(1)).findById(1);
        verify(popularityRepository, times(1)).findByCarId(1);
    }

    @Test
    public void getCarByIdShouldReturnNotFoundIfCarDoesNotExist() {
        when(carRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<CarResponse> response = carService.getCarById(99);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(carRepository, times(1)).findById(99);
    }

    @Test
    public void getCarWithDealerShouldReturnCarAndDealerInfo() {
        when(carRepository.findById(1)).thenReturn(Optional.of(car));

        ResponseEntity<CarDealerResponse> response = carService.getCarWithDealer(1);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDealerName()).isEqualTo("AutoWorld");
        verify(carRepository, times(1)).findById(1);
    }

    @Test
    public void getCarWithDealerShouldReturnNotFoundIfCarDoesNotExist() {
        when(carRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<CarDealerResponse> response = carService.getCarWithDealer(99);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(carRepository, times(1)).findById(99);
    }

    @Test
    public void getCarWithDealerShouldReturnNotFoundIfDealerDoesNotExist() {
        when(carRepository.findById(1)).thenReturn(Optional.of(car));
        when(dealerRepository.findById(2)).thenReturn(Optional.of(dealer));

        ResponseEntity<CarDealerResponse> response = carService.getCarWithDealer(99);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(carRepository, times(1)).findById(99);
    }

    @Test
    public void getTop4CarsShouldReturnListOfTop4Cars() {
        when(popularityRepository.findAll()).thenReturn(Arrays.asList(popularity));

        ResponseEntity<List<PopCarsResponse>> response = carService.getTop4Cars();

        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getBrand()).isEqualTo("Toyota");
        verify(popularityRepository, times(1)).findAll();
    }

    @Test
    public void getTop4CarsShouldReturnNotFoundIfNoPopularityData() {
        when(popularityRepository.findAll()).thenReturn(Arrays.asList());

        ResponseEntity<List<PopCarsResponse>> response = carService.getTop4Cars();

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(popularityRepository, times(1)).findAll();
    }

    @Test
    public void createCarShouldSaveAndReturn () {
        when(dealerRepository.findById(1)).thenReturn(Optional.of(dealer));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        ResponseEntity<CarResponse> response = carService.createCar(carRequest);

        verify(dealerRepository, times(1)).findById(1);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(popularityRepository, times(1)).save(any(Popularity.class));

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBrand()).isEqualTo("Toyota");
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    public void createCarShouldReturnNotFoundIfDealerDoesNotExist() {
        ResponseEntity<CarResponse> response = carService.createCar(carRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void createCarShouldCreatePopularity() {
        when(dealerRepository.findById(1)).thenReturn(Optional.of(dealer));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(popularityRepository.save(any(Popularity.class))).thenReturn(popularity);

        ResponseEntity<CarResponse> response = carService.createCar(carRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(popularityRepository, times(1)).save(any(Popularity.class));
    }

    @Test
    public void updateCarShouldReturnUpdatedCar() {
        CarRequest updatedCar = CarRequest.builder()
                .brand(car.getBrand())
                .model(car.getModel())
                .power(car.getPower())
                .year(car.getYear())
                .color("Black") // Updated color
                .imageUri(car.getImageUri())
                .description(car.getDescription())
                .dealerId(car.getDealer().getId())
                .build();

        when(carRepository.findById(1)).thenReturn(Optional.of(car));
        when(dealerRepository.findById(1)).thenReturn(Optional.of(dealer));
        when(carRepository.save(car)).thenReturn(car);

        ResponseEntity<CarResponse> response = carService.updateCar(1, updatedCar);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getColor()).isEqualTo("Black");
        verify(carRepository, times(1)).findById(1);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void updateCarShouldReturnNotFoundIfCarDoesNotExist() {
        when(carRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<CarResponse> response = carService.updateCar(99, carRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(carRepository, times(1)).findById(99);
    }

    @Test
    public void updateCarShouldReturnNotFoundIfDealerDoesNotExist() {
        CarRequest carWithInvalidDealer = CarRequest.builder()
                .brand(car.getBrand())
                .model(car.getModel())
                .power(car.getPower())
                .year(car.getYear())
                .color("Black") // Updated color
                .imageUri(car.getImageUri())
                .description(car.getDescription())
                .dealerId(car.getDealer().getId())
                .build();

        when(carRepository.findById(1)).thenReturn(Optional.of(car));

        ResponseEntity<CarResponse> response = carService.updateCar(1, carWithInvalidDealer);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(carRepository, times(1)).findById(1);
    }

    @Test
    public void deleteCarShouldRemoveAllRentalsThatAreLinked() {
        when(carRepository.findById(1)).thenReturn(Optional.of(car));

        ResponseEntity<Void> response = carService.deleteCar(1);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(carRepository, times(1)).delete(car);
    }

    @Test
    public void deleteCarShouldReturnNotFoundIfCarDoesNotExist() {
        when(carRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = carService.deleteCar(99);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(carRepository, times(1)).findById(99);
    }
}
