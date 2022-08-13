package com.bookit.utilities;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static com.bookit.utilities.Environment.*;
import static io.restassured.RestAssured.given;

public class BookItApiUtil {


    public static String generateToken(String email,String password){
        Response response = given().
                accept(ContentType.JSON)
                .queryParam("email", email)
                .queryParam("password", password)
                .when()
                .get(Environment.BASE_URL + "/sign")
                .then().log().all().extract().response();

        String token = response.path("accessToken");

        String finalToken ="Bearer "+token;

        return  finalToken;
    }


    //one method param role --> userType "student-member" "student-leader" "teacher"
    //returns --> token


    public static void deleteStudent(String studentEmail,String studentPassword){

        //1.send a get request to get token with student information
        String studentToken = BookItApiUtil.generateToken(studentEmail,studentPassword);

        //2.send a get request to /api/users/me endpoint and get the id number
        int idToDelete = given().accept(ContentType.JSON)
                .and().header("Authorization", studentToken)
                .when()
                .get(BASE_URL + "/api/users/me")
                .then().statusCode(200).extract().jsonPath().getInt("id");

        //3.send a delete request as a teacher to /api/students/{id} endpoint to delete the student
        String teacherToken =BookItApiUtil.generateToken(TEACHER_EMAIL,TEACHER_PASSWORD);
        given().
                pathParam("id",idToDelete)
                .and().
                header("Authorization",teacherToken)
                .when()
                .delete(BASE_URL+"/api/students/{id}")
                .then()
                .statusCode(204);
    }

    //teacher , student-member,student-leader
    //it will take user info from conf.properties
    public static String getTokenByRole(String role){
        //switch,if make sure you get correct user info
        //send request/get token/ return token
        String email, pass;

        switch (role) {

            case "teacher":
                email = TEACHER_EMAIL;
                pass = TEACHER_PASSWORD;
                break;
            case "student-member":
                email = MEMBER_EMAIL;
                pass = MEMBER_PASSWORD;
                break;

            case "student-leader":
                email = LEADER_EMAIL;
                pass = LEADER_PASSWORD;
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + role);
        }

        String accessToken =
                given()
                        .accept(ContentType.JSON)
                        .queryParams("email",email,"password",pass)
                        .when()
                        .get(BASE_URL+"/sign")
                        .then()
                        .statusCode(200)
                        .extract().jsonPath().getString("accessToken");

        System.out.println(role+": "+accessToken);
        return "Bearer " + accessToken;



    }
}
