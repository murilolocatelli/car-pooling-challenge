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
import com.fasterxml.jackson.databind.node.ObjectNode;

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

public class JourneyControllerTest extends BaseIntegrationTest {

    private static final String POST_JOURNEY = "/journey";

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
    public void postJourneyMethodNotAllowed() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = put(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.[0].message", is("Method not allowed")));
    }

    @Test
    public void postJourneyContentTypeNotSupported() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = post(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_XML_VALUE);

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Content type 'application/xml' not supported")));
    }

    @Test
    public void postJourneyMissingBody() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = post(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Malformed request")));
    }

    @Test
    public void postJourneyMissingId() throws Exception {
        final Journey journey5PeopleWaiting = JourneySeeder.journey5PeopleWaiting();
        journey5PeopleWaiting.setId(null);

        final MockHttpServletRequestBuilder requestBuilder = post(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(journey5PeopleWaiting));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Missing parameter id")));
    }

    @Test
    public void postJourneyInvalidId() throws Exception {
        final Journey journey5PeopleWaiting = JourneySeeder.journey5PeopleWaiting();

        final ObjectNode journey5PeopleWaitingJson = this.jsonService.toObjectNode(journey5PeopleWaiting);
        journey5PeopleWaitingJson.put("id", "abc");

        final MockHttpServletRequestBuilder requestBuilder = post(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(journey5PeopleWaitingJson.toString());

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(
                "$.[0].message",
                is("Invalid parameter id - it must be filled with a valid integer number")));
    }

    @Test
    public void postJourneyMissingPeople() throws Exception {
        final Journey journey5PeopleWaiting = JourneySeeder.journey5PeopleWaiting();
        journey5PeopleWaiting.setPeople(null);

        final MockHttpServletRequestBuilder requestBuilder = post(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(journey5PeopleWaiting));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Missing parameter people")));
    }

    @Test
    public void postJourneyInvalidPeople() throws Exception {
        final Journey journey5PeopleWaiting = JourneySeeder.journey5PeopleWaiting();

        final ObjectNode journey5PeopleWaitingJson = this.jsonService.toObjectNode(journey5PeopleWaiting);
        journey5PeopleWaitingJson.put("people", "abc");

        final MockHttpServletRequestBuilder requestBuilder = post(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(journey5PeopleWaitingJson.toString());

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(
                "$.[0].message",
                is("Invalid parameter people - it must be filled with a valid integer number")));
    }

    @Test
    public void postJourneyConflict() throws Exception {
        final Journey journey5PeopleWaiting = JourneySeeder.journey5PeopleWaiting();
        this.journeyRepository.save(journey5PeopleWaiting);

        final MockHttpServletRequestBuilder requestBuilder = post(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(journey5PeopleWaiting));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.[0].message", is("Journey already exists")));
    }

    @Test
    public void postJourneyOkCarsDoNotExists() throws Exception {
        final Journey journey5PeopleWaiting = JourneySeeder.journey5PeopleWaiting();

        final MockHttpServletRequestBuilder requestBuilder = post(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(journey5PeopleWaiting));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void postJourneyOkCarsDoNotHaveEnoughSeats() throws Exception {
        this.carRepository.save(CarSeeder.car3Seats());

        final Journey journey5PeopleWaiting = JourneySeeder.journey5PeopleWaiting();

        final MockHttpServletRequestBuilder requestBuilder = post(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(journey5PeopleWaiting));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void postJourneyOkCarsDoNotHaveSeatsAvailable() throws Exception {
        final Car car5Seats = CarSeeder.car5Seats();
        this.carRepository.save(car5Seats);

        Journey journey3PeopleTraveling = JourneySeeder.journey3PeopleTraveling();
        journey3PeopleTraveling = this.journeyRepository.save(journey3PeopleTraveling);

        final RegisteredJourney registeredJourney =
            RegisteredJourneySeeder.registeredJourney(car5Seats, journey3PeopleTraveling);

        this.registeredJourneyRepository.save(registeredJourney);

        this.entityManager.refresh(journey3PeopleTraveling);

        final Journey journey5PeopleWaiting = JourneySeeder.journey5PeopleWaiting();

        final MockHttpServletRequestBuilder requestBuilder = post(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(journey5PeopleWaiting));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void postJourneyOkCarsDoNotHaveEnoughSeatsAndAvailable() throws Exception {
        final Car car5Seats = CarSeeder.car5Seats();
        this.carRepository.save(car5Seats);

        final Car car3Seats = CarSeeder.car3Seats();
        this.carRepository.save(car3Seats);

        Journey journey3PeopleTraveling = JourneySeeder.journey3PeopleTraveling();
        journey3PeopleTraveling = this.journeyRepository.save(journey3PeopleTraveling);

        final RegisteredJourney registeredJourney =
            RegisteredJourneySeeder.registeredJourney(car5Seats, journey3PeopleTraveling);

        this.registeredJourneyRepository.save(registeredJourney);

        this.entityManager.refresh(journey3PeopleTraveling);

        final Journey journey5PeopleWaiting = JourneySeeder.journey5PeopleWaiting();

        final MockHttpServletRequestBuilder requestBuilder = post(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(journey5PeopleWaiting));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void postJourneyAccepted() throws Exception {
        this.carRepository.save(CarSeeder.car5Seats());

        final Journey journey5PeopleWaiting = JourneySeeder.journey5PeopleWaiting();

        final MockHttpServletRequestBuilder requestBuilder = post(POST_JOURNEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(journey5PeopleWaiting));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$").doesNotExist());
    }

}
