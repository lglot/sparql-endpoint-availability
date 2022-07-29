package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.config.AppConfig;
import it.unife.sparql_endpoint_availability.exception.UserNotFoundException;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class UserControllerTest {

    private final AppConfig appConfig;
    private final AppUserManagement appUserManagement;
    private String adminUsername;
    private String adminPassword;
    @Value("${server.servlet.context-path}")
    private String baseUrl;

    @LocalServerPort
    int randomServerPort;

    WebDriver driver;

    @Autowired
    public UserControllerTest(AppConfig appConfig, AppUserManagement appUserManagement) {
        this.appConfig = appConfig;
        this.appUserManagement = appUserManagement;
    }

    @BeforeAll
    void setupClass() {
        //WebDriverManager.chromedriver().setup();
        WebDriverManager.firefoxdriver().setup();
        adminUsername = appConfig.getAdminUsername();
        adminPassword = appConfig.getAdminPassword();
    }

    @BeforeEach
    void setupTest() {
        //driver = new ChromeDriver();
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        driver = new FirefoxDriver(options);
    }

    @AfterEach
    void teardown() {
        driver.quit();
        try{
            appUserManagement.deleteUser("test");
        }
        catch (UserNotFoundException e){
            return;
        }
    }

    @Test
    void login_and_logout() {
        driver.get("localhost:" + randomServerPort + baseUrl + "/login");
        assertEquals("Login", driver.getTitle());

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

        driver.findElement(By.id("username")).sendKeys(adminUsername);
        driver.findElement(By.id("password")).sendKeys(adminPassword);
        driver.findElement(By.id("submit")).click();
        assertEquals("Sparql Endpoint availability", driver.getTitle());

        driver.findElement(By.id("logout")).click();
        assertEquals("Login", driver.getTitle());
    }

    @Test
    void signup() {
        driver.get("localhost:" + randomServerPort + baseUrl + "/signup");
        assertEquals("Sign up", driver.getTitle());

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

        driver.findElement(By.id("username")).sendKeys("test");
        driver.findElement(By.id("password")).sendKeys("test");
        driver.findElement(By.id("confirmPassword")).sendKeys("test");

        driver.findElement(By.id("submit")).click();
        assertEquals("Login", driver.getTitle());
        assertTrue(driver.findElement(By.id("success-message")).isDisplayed());

    }

    @Test
    void signup_with_username_already_taken() {
        driver.get("localhost:" + randomServerPort + baseUrl + "/signup");
        assertEquals("Sign up", driver.getTitle());

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

        driver.findElement(By.id("username")).sendKeys(adminUsername);
        driver.findElement(By.id("password")).sendKeys(adminPassword);
        driver.findElement(By.id("confirmPassword")).sendKeys(adminPassword);

        driver.findElement(By.id("submit")).click();

        assertEquals("Sign up", driver.getTitle());
        assertTrue(driver.findElement(By.id("error-message")).isDisplayed());


    }


    @Test
    void getUserView() {
        driver.get("localhost:" + randomServerPort + baseUrl + "/login");
        assertEquals("Login", driver.getTitle());

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

        driver.findElement(By.id("username")).sendKeys(adminUsername);
        driver.findElement(By.id("password")).sendKeys(adminPassword);
        driver.findElement(By.id("submit")).click();
        assertEquals("Sparql Endpoint availability", driver.getTitle());

        driver.get("localhost:" + randomServerPort + baseUrl + "/user");
        assertEquals("User info", driver.getTitle());


    }
}