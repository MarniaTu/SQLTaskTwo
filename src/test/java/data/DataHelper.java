package data;


import lombok.Value;

import java.util.Random;


public class DataHelper {

    private DataHelper() {
    }


    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationCode {
        String verificationCode;
    }

    @Value
    public static class VerificationData {
        String login;
        String code;
    }

    @Value
    public static class CardInfo {
        String id;
        String number;
    }

    public static CardInfo getFirstCardInfo() {
        return new CardInfo("92df3f1c-a033-48e6-8390-206f6b1f56c0", "5559 0000 0000 0001");
    }

    public static CardInfo getSecondCardInfo() {
        return new CardInfo("0f3f5c2a-249e-4c3d-8287-09f7a039391d", "5559 0000 0000 0002");
    }

    public static int generateValidAmount(int balance) {
        return new Random().nextInt(balance) + 1;
    }
}
