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

public class DeleteSportActivityByIdTest extends BaseTest {

    private String token;

    @BeforeClass
    public void setup() throws Exception {
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

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
    public void deleteSportActivityValid() {

        System.out.println("TC-Delete-01 Sport Activity VALID");
        System.out.println("Endpoint: /sport-activities/delete/{id}");

        // Load activity_id from JSON
        String activityId = "";
        try (FileReader reader = new FileReader("src/resources/json/activity_id.json")) {
            JSONObject saved = new JSONObject(new org.json.JSONTokener(reader));
            activityId = saved.getString("activity_id");
        } catch (Exception e) {
            System.out.println("ERROR: Cannot read activity_id.json");
            Assert.fail("Missing activity_id.json");
        }

        System.out.println("Activity ID: " + activityId);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .when()
                .delete("/sport-activities/delete/" + activityId)
                .then()
                .extract().response();

        System.out.println("==== Response ====");
        try {
            JSONObject resJson = new JSONObject(response.asString());
            System.out.println(resJson.toString(4));
        } catch (Exception e) {
            System.out.println("Raw: " + response.asString());
        }

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("message"), "Data deleted successfully");

        System.out.println("Delete berhasil untuk activity_id = " + activityId);
        System.out.println();
    }

    @Test(priority = 2)
    public void deleteSportActivityInvalidToken() {

        System.out.println("TC-Delete-02 DELETE Sport Activity INVALID TOKEN");
        System.out.println("Endpoint: /sport-activities/delete/{id}");

        String activityId = "";
        try (FileReader reader = new FileReader("src/resources/json/activity_id.json")) {
            JSONObject saved = new JSONObject(new org.json.JSONTokener(reader));
            activityId = saved.getString("activity_id");
        } catch (Exception e) {
            System.out.println("ERROR: Cannot read activity_id.json");
            Assert.fail("Missing activity_id.json");
        }

        String invalidToken = "TokenYangSalahAtauExpired123";

        System.out.println("Activity ID: " + activityId);
        System.out.println("Using INVALID token");

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + invalidToken)
                .header("Accept", "application/json")
                .when()
                .delete("/sport-activities/delete/" + activityId)
                .then()
                .extract().response();

        System.out.println("==== Response ====");
        try {
            JSONObject resJson = new JSONObject(response.asString());
            System.out.println(resJson.toString(4));
        } catch (Exception e) {
            System.out.println("Raw: " + response.asString());
        }

        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.asString().toLowerCase().contains("unauthenticated")
                        || response.asString().toLowerCase().contains("invalid"),
                "Message must show token invalid");

        System.out.println();
    }


    @Test(priority = 3)
    public void deleteSportActivityNotFound() {

        System.out.println("TC-Delete-03 DELETE Sport Activity NOT FOUND");
        System.out.println("Endpoint: /sport-activities/delete/{id}");

        String wrongId = "999999"; //

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .when()
                .delete("/sport-activities/delete/" + wrongId)
                .then()
                .extract().response();

        System.out.println("==== Response ====");
        try {
            JSONObject resJson = new JSONObject(response.asString());
            System.out.println(resJson.toString(4));
        } catch (Exception e) {
            System.out.println("Raw: " + response.asString());
        }


        Assert.assertEquals(response.getStatusCode(), 406,
                "Sport Activity not found");

        // Validasi pesan error
        Assert.assertTrue(
                response.asString().toLowerCase().contains("not found") ||
                        response.asString().toLowerCase().contains("no data"),
                "Message harus menunjukkan bahwa data tidak ditemukan"
        );

        System.out.println();
    }

}
