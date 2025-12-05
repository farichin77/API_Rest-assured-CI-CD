package base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import utils.ConfigReader;

public class BaseTest {
    @BeforeClass
    public void setUp() {
        String baseUrl = ConfigReader.getProperty("baseUrl");
        RestAssured.baseURI = baseUrl;
    }
}