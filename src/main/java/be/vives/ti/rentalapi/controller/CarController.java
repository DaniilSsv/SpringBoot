package be.vives.ti.rentalapi.controller;

import be.vives.ti.rentalapi.dto.request.CarRequest;
import be.vives.ti.rentalapi.dto.response.*;
import be.vives.ti.rentalapi.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin
public class CarController {
    @Autowired
    private CarService carService;

    // GET ALL Cars
    @GetMapping
    public ResponseEntity<List<CarResponse>> getAllCars() {
        return carService.getAllCars();
    }

    // GET Car by ID
    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarById(@PathVariable Integer id) {
        return carService.getCarById(id);
    }

    // POST new Car
    @PostMapping
    public ResponseEntity<CarResponse> createCar(@Valid @RequestBody CarRequest carRequest) {
        return carService.createCar(carRequest);
    }

    // PUT update Car
    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> updateCar(@PathVariable Integer id, @Valid @RequestBody CarRequest carRequest) {
        return carService.updateCar(id, carRequest);
    }

    // DELETE delete Car
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Integer id) {
        return carService.deleteCar(id);
    }

    // GET Car with Dealer info
    @GetMapping("/carWithDealer/{id}")
    public ResponseEntity<CarDealerResponse> getCarWithDealer(@PathVariable Integer id) {
        return carService.getCarWithDealer(id);
    }

    // GET top 4 Cars
    // werkt enkel als de auto een populariteit record heeft
    @GetMapping("/topCars")
    public ResponseEntity<List<PopCarsResponse>> getTop4Cars() {
        return carService.getTop4Cars();
    }
}
