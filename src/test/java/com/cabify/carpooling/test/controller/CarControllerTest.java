package com.cabify.carpooling.test.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cabify.carpooling.model.Car;
import com.cabify.carpooling.service.JsonService;
import com.cabify.carpooling.test.BaseIntegrationTest;
import com.cabify.carpooling.test.seeder.CarSeeder;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class CarControllerTest extends BaseIntegrationTest {

    private static final String PUT_CARS = "/cars";

    @Autowired
    private JsonService jsonService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void before() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void putCarsMethodNotAllowed() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = post(PUT_CARS)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.[0].message", is("Method not allowed")));
    }

    @Test
    public void putCarsContentTypeNotSupported() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = put(PUT_CARS)
            .contentType(MediaType.APPLICATION_XML_VALUE);

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Content type 'application/xml' not supported")));
    }

    @Test
    public void putCarsMissingBody() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = put(PUT_CARS)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Malformed request")));
    }

    @Test
    public void putCarsEmptyList() throws Exception {
        final List<Car> cars = Collections.emptyList();

        final MockHttpServletRequestBuilder requestBuilder = put(PUT_CARS)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(cars));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Malformed request")));
    }

    @Test
    public void putCarsMissingId() throws Exception {
        final Car car5Seats = CarSeeder.car5Seats();
        car5Seats.setId(null);

        final List<Car> cars = Collections.singletonList(car5Seats);

        final MockHttpServletRequestBuilder requestBuilder = put(PUT_CARS)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(cars));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Missing parameter list[0].id")));
    }

    @Test
    public void putCarsInvalidId() throws Exception {
        final Car car5Seats = CarSeeder.car5Seats();

        final ObjectNode car5SeatsJson = this.jsonService.toObjectNode(car5Seats);
        car5SeatsJson.put("id", "abc");

        final List<ObjectNode> cars = Collections.singletonList(car5SeatsJson);

        final MockHttpServletRequestBuilder requestBuilder = put(PUT_CARS)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(cars.toString());

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(
                "$.[0].message",
                is("Invalid parameter [0].id - it must be filled with a valid integer number")));
    }

    @Test
    public void putCarsMissingSeats() throws Exception {
        final Car car5Seats = CarSeeder.car5Seats();
        car5Seats.setSeats(null);

        final List<Car> cars = Collections.singletonList(car5Seats);

        final MockHttpServletRequestBuilder requestBuilder = put(PUT_CARS)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(cars));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Missing parameter list[0].seats")));
    }

    @Test
    public void putCarsInvalidSeats() throws Exception {
        final Car car5Seats = CarSeeder.car5Seats();

        final ObjectNode car5SeatsJson = this.jsonService.toObjectNode(car5Seats);
        car5SeatsJson.put("seats", "abc");

        final List<ObjectNode> cars = Collections.singletonList(car5SeatsJson);

        final MockHttpServletRequestBuilder requestBuilder = put(PUT_CARS)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(cars.toString());

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(
                "$.[0].message",
                is("Invalid parameter [0].seats - it must be filled with a valid integer number")));
    }

    @Test
    public void putCarsRepeatedId() throws Exception {
        final Car car5Seats = CarSeeder.car5Seats();

        final List<Car> cars = List.of(car5Seats, car5Seats);

        final MockHttpServletRequestBuilder requestBuilder = put(PUT_CARS)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(cars));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(
                "$.[0].message",
                is("Invalid parameter id - it must not be filled with a repeated value")));
    }

    @Test
    public void putCarsOk() throws Exception {
        final Car car5Seats = CarSeeder.car5Seats();

        final List<Car> cars = Collections.singletonList(car5Seats);

        final MockHttpServletRequestBuilder requestBuilder = put(PUT_CARS)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(cars));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void putCarsMoreThanOneOk() throws Exception {
        final Car car5Seats = CarSeeder.car5Seats();
        final Car car3Seats = CarSeeder.car3Seats();

        final List<Car> cars = List.of(car5Seats, car3Seats);

        final MockHttpServletRequestBuilder requestBuilder = put(PUT_CARS)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(this.jsonService.toJsonString(cars));

        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

}
