package com.cabify.carpooling.test.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cabify.carpooling.model.Car;
import com.cabify.carpooling.model.Journey;
import com.cabify.carpooling.model.RegisteredJourney;
import com.cabify.carpooling.repository.CarRepository;
import com.cabify.carpooling.repository.JourneyRepository;
import com.cabify.carpooling.repository.RegisteredJourneyRepository;
import com.cabify.carpooling.service.JsonService;
import com.cabify.carpooling.test.BaseIntegrationTest;
import com.cabify.carpooling.test.seeder.CarSeeder;
import com.cabify.carpooling.test.seeder.JourneySeeder;
import com.cabify.carpooling.test.seeder.RegisteredJourneySeeder;
import com.cabify.carpooling.util.Constants;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class JourneyLocateControllerTest extends BaseIntegrationTest {

    private static final String POST_LOCATE = "/locate";

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private JourneyRepository journeyRepository;

    @Autowired
    private RegisteredJourneyRepository registeredJourneyRepository;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @PersistenceContext
    private EntityManager entityManager;

    private MockMvc mockMvc;

    @Before
    public void before() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void postLocateMethodNotAllowed() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = put(POST_LOCATE)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.[0].message", is("Method not allowed")));
    }

    @Test
    public void postLocateContentTypeNotSupported() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = post(POST_LOCATE)
            .contentType(MediaType.APPLICATION_XML_VALUE);

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Content type 'application/xml' not supported")));
    }

    @Test
    public void postLocateMissingId() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = post(POST_LOCATE)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Missing parameter ID")));
    }

    @Test
    public void postLocateNotFound() throws Exception {
        final Journey journey5PeopleWaiting = JourneySeeder.journey5PeopleWaiting();

        final MockHttpServletRequestBuilder requestBuilder = post(POST_LOCATE)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .param(Constants.PARAM_ID_JOURNEY, journey5PeopleWaiting.getId().toString());

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.[0].message", is("Journey not found")));
    }

    @Test
    public void postLocateNoContent() throws Exception {
        final Journey journey5PeopleWaiting = JourneySeeder.journey5PeopleWaiting();
        this.journeyRepository.save(journey5PeopleWaiting);

        final MockHttpServletRequestBuilder requestBuilder = post(POST_LOCATE)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .param(Constants.PARAM_ID_JOURNEY, journey5PeopleWaiting.getId().toString());

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void postLocateOk() throws Exception {
        final Car car5Seats = CarSeeder.car5Seats();
        this.carRepository.save(car5Seats);

        Journey journey3PeopleTraveling = JourneySeeder.journey3PeopleTraveling();
        journey3PeopleTraveling = this.journeyRepository.save(journey3PeopleTraveling);

        final RegisteredJourney registeredJourney =
            RegisteredJourneySeeder.registeredJourney(car5Seats, journey3PeopleTraveling);

        this.registeredJourneyRepository.save(registeredJourney);

        this.entityManager.refresh(journey3PeopleTraveling);

        final MockHttpServletRequestBuilder requestBuilder = post(POST_LOCATE)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .param(Constants.PARAM_ID_JOURNEY, journey3PeopleTraveling.getId().toString());

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(car5Seats.getId().intValue())))
            .andExpect(jsonPath("$.seats", is(car5Seats.getSeats())));
    }

}
