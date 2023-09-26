package cc.robotdreams;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;


public class ApiTests
{
    private static final String BASE_URL = "http://restful-booker.herokuapp.com/";
    private String authToken;
    private static final String bookingIdToUpdate = "1";

    @BeforeClass
    public void authenticate() {
        authToken = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                    .body("{\"username\" : \"admin\", \"password\" : \"password123\"}")
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");
    }
    @Test
        public void createBookingTest() {
            String requestBody = "{"
                    + "\"firstname\" : \"Jim\","
                    + "\"lastname\" : \"Brown\","
                    + "\"totalprice\" : 111,"
                    + "\"depositpaid\" : true,"
                    + "\"bookingdates\" : {"
                    + "\"checkin\" : \"2023-10-01\","
                    + "\"checkout\" : \"2023-10-05\""
                    + "},"
                    + "\"additionalneeds\" : \"Breakfast\""
                    + "}";

            given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .header("Accept", "application/json")
                    .body(requestBody)
                    .when()
                    .post("/booking")
                    .then()
                    .statusCode(200)
                    .body("bookingid", notNullValue())
                    .body("booking.firstname", equalTo("Jim"))
                    .body("booking.lastname", equalTo("Brown"))
                    .body("booking.totalprice", equalTo(111))
                    .body("booking.depositpaid", equalTo(true))
                    .body("booking.bookingdates.checkin", equalTo("2023-10-01"))
                    .body("booking.bookingdates.checkout", equalTo("2023-10-05"))
                    .body("booking.additionalneeds", equalTo("Breakfast"));
        }
    @Test
    public void getAllBookingIdsTest() {
        given()
                .baseUri(BASE_URL)
                .header("Accept", "application/json")
                .when()
                .get("/booking")
                .then()
                .statusCode(200) //
                .body("bookingid.size()", greaterThan(0));
    }

    @Test
    public void updateBookingPriceTest() {
        int bookingIdToUpdate = getBookingIdToUpdate();

        int newPrice = 228;

        String requestBody = "{"
                + "\"totalprice\": " + newPrice + ""
                + "}";

        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .header("Cookie", "token="+ authToken)
                .body(requestBody)
                .when()
                .patch("/booking/" + bookingIdToUpdate)
                .then()
                .statusCode(200)
                .body("totalprice", equalTo(newPrice));
    }
    @Test
    public void updateBookingInfoTest() {
        int bookingIdToUpdate = getBookingIdToUpdate();
        String newName = "New Name";
        String newAdditionalNeeds = "New Additional Needs";
        String requestBody = "{"
                + "\"firstname\": \"" + newName + "\","
                + "\"lastname\": \"Updated\","
                + "\"additionalneeds\": \"" + newAdditionalNeeds + "\""
                + "}";

        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .header("Cookie", "token="+ authToken)
                .body(requestBody)
                .when()
                .put("/booking/" + bookingIdToUpdate)
                .then()
                .statusCode(200)
                .body("firstname", equalTo(newName))
                .body("additionalneeds", equalTo(newAdditionalNeeds));
    }

    private int getBookingIdToUpdate() {
        int bookingIdToUpdate = 3;
        return bookingIdToUpdate;
    }
    @Test
    public void deleteBookingTest() {
        int bookingIdToDelete = getBookingIdToDelete();

        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete("/booking/" + bookingIdToDelete)
                .then()
                .statusCode(201);
    }

    private int getBookingIdToDelete() {
        int bookingIdToDelete = 4;
        return bookingIdToDelete;
    }
}


