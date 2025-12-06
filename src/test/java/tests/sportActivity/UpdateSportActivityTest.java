package tests.sportActivity;

import base.BaseTest;
import body.sportActivity.UpdateSportActivityBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.Utils;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UpdateSportActivityTest extends BaseTest {

    private String token;
    private int activityId;

    @BeforeClass
    public void setup() throws Exception {

        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // ----- Baca Token -----
        FileReader reader = new FileReader("src/resources/json/token.json");
        JSONObject tokenJson = new JSONObject(new org.json.JSONTokener(reader));
        token = tokenJson.getString("token");
        reader.close();

        // ----- Baca Activity ID -----
        String activityContent =
                new String(Files.readAllBytes(Paths.get("src/resources/json/activity_id.json")));
        JSONObject activityJson = new JSONObject(activityContent);
        activityId = activityJson.getInt("activity_id");
    }

    @Test(priority = 1)
    public void updateSportActivityValid() throws Exception {

        UpdateSportActivityBody helper = new UpdateSportActivityBody();
        JSONObject requestBody =
                helper.getBodyFromFile("src/resources/json/update_activity.json",
                        Utils.getDateAfterFourDays());

        System.out.println("TC-Update-01 VALID Update");
        System.out.println("Endpoint: /sport-activities/update/" + activityId);
        System.out.println("Request:\n" + requestBody.toString(4));

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toString())
                .when()
                .post("/sport-activities/update/" + activityId)
                .then()
                .extract().response();

        System.out.println("==== Response ====");
        try {
            JSONObject json = new JSONObject(response.asString());
            System.out.println(json.toString(4));
        } catch (Exception e) {
            System.out.println(response.asString());
        }

        // Validasi
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("message"), "data saved");
    }

    @Test(priority = 2)
    public void updateSportActivityInvalidDate() throws Exception {

        UpdateSportActivityBody helper = new UpdateSportActivityBody();

        // Kirim empty activity_date
        JSONObject requestBody =
                helper.getBodyFromFile("src/resources/json/update_activity.json", "");

        System.out.println("TC-Update-02 INVALID Missing Date");
        System.out.println("Endpoint: /sport-activities/update/" + activityId);
        System.out.println("Request:\n" + requestBody.toString(4));

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toString())
                .when()
                .post("/sport-activities/update/" + activityId)
                .then()
                .extract().response();

        System.out.println("==== Response ====");
        try {
            JSONObject json = new JSONObject(response.asString());
            System.out.println(json.toString(4));
        } catch (Exception e) {
            System.out.println(response.asString());
        }

        // Validasi
        Assert.assertEquals(response.getStatusCode(), 406);
        Assert.assertEquals(response.jsonPath().getString("message"),
                "The activity date field is required.");
    }
    @Test(priority = 3)
    public void updateSportActivityInvalidMethod() {

        System.out.println("TC-Update-03 INVALID METHOD (GET)");
        System.out.println("Endpoint: /sport-activities/update/" + activityId);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .when()
                .get("/sport-activities/update/" + activityId)
                .then()
                .extract().response();

        System.out.println("==== Response ====");
        System.out.println(response.asPrettyString());

        Assert.assertTrue(
                response.getStatusCode() == 405 || response.getStatusCode() == 400,
                "Seharusnya muncul Method Not Allowed"
        );
    }
    @Test(priority = 4)
    public void updateSportActivityWithoutToken() throws Exception {

        UpdateSportActivityBody helper = new UpdateSportActivityBody();
        JSONObject requestBody =
                helper.getBodyFromFile("src/resources/json/update_activity.json",
                        Utils.getDateAfterFourDays());

        System.out.println("TC-Update-04 WITHOUT TOKEN");
        System.out.println("Endpoint: /sport-activities/update/" + activityId);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toString())
                .when()
                .post("/sport-activities/update/" + activityId)
                .then()
                .extract().response();

        System.out.println("==== Response ====");
        System.out.println(response.asPrettyString());

        Assert.assertEquals(
                response.getStatusCode(),
                401,
                "Unauthenticated."
        );
    }

}

