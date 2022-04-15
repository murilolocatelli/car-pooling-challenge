package com.cabify.carpooling.business.impl;

import com.cabify.carpooling.business.JourneyBusiness;
import com.cabify.carpooling.enumeration.JourneyStatus;
import com.cabify.carpooling.exception.EntityAlreadyExistsException;
import com.cabify.carpooling.exception.EntityNotFoundException;
import com.cabify.carpooling.model.Car;
import com.cabify.carpooling.model.Journey;
import com.cabify.carpooling.model.RegisteredJourney;
import com.cabify.carpooling.repository.CarRepository;
import com.cabify.carpooling.repository.JourneyRepository;
import com.cabify.carpooling.repository.RegisteredJourneyRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JourneyBusinessImpl implements JourneyBusiness {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private JourneyRepository journeyRepository;

    @Autowired
    private RegisteredJourneyRepository registeredJourneyRepository;

    @Override
    @Transactional
    public boolean registerJourney(final Journey journey) {

        final Optional<Journey> optionalJourney = this.journeyRepository.findById(journey.getId());

        optionalJourney.ifPresent(t -> {
            throw new EntityAlreadyExistsException(Journey.class.getSimpleName());
        });

        final List<Car> cars =
            this.carRepository.findBySeatsGreaterThanEqualOrderByCreatedAtAsc(journey.getPeople());

        final Optional<Car> availableCar = this.getAvailableCar(cars, journey.getPeople());

        if (availableCar.isPresent()) {

            journey.setStatus(JourneyStatus.TRAVELING);

            this.journeyRepository.save(journey);

            final RegisteredJourney registeredJourney = RegisteredJourney.builder()
                .car(availableCar.get())
                .journey(journey)
                .build();

            this.registeredJourneyRepository.save(registeredJourney);

            return true;

        } else {

            journey.setStatus(JourneyStatus.WAITING);

            this.journeyRepository.save(journey);

            return false;
        }
    }

    private Optional<Car> getAvailableCar(final List<Car> cars, final Integer people) {
        return cars.stream()
            .filter(t ->  this.getAvailableSeats(t) >= people)
            .findFirst();
    }

    private Integer getAvailableSeats(final Car car) {
        final List<RegisteredJourney> registeredJourneys =
            this.registeredJourneyRepository.findByCarAndJourneyStatus(car, JourneyStatus.TRAVELING);

        final Integer totalPeople = registeredJourneys.stream()
            .mapToInt(t -> t.getJourney().getPeople())
            .sum();

        return car.getSeats() - totalPeople;
    }

    @Override
    @Transactional
    public boolean dropOffJourney(final Long idJourney) {

        final Optional<Journey> optionalJourney = this.journeyRepository.findById(idJourney);

        final Journey journey = optionalJourney.orElseThrow(()
            -> new EntityNotFoundException(Journey.class.getSimpleName()));

        journey.setStatus(JourneyStatus.DROPOFF);

        this.journeyRepository.save(journey);

        final RegisteredJourney registeredJourney = journey.getRegisteredJourney();

        if (registeredJourney != null) {

            this.registeredJourneyRepository.save(registeredJourney);

            return true;

        } else {

            return false;
        }
    }

    @Override
    public Car locateJourney(final Long idJourney) {

        final Optional<Journey> optionalJourney = this.journeyRepository.findById(idJourney);

        optionalJourney.orElseThrow(() -> new EntityNotFoundException(Journey.class.getSimpleName()));

        return optionalJourney
            .map(Journey::getRegisteredJourney)
            .map(RegisteredJourney::getCar)
            .orElse(null);
    }

}
