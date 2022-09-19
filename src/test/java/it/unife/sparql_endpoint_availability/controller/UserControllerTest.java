package it.unife.sparql_endpoint_availability.controller;

import io.github.bonigarcia.wdm.WebDriverManager;
import it.unife.sparql_endpoint_availability.config.AppConfig;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    String browser;

    @Autowired
    public UserControllerTest(AppConfig appConfig, AppUserManagement appUserManagement) {
        this.appConfig = appConfig;
        this.appUserManagement = appUserManagement;
    }

    @BeforeAll
    void setupClass() {
        browser = System.getProperty("browser");
        if (browser == null) {
            browser = "firefox";
        }
        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                break;
            default:
                throw new IllegalArgumentException("Browser not supported");
        }

        adminUsername = appConfig.getAdminUsername();
        adminPassword = appConfig.getAdminPassword();
    }

    @BeforeEach
    void setupTest() {
        switch (browser) {
            case "chrome": {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless");
                driver = new ChromeDriver(options);
                break;
            }
            case "firefox": {
                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("--headless");
                driver = new FirefoxDriver(options);
                break;
            }
            case "edge": {
                EdgeOptions options = new EdgeOptions();
                options.addArguments("--headless");
                driver = new EdgeDriver(options);
                break;
            }
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    void teardown() {
        driver.quit();
        try{
            appUserManagement.deleteUser("test");
        }
        catch (UsernameNotFoundException e){
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