package tests.sportCategory;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileReader;

public class DeleteSportCategoryTest {

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

    @Test
    public void deleteSportCategoryTest() {

        String endpoint = "/sport-categories/delete/" + categoryId;
        System.out.println("TC 05 - Delete Sport Category");
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
}
