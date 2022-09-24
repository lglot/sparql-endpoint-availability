package it.unife.sparql_endpoint_availability.controller;

import io.github.bonigarcia.wdm.WebDriverManager;
import it.unife.sparql_endpoint_availability.config.AppConfig;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class UserControllerWebTest {

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
    public UserControllerWebTest(AppConfig appConfig, AppUserManagement appUserManagement) {
        this.appConfig = appConfig;
        this.appUserManagement = appUserManagement;
    }

    @BeforeAll
    void setupClass() {
        browser = System.getProperty("browser");
        if (browser == null || browser.isEmpty()) {
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
    void waitForLoad(WebDriver driver) {
        ExpectedCondition<Boolean> pageLoadCondition = driver1 -> {
            assert driver1 != null;
            return ((JavascriptExecutor) driver1).executeScript("return document.readyState").equals("complete");
        };
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(pageLoadCondition);
    }

    void getPage(String url) {
        driver.get("http://"+url);
        waitForLoad(driver);
    }

    void click(By by) {
        driver.findElement(by).click();
        waitForLoad(driver);
    }

    @Test
    void login_and_logout() {
        getPage("localhost:" + randomServerPort + baseUrl + "/login");
        assertEquals("Login", driver.getTitle());


        driver.findElement(By.id("username")).sendKeys(adminUsername);
        driver.findElement(By.id("password")).sendKeys(adminPassword);
        driver.findElement(By.id("submit")).click();
        assertEquals("Sparql Endpoint availability", driver.getTitle());

        driver.findElement(By.id("logout")).click();
        assertEquals("Login", driver.getTitle());
    }

    @Test
    void signup() {
        getPage("localhost:" + randomServerPort + baseUrl + "/signup");
        assertEquals("Sign up", driver.getTitle());


        driver.findElement(By.id("username")).sendKeys("test");
        driver.findElement(By.id("password")).sendKeys("test");
        driver.findElement(By.id("confirmPassword")).sendKeys("test");

        click(By.id("submit"));
        assertEquals("Login", driver.getTitle());
        assertTrue(driver.findElement(By.id("success-message")).isDisplayed());

    }

    @Test
    void signup_with_username_already_taken() {
        getPage("localhost:" + randomServerPort + baseUrl + "/signup");
        assertEquals("Sign up", driver.getTitle());


        driver.findElement(By.id("username")).sendKeys(adminUsername);
        driver.findElement(By.id("password")).sendKeys(adminPassword);
        driver.findElement(By.id("confirmPassword")).sendKeys(adminPassword);

        click(By.id("submit"));
        assertEquals("Sign up", driver.getTitle());
        assertTrue(driver.findElement(By.id("error-message")).isDisplayed());


    }


    @Test
    void getUserView() {
        getPage("localhost:" + randomServerPort + baseUrl + "/login");
        assertEquals("Login", driver.getTitle());

        driver.findElement(By.id("username")).sendKeys(adminUsername);
        driver.findElement(By.id("password")).sendKeys(adminPassword);
        driver.findElement(By.id("submit")).click();
        assertEquals("Sparql Endpoint availability", driver.getTitle());

        getPage("localhost:" + randomServerPort + baseUrl + "/user");
        assertEquals("User info", driver.getTitle());

        //assert that info is shown
        assertEquals(driver.findElement(By.id("username")).getAttribute("value"), adminUsername);
        assertEquals(driver.findElement(By.id("role")).getAttribute("value"), "ADMIN");
        assertTrue(driver.findElement(By.id("jwtToken")).getAttribute("value").length() > 0);



        //return to home
        click(By.id("home"));
        assertEquals("Sparql Endpoint availability", driver.getTitle());
    }

    @Test
    void manageUser() {
        appUserManagement.saveUser("test", "test", "USER");


        getPage("localhost:" + randomServerPort + baseUrl + "/login");
        assertEquals("Login", driver.getTitle());


        driver.findElement(By.id("username")).sendKeys(adminUsername);
        driver.findElement(By.id("password")).sendKeys(adminPassword);
        driver.findElement(By.id("submit")).click();
        assertEquals("Sparql Endpoint availability", driver.getTitle());

        click(By.id("user-management"));
        assertEquals("User management", driver.getTitle());

        //assert that user table is shown
        assertTrue(driver.findElement(By.id("user-table")).isDisplayed());

        //assert that user is shown
        assertTrue(driver.findElement(By.id("user-test")).isDisplayed());

        // click disable button in table with id user-test
        driver.findElement(By.id("user-test")).findElement(By.linkText("Disable")).click();
        //assert that user is disabled
        assertEquals(driver.findElement(By.id("user-test")).findElement(By.className("enabled-row")).getText(), "false");

        // click enable button in table with id user-test
        driver.findElement(By.id("user-test")).findElement(By.linkText("Enable")).click();
        //assert that user is enabled
        assertEquals(driver.findElement(By.id("user-test")).findElement(By.className("enabled-row")).getText(), "true");

        // click delete button in table with id user-test
        driver.findElement(By.id("user-test")).findElement(By.linkText("Delete")).click();

        //assert that user is deleted
        assertThrows(NoSuchElementException.class, () -> driver.findElement(By.id("user-test")));


        //return to home
        click(By.id("home"));
        assertEquals("Sparql Endpoint availability", driver.getTitle());
    }
}