package com.cabify.carpooling.test.seeder;

import com.cabify.carpooling.model.Car;
import com.cabify.carpooling.model.Journey;
import com.cabify.carpooling.model.RegisteredJourney;

public abstract class RegisteredJourneySeeder {

    public static RegisteredJourney registeredJourney(final Car car, final Journey journey) {
        return RegisteredJourney.builder()
            .car(car)
            .journey(journey)
            .build();
    }

}
