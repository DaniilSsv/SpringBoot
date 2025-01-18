package be.vives.ti.rentalapi.service;

import be.vives.ti.rentalapi.dto.request.RentalRequest;
import be.vives.ti.rentalapi.dto.response.RentalResponse;
import be.vives.ti.rentalapi.model.Car;
import be.vives.ti.rentalapi.model.Popularity;
import be.vives.ti.rentalapi.model.Rental;
import be.vives.ti.rentalapi.repository.CarRepository;
import be.vives.ti.rentalapi.repository.PopularityRepository;
import be.vives.ti.rentalapi.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class RentalServiceTests {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private PopularityRepository popularityRepository;

    @InjectMocks
    private RentalService rentalService;

    private Rental rental;
    private Car car;
    private RentalRequest rentalRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        car = Car.builder()
                .id(1)
                .brand("Toyota")
                .model("Corolla")
                .build();

        rental = Rental.builder()
                .id(1)
                .car(car)
                .rentalPrice(new BigDecimal("100.0"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(4))
                .deposit(new BigDecimal("500.0"))
                .pickupLocation("Antwerp")
                .email("user@example.com")
                .build();

        rentalRequest = RentalRequest.builder()
                .carId(1)
                .rentalPrice(new BigDecimal("150.0"))
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(10))
                .deposit(new BigDecimal("300.0"))
                .pickupLocation("Ghent")
                .email("user2@example.com")
                .build();
    }

    @Test
    public void getAllRentalsShouldReturnListOfRentals() {
        when(rentalRepository.findAll()).thenReturn(Arrays.asList(rental));

        ResponseEntity<List<RentalResponse>> response = rentalService.getAllRentals();

        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().getFirst().getCarId()).isEqualTo(1);
        verify(rentalRepository, times(1)).findAll();
    }

    @Test
    public void getAllRentalsShouldReturnEmptyListIfNoRentals() {
        when(rentalRepository.findAll()).thenReturn(List.of());

        ResponseEntity<List<RentalResponse>> response = rentalService.getAllRentals();

        assertThat(response.getBody()).isEmpty();
        verify(rentalRepository, times(1)).findAll();
    }

    @Test
    public void getRentalByIdShouldReturnRentalIfExists() {
        when(rentalRepository.findById(1)).thenReturn(Optional.of(rental));

        ResponseEntity<RentalResponse> response = rentalService.getRentalById(1);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCarId()).isEqualTo(1);
        verify(rentalRepository, times(1)).findById(1);
    }

    @Test
    public void getRentalByIdShouldReturnNotFoundIfRentalDoesNotExist() {
        when(rentalRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<RentalResponse> response = rentalService.getRentalById(99);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(rentalRepository, times(1)).findById(99);
    }

    @Test
    public void createRentalShouldCreateAndReturnRentalWhenDatesAreCorrect() {
        when(carRepository.findById(1)).thenReturn(Optional.of(car));
        when(rentalRepository.existsByCarIdAndDateRange(1, rentalRequest.getStartDate(), rentalRequest.getEndDate())).thenReturn(false);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(popularityRepository.findByCarId(1)).thenReturn(Optional.empty());

        ResponseEntity<RentalResponse> response = rentalService.createRental(rentalRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCarId()).isEqualTo(1);
        verify(carRepository, times(1)).findById(1);
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(popularityRepository, times(1)).save(any(Popularity.class));
    }

    @Test
    public void createRentalShouldReturnConflictIfCarIsAlreadyRentedInDateRange() {
        when(carRepository.findById(1)).thenReturn(Optional.of(car));
        when(rentalRepository.existsByCarIdAndDateRange(1, rentalRequest.getStartDate(), rentalRequest.getEndDate())).thenReturn(true);

        ResponseEntity<RentalResponse> response = rentalService.createRental(rentalRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        verify(carRepository, times(1)).findById(1);
        verify(rentalRepository, times(1)).existsByCarIdAndDateRange(1, rentalRequest.getStartDate(), rentalRequest.getEndDate());
    }

    @Test
    public void createRentalShouldReturnNotFoundIfCarDoesNotExist() {
        when(carRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<RentalResponse> response = rentalService.createRental(rentalRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(carRepository, times(1)).findById(1);
    }

    @Test
    public void updateRentalShouldReturnNotFoundIfRentalDoesNotExist() {
        when(rentalRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<RentalResponse> response = rentalService.updateRental(99, rentalRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(rentalRepository, times(1)).findById(99);
    }

    @Test
    public void updateRentalShouldReturnNotFoundIfCarDoesNotExist() {
        when(rentalRepository.findById(1)).thenReturn(Optional.of(rental));
        when(carRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<RentalResponse> response = rentalService.updateRental(1, rentalRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(rentalRepository, times(1)).findById(1);
        verify(carRepository, times(1)).findById(1);
    }

    @Test
    public void updateRentalShouldReturnConflictIfCarIsAlreadyRentedInDateRange() {
        when(rentalRepository.findById(1)).thenReturn(Optional.of(rental));
        when(carRepository.findById(1)).thenReturn(Optional.of(car));
        when(rentalRepository.existsByCarIdAndDateRange(1, rentalRequest.getStartDate(), rentalRequest.getEndDate())).thenReturn(true);

        ResponseEntity<RentalResponse> response = rentalService.updateRental(1, rentalRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        verify(rentalRepository, times(1)).findById(1);
        verify(carRepository, times(1)).findById(1);
        verify(rentalRepository, times(1)).existsByCarIdAndDateRange(1, rentalRequest.getStartDate(), rentalRequest.getEndDate());
    }

    @Test
    public void updateRentalShouldUpdateAndReturnRentalWhenDatesAreCorrect() {
        when(rentalRepository.findById(1)).thenReturn(Optional.of(rental));
        when(carRepository.findById(1)).thenReturn(Optional.of(car));
        when(rentalRepository.existsByCarIdAndDateRange(1, rentalRequest.getStartDate(), rentalRequest.getEndDate())).thenReturn(false);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        ResponseEntity<RentalResponse> response = rentalService.updateRental(1, rentalRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCarId()).isEqualTo(1);
        verify(rentalRepository, times(1)).findById(1);
        verify(carRepository, times(1)).findById(1);
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    public void deleteRentalShouldRemoveRentalIfExists() {
        when(rentalRepository.existsById(1)).thenReturn(true);

        ResponseEntity<Void> response = rentalService.deleteRental(1);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(rentalRepository, times(1)).existsById(1);
        verify(rentalRepository, times(1)).deleteById(1);
    }

    @Test
    public void deleteRentalShouldReturnNotFoundIfRentalDoesNotExist() {
        when(rentalRepository.existsById(99)).thenReturn(false);

        ResponseEntity<Void> response = rentalService.deleteRental(99);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(rentalRepository, times(1)).existsById(99);
    }
}