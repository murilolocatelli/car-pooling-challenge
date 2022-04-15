package com.cabify.carpooling.test;

import com.cabify.carpooling.CarPoolingApplication;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CarPoolingApplication.class)
@Transactional
public abstract class BaseIntegrationTest {

}
