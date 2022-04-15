package com.cabify.carpooling.business.impl;

import com.cabify.carpooling.business.CarBusiness;
import com.cabify.carpooling.model.Car;
import com.cabify.carpooling.repository.CarRepository;
import com.cabify.carpooling.repository.JourneyRepository;
import com.cabify.carpooling.repository.RegisteredJourneyRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarBusinessImpl implements CarBusiness {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private JourneyRepository journeyRepository;

    @Autowired
    private RegisteredJourneyRepository registeredJourneyRepository;

    @Override
    @Transactional
    public void saveCars(final List<Car> cars) {

        this.registeredJourneyRepository.deleteAll();
        this.journeyRepository.deleteAll();
        this.carRepository.deleteAll();

        this.carRepository.saveAll(cars);
    }

}
