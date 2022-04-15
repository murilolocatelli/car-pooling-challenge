package com.cabify.carpooling.business;

import com.cabify.carpooling.model.Car;
import com.cabify.carpooling.model.Journey;

public interface JourneyBusiness {

    boolean registerJourney(Journey journey);

    boolean dropOffJourney(final Long idJourney);

    Car locateJourney(final Long idJourney);

}
