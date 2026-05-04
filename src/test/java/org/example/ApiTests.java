package org.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ApiTests {

    @Test
    void test1_updateBooking_withConditionalResponseLogging() {
        RestAssured.baseURI = "https://restfulbooker.herokuapp.com";

        JSONObject requestBody = new JSONObject();
        requestBody.put("firstname", "James");
        requestBody.put("lastname", "Brown");
        requestBody.put("totalprice", 111);
        requestBody.put("depositpaid", true);

        JSONObject bookingDates = new JSONObject();
        bookingDates.put("checkin", "2018-01-01");
        bookingDates.put("checkout", "2019-01-01");

        requestBody.put("bookingdates", bookingDates);
        requestBody.put("additionalneeds", "Breakfast");

        Response response = given()
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .body(requestBody.toString())
                .when()
                .put("/booking/1")
                .then()
                .extract()
                .response();

        int statusCode = response.statusCode();
        if (statusCode == 201) {
            response.then().log().body();
        }

        Assertions.assertEquals(201, statusCode, "Expected 201 from update booking endpoint.");
    }

    @Test
    void test2_booksShouldHaveLessThan1000Pages_andAuthorsShouldMatch() {
        Response response = given()
                .baseUri("https://bookstore.toolsqa.com")
                .when()
                .get("/BookStore/v1/Books")
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<Integer> pages = response.jsonPath().getList("books.pages");
        Assertions.assertFalse(pages.isEmpty(), "Books list should not be empty.");

        for (Integer pageCount : pages) {
            Assertions.assertTrue(pageCount < 1000,
                    "Found a book with page count >= 1000: " + pageCount);
        }

        String firstAuthor = response.jsonPath().getString("books[0].author");
        String secondAuthor = response.jsonPath().getString("books[1].author");

        Assertions.assertEquals("Richard E. Silverman", firstAuthor,
                "First author does not match expected value.");
        Assertions.assertEquals("Addy Osmani", secondAuthor,
                "Second author does not match expected value.");
    }
}
