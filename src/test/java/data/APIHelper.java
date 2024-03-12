package data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class APIHelper {

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();


    public static void sendRequestToLogin(DataHelper.AuthInfo authInfo, int statusCode) {
        given()
                .spec(requestSpec)
                .body(authInfo)

                .when()
                .post("/api/auth")

                .then().log().all()
                .statusCode(statusCode);

    }

    @Value
    public static class APITokenValue {
        String token;
    }

    public static APITokenValue sendRequestToVerifyAndGetAPITokenValue(DataHelper.VerificationData verificationData, int statusCode) {
        return given()
                .spec(requestSpec)
                .body(verificationData)

                .when()
                .post("/api/auth/verification")

                .then().log().all()
                .statusCode(statusCode)
                .extract()
                .body()
                .as(APITokenValue.class);
    }


    public static Response sendRequestToGetCardsInfo(String token, int statusCode) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)

                .when()
                .get("/api/cards")

                .then().log().all()
                .statusCode(statusCode)
                .extract()
                .response();

    }

    @Value
    public static class APICardInfo {
        String id;
        int balance;
    }

    public static Map<String, Integer> sendRequestToGetCardsBalances(String token, int statusCode) {
        APICardInfo[] cardsInfo = given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)

                .when()
                .get("/api/cards")

                .then().log().all()
                .statusCode(statusCode)
                .extract()
                .body()
                .as(APICardInfo[].class);

        Map<String, Integer> cardsBalances = new HashMap<>();
        for (APICardInfo cardInfo : cardsInfo) {
            cardsBalances.put(cardInfo.getId(), cardInfo.getBalance());
        }
        return cardsBalances;
    }

    @Value
    public static class TransferData {
        String from;
        String to;
        int amount;
    }

    public static void sendRequestToMakeTransfer(String token, TransferData transferData, int statusCode) {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(transferData)

                .when()
                .post("/api/transfer")

                .then().log().all()
                .statusCode(statusCode);


    }

}
