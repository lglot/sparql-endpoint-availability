package it.unife.sparql_endpoint_availability.controller;


import org.junit.jupiter.api.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.ServletContext;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Value("${server.servlet.context-path}")
    private String baseUrl;

    WebDriver driver;

    @BeforeAll
    void setupClass() {
        //WebDriverManager.chromedriver().setup();
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    void setupTest() {
        //driver = new ChromeDriver();
        FirefoxOptions options = new FirefoxOptions();
        driver = new FirefoxDriver(options);
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    @Test
    void login() {
        driver.get("localhost:8080" + baseUrl + "/login");
        assertEquals("Login", driver.getTitle());

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

        driver.findElement(By.id("username")).sendKeys("luigi");
        driver.findElement(By.id("password")).sendKeys("luigi");
        driver.findElement(By.id("submit")).click();
        assertEquals("Sparql Endpoint availability", driver.getTitle());
    }

    @Test
    void signup() {
        driver.get("localhost:8080" + baseUrl + "/signup");
        assertEquals("Sign up", driver.getTitle());

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

        driver.findElement(By.id("username")).sendKeys("test");
        driver.findElement(By.id("password")).sendKeys("test");
        driver.findElement(By.id("confirmPassword")).sendKeys("test");

        driver.findElement(By.id("submit")).click();
        assertEquals("Login", driver.getTitle());

        driver.findElement(By.id("username")).sendKeys("test");
        driver.findElement(By.id("password")).sendKeys("test");
        driver.findElement(By.id("submit")).click();

        assertEquals("Sparql Endpoint availability", driver.getTitle());


    }

    @Test
    void getUserView() {
        driver.get("localhost:8080" + baseUrl + "/login");
        assertEquals("Login", driver.getTitle());

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

        driver.findElement(By.id("username")).sendKeys("luigi");
        driver.findElement(By.id("password")).sendKeys("luigi");
        driver.findElement(By.id("submit")).click();
        assertEquals("Sparql Endpoint availability", driver.getTitle());

        driver.get("localhost:8080" + baseUrl + "/user");
        assertEquals("User info", driver.getTitle());
    }
}