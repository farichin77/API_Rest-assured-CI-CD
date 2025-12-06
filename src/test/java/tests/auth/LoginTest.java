package tests.auth;

import base.BaseTest;
import body.auth.LoginBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileWriter;
import java.io.IOException;

public class LoginTest extends BaseTest {

    @Test(priority = 1)
    public void loginTestValidCredential() throws IOException {

        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        LoginBody loginBody = new LoginBody();

        // Mask username & password untuk log
        JSONObject maskedBody = new JSONObject(loginBody.loginData().toString());
        if(maskedBody.has("username")) maskedBody.put("username", "*****");
        if(maskedBody.has("password")) maskedBody.put("password", "*****");

        System.out.println("TC-Login-01  Login with valid username and password");
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
    private void maskAndPrintRequest(JSONObject req) {
        JSONObject masked = new JSONObject(req.toString());
        masked.put("username", "*****");
        masked.put("password", "*****");

        System.out.println("Request Body: " + masked.toString(4));
    }

    private void printResponse(Response response) {
        System.out.println("=== Response ===");
        try {
            JSONObject json = new JSONObject(response.asString());
            System.out.println(json.toString(4));
        } catch (Exception e) {
            System.out.println("Body (raw): " + response.asString());
        }
    }

    @Test(priority = 2)
    public void loginInvalidUsername() {
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        System.out.println("TC-Login-02 Login with INVALID username");
        System.out.println("Endpoint: {{baseURL}}/login");

        JSONObject req = new JSONObject();
        req.put("username", "wrong_user");
        req.put("password", "syukron123");

        maskAndPrintRequest(req);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(req.toString())
                .post("/login");

        printResponse(response);

        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertEquals(response.jsonPath().getString("message"), "Unauthorised.");

        System.out.println("TC02 PASSED\n");
    }

    @Test(priority = 3)
    public void loginInvalidPassword() {
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        System.out.println("TC-Login-03 Login with INVALID password");
        System.out.println("Endpoint: {{baseURL}}/login");

        JSONObject req = new JSONObject();
        req.put("username", "syukron@gmail.com");
        req.put("password", "syukron_wrong");

        maskAndPrintRequest(req);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(req.toString())
                .post("/login");

        printResponse(response);

        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertEquals(response.jsonPath().getString("message"), "Unauthorised.");

        System.out.println("TC03 PASSED\n");
    }
    @Test(priority = 4)
    public void loginWithEmptyUsernameAndPassword() {
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        System.out.println("TC-Login-04 loginWithEmptyUsernameAndPassword");
        System.out.println("Endpoint: {{baseURL}}/login");

        JSONObject req = new JSONObject();
        req.put("username", "");
        req.put("password", "");

        maskAndPrintRequest(req);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(req.toString())
                .post("/login");

        printResponse(response);

        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertEquals(response.jsonPath().getString("message"), "Unauthorised.");

        System.out.println("TC04 PASSED\n");
    }

}
