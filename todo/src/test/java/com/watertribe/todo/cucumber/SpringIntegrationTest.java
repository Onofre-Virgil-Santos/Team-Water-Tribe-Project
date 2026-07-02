package com.watertribe.todo.cucumber;

import com.watertribe.todo.TodoApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(
    classes = TodoApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class SpringIntegrationTest {
}
