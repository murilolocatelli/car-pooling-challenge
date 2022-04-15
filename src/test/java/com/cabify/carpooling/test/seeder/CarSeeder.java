package com.cabify.carpooling.test.seeder;

import com.cabify.carpooling.model.Car;

public abstract class CarSeeder {

    public static Car car5Seats() {
        return Car.builder()
            .id(1L)
            .seats(5)
            .build();
    }

    public static Car car3Seats() {
        return Car.builder()
            .id(2L)
            .seats(3)
            .build();
    }

}
