package com.cabify.carpooling.controller;

import com.cabify.carpooling.business.JourneyBusiness;
import com.cabify.carpooling.exception.MissingParameterException;
import com.cabify.carpooling.model.Car;
import com.cabify.carpooling.model.Journey;
import com.cabify.carpooling.util.Constants;
import com.cabify.carpooling.util.NumericUtils;

import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class JourneyController extends BaseController {

    @Autowired
    private JourneyBusiness journeyBusiness;

    @PostMapping(value = "/journey", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> postJourney(@RequestBody @Valid final Journey journey) {

        final boolean registered = this.journeyBusiness.registerJourney(journey);

        if (registered) {
            return super.buildResponse(HttpStatus.ACCEPTED, null);
        } else {
            return super.buildResponse(HttpStatus.OK, null);
        }
    }

    @PostMapping(value = "/dropoff", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> postDropOff(@RequestParam final Map<String, String> mapParam) {

        final Long idJourney = this.getIdJourney(mapParam);

        final boolean unregistered = this.journeyBusiness.dropOffJourney(idJourney);

        if (unregistered) {
            return super.buildResponse(HttpStatus.NO_CONTENT, null);
        } else {
            return super.buildResponse(HttpStatus.OK, null);
        }
    }

    @PostMapping(value = "/locate", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> postLocate(@RequestParam final Map<String, String> mapParam) {

        final Long idJourney = this.getIdJourney(mapParam);

        final Car car = this.journeyBusiness.locateJourney(idJourney);

        if (car == null) {
            return super.buildResponse(HttpStatus.NO_CONTENT, null);
        } else {
            return super.buildResponse(HttpStatus.OK, car);
        }
    }

    private Long getIdJourney(@RequestParam Map<String, String> mapParam) {
        return Optional.ofNullable(mapParam)
            .map(t -> t.get(Constants.PARAM_ID_JOURNEY))
            .map(NumericUtils::toLong)
            .orElseThrow(() -> new MissingParameterException(Constants.PARAM_ID_JOURNEY));
    }

}
