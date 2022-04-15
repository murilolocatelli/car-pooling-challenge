package com.cabify.carpooling.repository;

import com.cabify.carpooling.model.Car;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>  {

    @Query
    List<Car> findBySeatsGreaterThanEqualOrderByCreatedAtAsc(Integer seats);

}
