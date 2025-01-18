package be.vives.ti.rentalapi.service;

import be.vives.ti.rentalapi.dto.request.RentalRequest;
import be.vives.ti.rentalapi.dto.response.RentalResponse;
import be.vives.ti.rentalapi.model.Car;
import be.vives.ti.rentalapi.model.Popularity;
import be.vives.ti.rentalapi.model.Rental;
import be.vives.ti.rentalapi.repository.CarRepository;
import be.vives.ti.rentalapi.repository.PopularityRepository;
import be.vives.ti.rentalapi.repository.RentalRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RentalService {
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private PopularityRepository popularityRepository;

    public ResponseEntity<List<RentalResponse>> getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        if (rentals.isEmpty()) {
            return ResponseEntity.ok().body(new ArrayList<>()); // 200
        }
        List<RentalResponse> rentalList = rentals.stream()
                .map(rental ->
                        RentalResponse.builder()
                                .id(rental.getId())
                                .rentalPrice(rental.getRentalPrice())
                                .carId(rental.getCar().getId())
                                .deposit(rental.getDeposit())
                                .pickupLocation(rental.getPickupLocation())
                                .startDate(rental.getStartDate())
                                .endDate(rental.getEndDate())
                                .email(rental.getEmail())
                                .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(rentalList);
    }

    public ResponseEntity<RentalResponse> getRentalById(@PathVariable Integer id) {
        Optional<Rental> rental = rentalRepository.findById(id);
        if (rental.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        Rental rentalEntity = rental.get();
        RentalResponse rentalResponse = RentalResponse.builder()
                .id(rentalEntity.getId())
                .rentalPrice(rentalEntity.getRentalPrice())
                .carId(rentalEntity.getCar().getId())
                .deposit(rentalEntity.getDeposit())
                .pickupLocation(rentalEntity.getPickupLocation())
                .startDate(rentalEntity.getStartDate())
                .endDate(rentalEntity.getEndDate())
                .email(rentalEntity.getEmail())
                .build();
        return ResponseEntity.ok(rentalResponse); // 200
    }

    public ResponseEntity<RentalResponse> createRental(@Valid @RequestBody RentalRequest rentalRequest) {
        Optional<Car> car = carRepository.findById(rentalRequest.getCarId());
        if (car.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        // check voor overlappende datums
        boolean isCarRented = rentalRepository.existsByCarIdAndDateRange(
                rentalRequest.getCarId(),
                rentalRequest.getStartDate(),
                rentalRequest.getEndDate()
        );
        if (isCarRented) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
        }
        // miss nog een message meegeven dat de datum al gereserveerd is.

        Rental rental = Rental.builder()
                .car(car.get())
                .rentalPrice(rentalRequest.getRentalPrice())
                .startDate(rentalRequest.getStartDate())
                .endDate(rentalRequest.getEndDate())
                .deposit(rentalRequest.getDeposit())
                .pickupLocation(rentalRequest.getPickupLocation())
                .email(rentalRequest.getEmail())
                .build();

        Rental savedRental = rentalRepository.save(rental);

        // populariteit aanpassen
        Popularity popularity = popularityRepository.findByCarId(rentalRequest.getCarId())
                        .orElse(Popularity.builder()
                                .car(car.get())
                                .likes(0)
                                .build());
        popularity.setLikes(popularity.getLikes() + 1);
        popularityRepository.save(popularity);

        RentalResponse rentalResponse = RentalResponse.builder()
                .id(savedRental.getId())
                .carId(savedRental.getCar().getId())
                .rentalPrice(savedRental.getRentalPrice())
                .startDate(savedRental.getStartDate())
                .endDate(savedRental.getEndDate())
                .deposit(savedRental.getDeposit())
                .pickupLocation(savedRental.getPickupLocation())
                .email(savedRental.getEmail())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(rentalResponse); // 201 Created
    }

    public ResponseEntity<RentalResponse> updateRental(@PathVariable Integer id, @Valid @RequestBody RentalRequest rentalRequest) {
        Optional<Rental> existingRental = rentalRepository.findById(id);
        if (existingRental.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        Optional<Car> car = carRepository.findById(rentalRequest.getCarId());
        if (car.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        // check voor overlappende datums
        boolean isCarRented = rentalRepository.existsByCarIdAndDateRange(
                rentalRequest.getCarId(),
                rentalRequest.getStartDate(),
                rentalRequest.getEndDate()
        );
        if (isCarRented) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
        }

        Rental rentalEntity = existingRental.get();

        // Update fields of the existing rental
        rentalEntity.setCar(car.get());
        rentalEntity.setRentalPrice(rentalRequest.getRentalPrice());
        rentalEntity.setStartDate(rentalRequest.getStartDate());
        rentalEntity.setEndDate(rentalRequest.getEndDate());
        rentalEntity.setDeposit(rentalRequest.getDeposit());
        rentalEntity.setPickupLocation(rentalRequest.getPickupLocation());
        rentalEntity.setEmail(rentalRequest.getEmail());

        Rental updatedRental = rentalRepository.save(rentalEntity);

        RentalResponse rentalResponse = RentalResponse.builder()
                .id(updatedRental.getId())
                .rentalPrice(updatedRental.getRentalPrice())
                .carId(updatedRental.getCar().getId())
                .deposit(updatedRental.getDeposit())
                .pickupLocation(updatedRental.getPickupLocation())
                .startDate(updatedRental.getStartDate())
                .endDate(updatedRental.getEndDate())
                .email(updatedRental.getEmail())
                .build();

        return ResponseEntity.ok(rentalResponse); // 200 OK
    }


    public ResponseEntity<Void> deleteRental(@PathVariable Integer id) {
        if (!rentalRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 if rental not found
        }

        rentalRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
    }
}
