package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtil;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.Map;

import static com.bookit.utilities.Environment.BASE_URL;
import static com.bookit.utilities.Environment.URL;
import static io.restassured.RestAssured.*;

public class ApiStepDefs {
    String token;
    Response response;

    @Given("I logged Bookit api using {string} and {string}")
    public void i_logged_Bookit_api_using_and(String username, String password) {
        token = BookItApiUtil.generateToken(username, password);
    }

    @When("I send POST request to {string} endpoint with following information")
    public void i_send_POST_request_to_endpoint_with_following_information(String path, Map<String, String> studentInfo) {
        response = given().contentType(ContentType.JSON)
                .and().queryParams(studentInfo)
                .and().header("Authorization", token)
                .when().post(BASE_URL + path)
                .then()
                .and().log().all()
                .and().extract().response();
    }

    @Then("status code should be {int}")
    public void status_code_should_be(int statusCode) {
        Assert.assertEquals(statusCode, response.statusCode());
    }

    @Then("I delete previously added student")
    public void i_delete_previously_added_student() {
        int studentId = response.path("entryiId");
        System.out.println("studentId = " + studentId);

        given().pathParam("id", studentId)
                .and().header("Authorization", token)
                .when().delete(BASE_URL + "/api/students/{id}")
                .then().statusCode(204)
                .and().log().all();
    }

    @Given("I get env properties")
    public void iGetEnvProperties() {
        System.out.println("URL = " + URL);
        System.out.println("BASE_URL = " + BASE_URL);
    }

    @Given("I logged Bookit api as {string}")
    public void iLoggedBookitApiAs(String arg0) {
    }
}
