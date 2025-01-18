package be.vives.ti.rentalapi.controller;

import be.vives.ti.rentalapi.dto.response.RentalResponse;
import be.vives.ti.rentalapi.service.RentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class RentalControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    private RentalResponse rentalResponse;

    @BeforeEach
    public void setUp() {
        rentalResponse = RentalResponse.builder()
                .id(1)
                .carId(1)
                .startDate(LocalDate.of(2024, 12, 1))
                .endDate(LocalDate.of(2024, 12, 5))
                .email("user@example.com")
                .pickupLocation("Antwerp")
                .build();
    }

    @Test
    public void testGetAllRentals() throws Exception {
        List<RentalResponse> rentalResponses = Arrays.asList(rentalResponse);
        when(rentalService.getAllRentals()).thenReturn(ResponseEntity.ok(rentalResponses));

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(rentalResponses.size()))
                .andExpect(jsonPath("$[0].id").value(rentalResponse.getId()))
                .andExpect(jsonPath("$[0].carId").value(rentalResponse.getCarId()));
    }

    @Test
    public void testGetAllRentals_Empty() throws Exception {
        when(rentalService.getAllRentals()).thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    public void testGetRentalById() throws Exception {
        when(rentalService.getRentalById(1)).thenReturn(ResponseEntity.ok(rentalResponse));

        mockMvc.perform(get("/api/rentals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rentalResponse.getId()))
                .andExpect(jsonPath("$.carId").value(rentalResponse.getCarId()))
                .andExpect(jsonPath("$.email").value(rentalResponse.getEmail()));
    }

    @Test
    public void testGetRentalById_NotFound() throws Exception {
        when(rentalService.getRentalById(99)).thenReturn(ResponseEntity.status(404).build());

        mockMvc.perform(get("/api/rentals/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateRental() throws Exception {
        when(rentalService.createRental(any())).thenReturn(ResponseEntity.status(201).body(rentalResponse));

        String startDate = LocalDate.now().toString(); // Formats as "yyyy-MM-dd"
        String endDate = LocalDate.now().plusDays(2).toString(); // Formats as "yyyy-MM-dd"

        String requestPayload = String.format(
                "{ \"carId\": 1, \"rentalPrice\": 500, \"startDate\": \"%s\", \"endDate\": \"%s\", \"deposit\": 250, \"pickupLocation\": \"Antwerp\", \"email\": \"user@example.com\"}",
                startDate, endDate
        );

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(rentalResponse.getId()))
                .andExpect(jsonPath("$.carId").value(rentalResponse.getCarId()));
    }

    @Test
    public void testCreateRental_InvalidRequest() throws Exception {
        String rentalRequestJsonMissingField = """
            {
                "carId": 1,
                "startDate": "2024-12-01",
                "pickupLocation": "Antwerp"
            }
        """;

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalRequestJsonMissingField))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateRental() throws Exception {
        String startDate = LocalDate.now().toString(); // Formats as "yyyy-MM-dd"
        String endDate = LocalDate.now().plusDays(2).toString(); // Formats as "yyyy-MM-dd"

        String requestPayload = String.format(
                "{ \"carId\": 1, \"rentalPrice\": 500, \"startDate\": \"%s\", \"endDate\": \"%s\", \"deposit\": 250, \"pickupLocation\": \"Antwerp\", \"email\": \"user@example.com\"}",
                startDate, endDate
        );

        when(rentalService.updateRental(eq(1), any())).thenReturn(ResponseEntity.ok(rentalResponse));

        mockMvc.perform(put("/api/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rentalResponse.getId()))
                .andExpect(jsonPath("$.carId").value(rentalResponse.getCarId()));
    }

    @Test
    public void testDeleteRental() throws Exception {
        when(rentalService.deleteRental(1)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/api/rentals/1"))
                .andExpect(status().isNoContent());
    }
}
