package be.vives.ti.rentalapi.controller;

import be.vives.ti.rentalapi.dto.request.RentalRequest;
import be.vives.ti.rentalapi.dto.response.RentalResponse;
import be.vives.ti.rentalapi.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@CrossOrigin
public class RentalController {
    @Autowired
    private RentalService rentalService;

    // GET All Rentals
    @GetMapping
    public ResponseEntity<List<RentalResponse>> getAllRentals() {
        return rentalService.getAllRentals();
    }

    // GET Rental by ID
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> getRentalById(@PathVariable Integer id) {
        return rentalService.getRentalById(id);
    }

    // POST a new Rental
    @PostMapping
    public ResponseEntity<RentalResponse> createRental(@Valid @RequestBody RentalRequest rentalRequest) {
        return rentalService.createRental(rentalRequest);
    }

    // PUT (update) a Rental
    @PutMapping("/{id}")
    public ResponseEntity<RentalResponse> updateRental(@PathVariable Integer id, @Valid @RequestBody RentalRequest rentalRequest) {
        return rentalService.updateRental(id, rentalRequest);
    }

    // DELETE a Rental by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Integer id) {
        return rentalService.deleteRental(id);
    }
}
