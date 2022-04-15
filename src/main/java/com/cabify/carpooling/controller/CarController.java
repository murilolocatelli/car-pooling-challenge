package com.cabify.carpooling.controller;

import com.cabify.carpooling.business.CarBusiness;
import com.cabify.carpooling.dto.ValidList;
import com.cabify.carpooling.exception.MalformedRequestException;
import com.cabify.carpooling.exception.RepeatedParameterException;
import com.cabify.carpooling.model.Car;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class CarController extends BaseController {

    @Autowired
    private CarBusiness carBusiness;

    @PutMapping(value = "/cars", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> putCars(@RequestBody @Valid final ValidList<Car> validListCars) {

        this.validateCars(validListCars);

        this.carBusiness.saveCars(validListCars.getList());

        return super.buildResponse(HttpStatus.OK, null);
    }

    private void validateCars(final ValidList<Car> validListCars) {

        Optional.ofNullable(validListCars)
            .filter(t -> !t.isEmpty())
            .orElseThrow(MalformedRequestException::new);

        final List<Car> cars = validListCars.getList();

        long count = cars.stream()
            .filter(t -> Collections.frequency(cars, t) > 1)
            .count();

        if (count >= 2) {
            throw new RepeatedParameterException("id");
        }
    }

}
