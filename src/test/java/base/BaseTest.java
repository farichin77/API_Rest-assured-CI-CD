package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.ConfigReader;
import utils.ExtentManager;

import java.lang.reflect.Method;

public class BaseTest {

    protected static ExtentReports extent;
    protected ExtentTest test;

    @BeforeSuite
    public void initReport() {
        extent = ExtentManager.getInstance();
    }

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");
    }

    @BeforeMethod
    public void startTest(Method method) {
        String testName = method.getName();

        // ambil package terakhir sebagai kategori (misal: auth, sportCategory, sportActivity)
        String[] packageParts = method.getDeclaringClass().getPackage().getName().split("\\.");
        String category = packageParts[packageParts.length - 1];

        test = extent.createTest(testName).assignCategory(category);
    }

    protected void logInfo(String message) {
        test.info(message);
    }

    protected void logRequest(String endpoint, Object body) {
        test.info("Endpoint: " + endpoint);
        if (body != null) {
            test.info("Request Body:<br><pre>" + body.toString() + "</pre>");
        }
    }

    protected void logResponse(Response response) {
        test.info("Status Code: " + response.getStatusCode());
        test.info("Response:<br><pre>" + response.asString() + "</pre>");
    }

    @AfterMethod
    public void captureResult(ITestResult result) {

        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                test.pass("Test passed");
                break;

            case ITestResult.FAILURE:
                test.fail(result.getThrowable());
                break;

            case ITestResult.SKIP:
                test.skip("Test skipped");
                break;
        }
    }

    @AfterSuite
    public void tearDown() {
        extent.flush();
    }
}
