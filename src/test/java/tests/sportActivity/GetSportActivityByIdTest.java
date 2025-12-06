package tests.sportActivity;

import base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileReader;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class GetSportActivityByIdTest extends BaseTest {

    private String token;

    @BeforeClass
    public void setup() throws Exception {
        // Set base URI
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // Baca token
        FileReader reader = new FileReader("src/resources/json/token.json");
        JSONObject tokenJson = new JSONObject(new JSONTokener(reader));
        token = tokenJson.getString("token");
        reader.close();
    }

    private int getActivityId() throws IOException {
        FileReader reader = new FileReader("src/resources/json/activity_id.json");
        JSONObject json = new JSONObject(new JSONTokener(reader));
        reader.close();
        return json.getInt("activity_id");
    }

    @Test(priority = 1)
    public void getSportActivityByValidId() throws IOException {

        int activityId = getActivityId();
        System.out.println("TC-Get-01 VALID ID");
        System.out.println("Endpoint: /sport-activities/" + activityId);

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .when()
                .get("/sport-activities/" + activityId)
                .then()
                .extract().response();

        System.out.println("Response: " + response.asString());

        // Status code harus 200
        Assert.assertEquals(response.getStatusCode(), 200);

        // error harus false
        Assert.assertFalse(response.jsonPath().getBoolean("error"));

        // result tidak boleh null
        Assert.assertNotNull(response.jsonPath().get("result"));

        // id yang dikembalikan harus sama dengan id yang diminta
        Assert.assertEquals(
                response.jsonPath().getInt("result.id"),
                activityId
        );

        // field-field penting tidak boleh null
        Assert.assertNotNull(response.jsonPath().get("result.sport_category_id"));
        Assert.assertNotNull(response.jsonPath().get("result.price"));
        Assert.assertNotNull(response.jsonPath().get("result.activity_date"));

        // cek tipe price
        Object priceValue = response.jsonPath().get("result.price");
        Assert.assertTrue(priceValue instanceof Integer);

        // cek organizer
        Assert.assertNotNull(response.jsonPath().get("result.organizer"));
        Assert.assertNotNull(response.jsonPath().get("result.organizer.name"));

        // cek city
        Assert.assertNotNull(response.jsonPath().get("result.city"));
        Assert.assertNotNull(response.jsonPath().get("result.city.city_name"));

        // cek province
        Assert.assertNotNull(response.jsonPath().get("result.city.province"));
        Assert.assertNotNull(response.jsonPath().get("result.city.province.province_name"));

        // response time
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 2)
    public void getSportActivityInvalidId() {

        int invalidId = 999999; // ID yang pasti tidak valid

        System.out.println("TC-Get-02 INVALID ID");
        System.out.println("Endpoint: /sport-activities/" + invalidId);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .when()
                .get("/sport-activities/" + invalidId)
                .then()
                .extract().response();

        System.out.println("==== Response ====");
        try {
            JSONObject resJson = new JSONObject(response.asString());
            System.out.println(resJson.toString(4));
        } catch (Exception e) {
            System.out.println("Raw Response: " + response.asString());
        }
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("--------------------------------------------------");

        Assert.assertTrue(
                response.getStatusCode() == 200 || response.getStatusCode() == 404,
                "Expected status 200 (with error) OR 404 Not Found"
        );

        Assert.assertTrue(
                response.jsonPath().getBoolean("error"),
                "Expected error = true for invalid ID"
        );

        String message = response.jsonPath().getString("message").toLowerCase();
        Assert.assertTrue(
                message.contains("not found") ||
                        message.contains("not exist") ||
                        message.contains("invalid"),
                "Message should indicate ID is invalid or not found"
        );

        // result harus null
        Assert.assertNull(
                response.jsonPath().get("result"),
                "Result must be null when ID is invalid"
        );

        Assert.assertTrue(
                response.getTime() < 2000,
                "Response too slow (> 2s)"
        );

        System.out.println();
    }
    @Test(priority = 3)
    public void getSportActivityWithoutToken() throws IOException {

        int activityId = getActivityId();

        System.out.println("TC-Get-03 WITHOUT TOKEN");
        System.out.println("Endpoint: /sport-activities/" + activityId);

        Response response = given()
                .header("Accept", "application/json")
                .when()
                .get("/sport-activities/" + activityId)
                .then()
                .extract().response();

        System.out.println("Response: " + response.asString());

        // Ekspektasi standar REST API → 401 Unauthorized
        Assert.assertEquals(
                response.getStatusCode(),
                401,
                "Seharusnya endpoint menolak request tanpa token (Unauthorized)"
        );
    }


}
