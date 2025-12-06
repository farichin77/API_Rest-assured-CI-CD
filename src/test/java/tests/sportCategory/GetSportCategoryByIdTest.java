package tests.sportCategory;

import base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class GetSportCategoryByIdTest extends BaseTest {

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

        // Load category_id
        FileReader reader2 = new FileReader("src/resources/json/sport_category_id.json");
        JSONObject catJson = new JSONObject(new org.json.JSONTokener(reader2));
        categoryId = catJson.getString("category_id");
        reader2.close();
    }

    @Test(priority = 1)
    public void getSportCategoryByIdFromList() throws Exception {

        String endpoint = "/sport-categories";
        System.out.println("TC-get-01 Get Sport Category By ID from List");
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Query Params: is_paginate=false");
        System.out.println(" ");

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .queryParam("is_paginate", false)
                .when()
                .get(endpoint)
                .then()
                .extract().response();

        // Validasi status
        Assert.assertEquals(response.getStatusCode(), 200, "Status code tidak 200");
        System.out.println("Status code: 200 OK");

        // Validasi error field
        boolean error = response.jsonPath().getBoolean("error");
        Assert.assertFalse(error, "Error field seharusnya false");
        System.out.println("Error field: " + error);

        // Ambil list category
        List<Map<String, Object>> resultList = response.jsonPath().getList("result");
        Assert.assertNotNull(resultList, "Result list seharusnya tidak null");
        System.out.println("Total categories: " + resultList.size());

        // cari category berdasarkan ID
        Map<String, Object> category = resultList.stream()
                .filter(r -> r.get("id").toString().equals(categoryId))
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(category, "Category ID " + categoryId + " tidak ditemukan");

        // tampilkan hanya category yang dicari
        System.out.println("==== Category Response ====");
        JSONObject categoryJson = new JSONObject(category);
        System.out.println(categoryJson.toString(4));

        // Simpan full list ke file JSON
        JSONObject json = new JSONObject();
        json.put("all_categories", resultList);

        try (FileWriter file = new FileWriter("src/resources/json/all_sport_categories.json")) {
            file.write(json.toString(4)); // pretty print
            file.flush();
        }
        System.out.println("Full category list saved to all_sport_categories.json");
        System.out.println("\n");
    }
    @Test(priority = 2)
    public void getSportCategoryByIdFromListInvalid() throws Exception {

        String invalidId = "999999";
        String endpoint = "/sport-categories";

        System.out.println("TC-get-02 Get Sport Category By ID from List (Invalid)");
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Query Params: is_paginate=false");
        System.out.println("Mencari ID: " + invalidId);
        System.out.println(" ");

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .queryParam("is_paginate", false)
                .when()
                .get(endpoint)
                .then()
                .extract().response();

        // validasi status selalu 200 karena GET list bukan detail
        Assert.assertEquals(response.getStatusCode(), 200, "Status code tidak 200");
        System.out.println("Status code: 200 OK");

        // Validasi error field
        boolean error = response.jsonPath().getBoolean("error");
        Assert.assertFalse(error, "Error field seharusnya false");
        System.out.println("Error field: " + error);

        // ambil list
        List<Map<String, Object>> resultList = response.jsonPath().getList("result");
        Assert.assertNotNull(resultList, "Result list seharusnya tidak null");
        System.out.println("Total categories: " + resultList.size());

        // cari id yg tidak ada
        Map<String, Object> category = resultList.stream()
                .filter(r -> r.get("id").toString().equals(invalidId))
                .findFirst()
                .orElse(null);

        // expect: null
        Assert.assertNull(category, "Seharusnya ID " + invalidId + " tidak ditemukan!");
        System.out.println("Category ID " + invalidId + " tidak ditemukan di list (expected).");

        System.out.println("\n");
    }

}


