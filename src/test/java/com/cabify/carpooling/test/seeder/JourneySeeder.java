package com.cabify.carpooling.test.seeder;

import com.cabify.carpooling.enumeration.JourneyStatus;
import com.cabify.carpooling.model.Journey;

public abstract class JourneySeeder {

    public static Journey journey5PeopleWaiting() {
        return Journey.builder()
            .id(1L)
            .people(5)
            .status(JourneyStatus.WAITING)
            .build();
    }

    public static Journey journey3PeopleTraveling() {
        return Journey.builder()
            .id(2L)
            .people(3)
            .status(JourneyStatus.TRAVELING)
            .build();
    }

}
