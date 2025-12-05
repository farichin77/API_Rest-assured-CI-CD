package tests.sportCategory;

import body.sportCategory.UpdateSportCategoryBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileReader;

public class UpdateSportCategoryTest {

    private String token;
    private String categoryId;

    @BeforeClass
    public void setup() throws Exception {

        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // Load token
        FileReader reader = new FileReader("src/resources/json/token.json");
        JSONObject tokenJson = new JSONObject(new org.json.JSONTokener(reader));
        token = tokenJson.getString("token");
        reader.close();

        // Load category_id created earlier
        FileReader reader2 = new FileReader("src/resources/json/sport_category_id.json");
        JSONObject catJson = new JSONObject(new org.json.JSONTokener(reader2));
        categoryId = catJson.getString("category_id");
        reader2.close();
    }

    @Test
    public void updateSportCategoryValid() {

        UpdateSportCategoryBody body = new UpdateSportCategoryBody();
        String requestBody = body.getBody("Badminton").toString();

        // tampilkan request body
        System.out.println("TC 03 - Update Sport Category Valid");
        System.out.println("Endpoint: /sport-categories/update/" + categoryId);
        System.out.println(requestBody);

        // request update
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/sport-categories/update/" + categoryId)
                .then()
                .extract().response();

        // tampilkan response detail
        System.out.println("==== Response ====");
        System.out.println("Status: " + response.getStatusCode());

        // tampilkan body prettified
        try {
            JSONObject responseJson = new JSONObject(response.asString());
            System.out.println("Body:\n" + responseJson.toString(4));
        } catch (Exception e) {
            System.out.println("Body (raw): " + response.asString());
        }

        // validasi status code
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println("Status code: 200 OK");

        // validasi message
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "data saved", "Message tidak sesuai");
        System.out.println("Message: " + message);

        // validasi ID
        String resultId = response.jsonPath().getString("result.id");
        Assert.assertEquals(resultId, categoryId, "Category ID mismatch!");
        System.out.println("Category ID: " + resultId + " (update berhasil)");
        System.out.println("\n");
    }
}

