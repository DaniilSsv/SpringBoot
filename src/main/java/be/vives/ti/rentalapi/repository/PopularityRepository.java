package be.vives.ti.rentalapi.repository;

import be.vives.ti.rentalapi.model.Popularity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PopularityRepository extends JpaRepository<Popularity, Integer> {
    Optional<Popularity> findByCarId(Integer carId);
    Optional<Popularity> deleteByCarId(Integer userId);
}
