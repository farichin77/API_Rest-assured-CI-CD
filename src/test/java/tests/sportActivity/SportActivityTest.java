package tests.sportActivity;

import base.BaseTest;
import body.sportActivity.CreateSportActivityBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SportActivityTest extends BaseTest {

    private String token;

    @BeforeClass
    public void setup() throws Exception {
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        FileReader reader = new FileReader("src/resources/json/token.json");
        JSONObject tokenJson = new JSONObject(new org.json.JSONTokener(reader));
        token = tokenJson.getString("token");
        reader.close();
    }


    @Test(priority = 1)
    public void createSportActivityValid() throws IOException {

        CreateSportActivityBody body = new CreateSportActivityBody();
        String requestBody = body.getBody("76", "170000").toString();

        System.out.println("TC-Activity-01 Create Sport Activity VALID");
        System.out.println("Endpoint: /sport-activities/create");
        System.out.println("Request Body:\n" + requestBody);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody)
                .when()
                .post("/sport-activities/create")
                .then()
                .extract().response();

        // Pretty response
        System.out.println("==== Response ====");
        try {
            JSONObject resJson = new JSONObject(response.asString());
            System.out.println(resJson.toString(4));
        } catch (Exception e) {
            System.out.println("Raw: " + response.asString());
        }

        // Validasi
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("message"), "data saved");

        // Save activity_id
        String activityId = response.jsonPath().getString("result.id");

        JSONObject json = new JSONObject();
        json.put("activity_id", activityId);

        try (FileWriter file = new FileWriter("src/resources/json/activity_id.json")) {
            file.write(json.toString(4));
            file.flush();
        }

        System.out.println("Activity ID saved → activity_id.json");
        System.out.println();
    }

    @Test(priority = 2)
    public void createSportActivityInvalidCategoryId() {

        CreateSportActivityBody body = new CreateSportActivityBody();
        String requestBody = body.getBody("", "150000").toString();

        System.out.println("TC-Activity-02 INVALID CATEGORY ID");
        System.out.println("Endpoint: /sport-activities/create");
        System.out.println("Request Body:\n" + requestBody);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody)
                .when()
                .post("/sport-activities/create")
                .then()
                .extract().response();

        System.out.println("==== Response ====");
        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 406);
        Assert.assertEquals(response.jsonPath().getString("message"),
                "The sport category id field is required.");
    }


    @Test(priority = 3)
    public void createSportActivityInvalidPrice() {

        CreateSportActivityBody body = new CreateSportActivityBody();
        String requestBody = body.getBody("78", "").toString();

        System.out.println("TC-Activity-03 INVALID PRICE");
        System.out.println("Endpoint: /sport-activities/create");
        System.out.println("Request Body:\n" + requestBody);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody)
                .when()
                .post("/sport-activities/create")
                .then()
                .extract().response();

        System.out.println("==== Response ====");
        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 406);
        Assert.assertEquals(response.jsonPath().getString("message"),
                "The price field is required.");
    }
}
