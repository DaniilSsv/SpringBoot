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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private PopularityRepository popularityRepository;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private DealerRepository dealerRepository;

    public ResponseEntity<List<CarResponse>> getAllCars() {
        List<Car> cars = carRepository.findAll();
        if (cars.isEmpty()) {
            return ResponseEntity.ok().body(new ArrayList<>()); // 200
        }
        List<CarResponse> carList = cars.stream()
                .map(car ->
                        CarResponse.builder()
                                .id(car.getId())
                                .brand(car.getBrand())
                                .model(car.getModel())
                                .power(car.getPower())
                                .year(car.getYear())
                                .color(car.getColor())
                                .imageUri(car.getImageUri())
                                .description(car.getDescription())
                                .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(carList); // 200
    }

    public ResponseEntity<CarResponse> getCarById(Integer id) {
        Optional<Car> car = carRepository.findById(id);
        if (car.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        Optional<Popularity> popularity = popularityRepository.findByCarId(id);
        if (popularity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        Car carEntity = car.get();
        CarResponse carResponse = CarResponse.builder()
                .id(carEntity.getId())
                .brand(carEntity.getBrand())
                .model(carEntity.getModel())
                .power(carEntity.getPower())
                .year(carEntity.getYear())
                .color(carEntity.getColor())
                .imageUri(carEntity.getImageUri())
                .description(carEntity.getDescription())
                .build();

        return ResponseEntity.ok(carResponse); // 200
    }

    public ResponseEntity<CarResponse> createCar(CarRequest carRequest) {
        Optional<Dealer> dealer = dealerRepository.findById(carRequest.getDealerId());
        if (dealer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        Dealer dealerEntity = dealer.get();
        Car car = Car.builder()
                .brand(carRequest.getBrand())
                .model(carRequest.getModel())
                .power(carRequest.getPower())
                .year(carRequest.getYear())
                .color(carRequest.getColor())
                .imageUri(carRequest.getImageUri())
                .description(carRequest.getDescription())
                .dealer(dealerEntity)
                .build();

        car = carRepository.save(car);

        // 1 op 1
        Popularity popularity = Popularity.builder()
                .car(car)
                .likes(0)
                .build();

        popularityRepository.save(popularity);

        CarResponse carResponse = CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .power(car.getPower())
                .year(car.getYear())
                .color(car.getColor())
                .imageUri(car.getImageUri())
                .description(car.getDescription())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(carResponse); // 201
    }

    public ResponseEntity<CarResponse> updateCar(Integer id, CarRequest carRequest) {
        Optional<Car> optionalCar = carRepository.findById(id);
        if (optionalCar.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        Optional<Dealer> dealer = dealerRepository.findById(carRequest.getDealerId());
        if (dealer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
        Dealer dealerEntity = dealer.get();
        Car carEntity = optionalCar.get();

        // Update the existing Car entity
        carEntity.setDealer(dealerEntity);
        carEntity.setDescription(carRequest.getDescription());
        carEntity.setPower(carRequest.getPower());
        carEntity.setBrand(carRequest.getBrand());
        carEntity.setModel(carRequest.getModel());
        carEntity.setYear(carRequest.getYear());
        carEntity.setColor(carRequest.getColor());
        carEntity.setImageUri(carRequest.getImageUri());

        Car updatedCar = carRepository.save(carEntity);

        CarResponse carResponse = CarResponse.builder()
                .id(updatedCar.getId())
                .brand(updatedCar.getBrand())
                .model(updatedCar.getModel())
                .power(updatedCar.getPower())
                .year(updatedCar.getYear())
                .color(updatedCar.getColor())
                .imageUri(updatedCar.getImageUri())
                .description(updatedCar.getDescription())
                .build();

        return ResponseEntity.ok(carResponse); // 200
    }

    @Transactional
    public ResponseEntity<Void> deleteCar(Integer id) {
        Optional<Car> optionalCar = carRepository.findById(id);
        if (optionalCar.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        Car car = optionalCar.get();
        rentalRepository.deleteAllByCarId(car.getId()); // eerst alle rental verwijderen met zelfde car id
        popularityRepository.deleteByCarId(car.getId()); // 1 popularity record verwijderen voor popularity
        carRepository.delete(car); // car zelf verwijderen

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204
    }


    // endpoint, GET car with Dealer information
    public ResponseEntity<CarDealerResponse> getCarWithDealer(Integer id) {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        Car car = carOptional.get();
        Dealer dealer = car.getDealer();

        if (dealer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        CarDealerResponse carDealerResponse = CarDealerResponse.builder()
                .carId(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .power(car.getPower())
                .year(car.getYear())
                .color(car.getColor())
                .imageUri(car.getImageUri())
                .description(car.getDescription())
                .dealerId(dealer.getId())
                .dealerName(dealer.getName())
                .dealerAddress(dealer.getAddress())
                .dealerCity(dealer.getCity())
                .dealerEmail(dealer.getEmail())
                .dealerPostcode(dealer.getPostcode())
                .dealerPhone(dealer.getPhone())
                .build();

        return ResponseEntity.ok(carDealerResponse); // 200
    }

    // endpoint, GET top 4 cars based on popularity and power
    public ResponseEntity<List<PopCarsResponse>> getTop4Cars() {
        List<Popularity> popularities = popularityRepository.findAll();
        if (popularities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        List<PopCarsResponse> topCars = popularities.stream()
                .sorted(Comparator.comparingInt(Popularity::getLikes).reversed()
                        .thenComparing(p -> p.getCar().getPower(), Comparator.reverseOrder()))
                .limit(4)
                .map(popularity -> {
                    Car car = popularity.getCar();
                    return PopCarsResponse.builder()
                            .id(car.getId())
                            .brand(car.getBrand())
                            .model(car.getModel())
                            .power(car.getPower())
                            .year(car.getYear())
                            .color(car.getColor())
                            .imageUri(car.getImageUri())
                            .description(car.getDescription())
                            .likes(popularity.getLikes())
                            .build();
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(topCars); // 200
    }
}
