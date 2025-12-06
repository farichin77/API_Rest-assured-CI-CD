package tests.sportCategory;

import base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileReader;

public class DeleteSportCategoryTest extends BaseTest {

    private String token;
    private String categoryId;

    @BeforeClass
    public void setup() throws Exception {
        // Set Base URL
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // Baca token dari token.json
        FileReader reader = new FileReader("src/resources/json/token.json");
        JSONObject tokenJson = new JSONObject(new org.json.JSONTokener(reader));
        token = tokenJson.getString("token");
        reader.close();

        // Baca category_id dari file JSON
        FileReader reader2 = new FileReader("src/resources/json/sport_category_id.json");
        JSONObject catJson = new JSONObject(new org.json.JSONTokener(reader2));
        categoryId = catJson.getString("category_id");
        reader2.close();
    }

    @Test(priority = 1)
    public void deleteSportCategoryTest() {

        String endpoint = "/sport-categories/delete/" + categoryId;
        System.out.println("TC-Delete-01  Delete Sport Category");
        System.out.println("Endpoint: " + endpoint);
        System.out.println(" ");

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .when()
                .delete(endpoint)
                .then()
                .extract().response();

        // tampilkan response body rapi
        System.out.println("==== Response ====");
        try {
            JSONObject responseJson = new JSONObject(response.asString());
            System.out.println(responseJson.toString(4));
        } catch (Exception e) {
            System.out.println("Body (raw): " + response.asString());
        }

        // validasi status code
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println("Status code: 200 OK");

        // validasi message
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "Data deleted successfully", "Message tidak sesuai");
        System.out.println("Message: " + message);
    }
    @Test(priority = 2)
    public void deleteSportCategoryInvalidId() {

        String invalidId = "999999"; // ID yang dijamin tidak ada
        String endpoint = "/sport-categories/delete/" + invalidId;

        System.out.println("TC-Delete-02  Delete Sport Category Invalid (ID tidak ditemukan)");
        System.out.println("Endpoint: " + endpoint);
        System.out.println(" ");

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .when()
                .delete(endpoint)
                .then()
                .extract().response();

        // tampilkan response body rapi
        System.out.println("==== Response ====");
        try {
            JSONObject responseJson = new JSONObject(response.asString());
            System.out.println(responseJson.toString(4));
        } catch (Exception e) {
            System.out.println("Body (raw): " + response.asString());
        }

        // validasi status code
        Assert.assertEquals(response.getStatusCode(), 400, "Status code invalid seharusnya 400");
        System.out.println("Status code: 400 Bad Request");

        // validasi message
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "category not found", "Message tidak sesuai");
        System.out.println("Message: " + message);

        System.out.println(" ");
    }
    @Test(priority = 3)
    public void deleteSportCategoryInvalidToken() {

        String endpoint = "/sport-categories/delete/" + categoryId;
        String invalidToken = "this_is_invalid_token_123";

        System.out.println("TC-Delete-03  Delete Sport Category Invalid Token");
        System.out.println("Endpoint: " + endpoint);
        System.out.println(" ");

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + invalidToken)
                .header("Accept", "application/json")
                .when()
                .delete(endpoint)
                .then()
                .extract().response();

        // tampilkan response body rapi
        System.out.println("==== Response ====");
        try {
            JSONObject responseJson = new JSONObject(response.asString());
            System.out.println(responseJson.toString(4));
        } catch (Exception e) {
            System.out.println("Body (raw): " + response.asString());
        }

        // validasi status code (biasanya 401 untuk token salah)
        Assert.assertEquals(response.getStatusCode(), 401, "Status code invalid token seharusnya 401");
        System.out.println("Status code: 401 Unauthorized");

        // validasi message
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "Unauthenticated.", "Message tidak sesuai");
        System.out.println("Message: " + message);

        System.out.println(" ");
    }


}
