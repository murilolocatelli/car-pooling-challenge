package com.cabify.carpooling.repository;

import com.cabify.carpooling.enumeration.JourneyStatus;
import com.cabify.carpooling.model.Car;
import com.cabify.carpooling.model.RegisteredJourney;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredJourneyRepository extends JpaRepository<RegisteredJourney, Long>  {

    List<RegisteredJourney> findByCarAndJourneyStatus(Car car, JourneyStatus journeyStatus);

}
