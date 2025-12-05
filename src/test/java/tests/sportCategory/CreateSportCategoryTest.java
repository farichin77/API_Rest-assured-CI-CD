package tests.sportCategory;

import body.sportCategory.CreateSportCategoryBody;
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

public class CreateSportCategoryTest {

    private String token;

    @BeforeClass
    public void setup() throws Exception {
        // set base URI
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // baca token dari token.json
        FileReader reader = new FileReader("src/resources/json/token.json");
        JSONObject tokenJson = new JSONObject(new org.json.JSONTokener(reader));
        token = tokenJson.getString("token");
        reader.close();
    }

    @Test
    public void createSportCategoryValid() throws IOException {

        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // body
        CreateSportCategoryBody categoryBody = new CreateSportCategoryBody();
        String requestBody = categoryBody.getBody("Sepakbola").toString();

        // tampilkan request body
        System.out.println("TC 02 - Create Sport Category Valid");
        System.out.println("Endpoint: /sport-categories/create");
        System.out.println(requestBody);

        // request
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/sport-categories/create")
                .then()
                .extract().response();

        // tampilkan response detail
        System.out.println("==== Response ====");
        System.out.println("Status: " + response.getStatusCode());

        // body prettified
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

        // ambil category ID
        String category_id = response.jsonPath().getString("result.id");
        System.out.println("Category ID: " + category_id);

        // simpan ke file JSON
        JSONObject json = new JSONObject();
        json.put("category_id", category_id);

        try (FileWriter file = new FileWriter("src/resources/json/sport_category_id.json")) {
            file.write(json.toString(4));
            file.flush();
        }
        System.out.println("Category ID berhasil disimpan ke file JSON");
        System.out.println("\n");
    }

}

