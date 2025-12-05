package tests.auth;

import body.auth.LoginBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileWriter;
import java.io.IOException;

public class LoginTest {

    @Test
    public void loginTest() throws IOException {

        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        LoginBody loginBody = new LoginBody();

        // Mask username & password untuk log
        JSONObject maskedBody = new JSONObject(loginBody.loginData().toString());
        if(maskedBody.has("username")) maskedBody.put("username", "*****");
        if(maskedBody.has("password")) maskedBody.put("password", "*****");

        System.out.println("TC 01 - Login with valid username and password");
        System.out.println("Endpoint: {{baseURL}}/login");
        System.out.println("Request Body: " + maskedBody.toString(4));


        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(loginBody.loginData().toString()) // tetap pakai username/password asli
                .when()
                .post( "/login")
                .then()
                .extract().response();

        // tampilkan response body
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
        Assert.assertEquals(message, "User login successfully.", "Message does not match");
        System.out.println("Message: " + message);

        // validasi token
        String token = response.jsonPath().getString("data.token");
        Assert.assertNotNull(token, "Token should not be null");
        System.out.println("Token: " + token);

        // simpan token ke file JSON
        JSONObject tokenJson = new JSONObject();
        tokenJson.put("token", token);

        try (FileWriter file = new FileWriter("src/resources/json/token.json")) {
            file.write(tokenJson.toString(4));
            file.flush();
        }

        System.out.println("Token berhasil disimpan di src/resources/json/token.json");
        System.out.println("\n");
    }
}
