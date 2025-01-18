package be.vives.ti.rentalapi.repository;

import be.vives.ti.rentalapi.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Rental r WHERE r.car.id = :carId AND " +
            "(r.startDate <= :endDate AND r.endDate >= :startDate)")
    boolean existsByCarIdAndDateRange(@Param("carId") Integer carId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    void deleteAllByCarId(Integer id);
}
