package com.matus.expenzor;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;


public class BaseTest {

    @Container
    private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest")
            .withDatabaseName("spring-expenzor-test-db")
            .withUsername("testuser")
            .withPassword("pass");

    static{
        mySQLContainer.start();
    }

}
