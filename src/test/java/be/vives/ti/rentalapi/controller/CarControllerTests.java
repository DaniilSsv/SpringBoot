package be.vives.ti.rentalapi.controller;

import be.vives.ti.rentalapi.dto.request.CarRequest;
import be.vives.ti.rentalapi.dto.response.CarDealerResponse;
import be.vives.ti.rentalapi.dto.response.CarResponse;
import be.vives.ti.rentalapi.dto.response.PopCarsResponse;
import be.vives.ti.rentalapi.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class CarControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    private CarResponse carResponse;
    private CarDealerResponse carDealerResponse;
    private PopCarsResponse popCarsResponse;

    @BeforeEach
    public void setUp() {
        carResponse = CarResponse.builder()
                .id(1)
                .brand("Toyota")
                .model("Corolla")
                .power(150)
                .year(2020)
                .color("Red")
                .imageUri("http://example.com/car.jpg")
                .description("A reliable car")
                .build();

        carDealerResponse = CarDealerResponse.builder()
                .carId(1)
                .brand("Toyota")
                .model("Corolla")
                .power(150)
                .year(2020)
                .color("Red")
                .imageUri("http://example.com/car.jpg")
                .description("A reliable car")
                .dealerId(10)
                .dealerName("Vives Cars")
                .dealerAddress("Main Street 123")
                .dealerCity("Kortrijk")
                .dealerEmail("contact@vivescars.com")
                .dealerPostcode(8500)
                .dealerPhone(485422213)
                .build();

        popCarsResponse = PopCarsResponse.builder()
                .id(1)
                .brand("Toyota")
                .model("Corolla")
                .power(150)
                .year(2020)
                .color("Red")
                .imageUri("http://example.com/car.jpg")
                .description("A reliable car")
                .likes(95)
                .build();
    }

    @Test
    public void testGetAllCars() throws Exception {
        List<CarResponse> carResponses = Arrays.asList(carResponse);
        when(carService.getAllCars()).thenReturn(ResponseEntity.ok(carResponses));

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(carResponses.size()))
                .andExpect(jsonPath("$[0].id").value(carResponse.getId()))
                .andExpect(jsonPath("$[0].brand").value(carResponse.getBrand()))
                .andExpect(jsonPath("$[0].model").value(carResponse.getModel()));
    }

    @Test
    public void testGetAllCars_Empty() throws Exception {
        when(carService.getAllCars()).thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    public void testGetCarById() throws Exception {
        when(carService.getCarById(1)).thenReturn(ResponseEntity.ok(carResponse));

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carResponse.getId()))
                .andExpect(jsonPath("$.brand").value(carResponse.getBrand()))
                .andExpect(jsonPath("$.model").value(carResponse.getModel()));
    }

    @Test
    public void testGetCarById_NotFound() throws Exception {
        when(carService.getCarById(99)).thenReturn(ResponseEntity.status(404).build());

        mockMvc.perform(get("/api/cars/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCarWithDealer() throws Exception {
        when(carService.getCarWithDealer(1)).thenReturn(ResponseEntity.ok(carDealerResponse));

        mockMvc.perform(get("/api/cars/carWithDealer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carId").value(carDealerResponse.getCarId()))
                .andExpect(jsonPath("$.dealerName").value(carDealerResponse.getDealerName()))
                .andExpect(jsonPath("$.dealerAddress").value(carDealerResponse.getDealerAddress()));
    }

    @Test
    public void testGetCarWithDealer_NotFound() throws Exception {
        when(carService.getCarWithDealer(99)).thenReturn(ResponseEntity.status(404).build());

        mockMvc.perform(get("/api/cars/carWithDealer/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetTop4Cars() throws Exception {
        List<PopCarsResponse> popCarsResponses = Arrays.asList(popCarsResponse);
        when(carService.getTop4Cars()).thenReturn(ResponseEntity.ok(popCarsResponses));

        mockMvc.perform(get("/api/cars/topCars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(popCarsResponses.size()))
                .andExpect(jsonPath("$[0].id").value(popCarsResponse.getId()))
                .andExpect(jsonPath("$[0].brand").value(popCarsResponse.getBrand()));
    }

    @Test
    public void testGetTop4Cars_Empty() throws Exception {
        when(carService.getTop4Cars()).thenReturn(ResponseEntity.status(404).build());

        mockMvc.perform(get("/api/cars/topCars"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCar() throws Exception {
        when(carService.createCar(any(CarRequest.class))).thenReturn(ResponseEntity.ok(carResponse));

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"brand\": \"Toyota\", \"model\": \"Corolla\", \"power\": 150, \"year\": 2020, \"color\": \"Red\", \"imageUri\": \"http://example.com/car.jpg\", \"description\": \"A reliable car\", \"dealerId\": 10 }")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carResponse.getId()))
                .andExpect(jsonPath("$.brand").value(carResponse.getBrand()))
                .andExpect(jsonPath("$.model").value(carResponse.getModel()));
    }

    @Test
    public void testCreateCar_InvalidRequest() throws Exception {
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"brand\": \"\", \"model\": \"Corolla\", \"power\": 150 }")) // Missing required fields
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateCar() throws Exception {
        when(carService.updateCar(eq(1), any(CarRequest.class))).thenReturn(ResponseEntity.ok(carResponse));

        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"brand\": \"Toyota\", \"model\": \"Corolla\", \"power\": 160, \"year\": 2021, \"color\": \"Black\", \"imageUri\": \"http://example.com/car.jpg\", \"description\": \"An updated car\", \"dealerId\": 10 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carResponse.getId()))
                .andExpect(jsonPath("$.brand").value(carResponse.getBrand()))
                .andExpect(jsonPath("$.color").value("Red")); // The mock return is based on carResponse
    }

    @Test
    public void testUpdateCar_NotFound() throws Exception {
        when(carService.updateCar(eq(99), any(CarRequest.class))).thenReturn(ResponseEntity.status(404).build());

        mockMvc.perform(put("/api/cars/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"brand\": \"Toyota\", \"model\": \"Corolla\", \"power\": 160, \"year\": 2021, \"color\": \"Black\", \"imageUri\": \"http://example.com/car.jpg\", \"description\": \"An updated car\", \"dealerId\": 10 }"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCar() throws Exception {
        when(carService.deleteCar(1)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteCar_NotFound() throws Exception {
        when(carService.deleteCar(99)).thenReturn(ResponseEntity.status(404).build());

        mockMvc.perform(delete("/api/cars/99"))
                .andExpect(status().isNotFound());
    }
}
