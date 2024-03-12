package test;

import data.APIHelper;
import data.DataHelper;
import data.SQLHelper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


import static data.SQLHelper.deleteDataFromAuthCodes;
import static data.SQLHelper.deleteDataFromTables;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TransferFromCardToCardTest {

    @AfterEach
    void tearDown() {
        deleteDataFromAuthCodes();
    }

    @AfterAll
    static void tearDownAll() {
        deleteDataFromTables();
    }

    @Test
    public void shouldSuccessfullyTransferFromFirstToSecondCard() {
        var authInfo = DataHelper.getAuthInfo();
        APIHelper.sendRequestToLogin(authInfo, 200);
        var verificationCode = SQLHelper.getValidVerificationCode();
        var verificationData = new DataHelper.VerificationData(authInfo.getLogin(), verificationCode.getVerificationCode());
        var tokenValue = APIHelper.sendRequestToVerifyAndGetAPITokenValue(verificationData, 200);
        var cardsBalances = APIHelper.sendRequestToGetCardsBalances(tokenValue.getToken(), 200);
        var firstCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var secondCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());
        ;
        var amount = DataHelper.generateValidAmount(firstCardBalance);
        var transferData = new APIHelper.TransferData(DataHelper.getFirstCardInfo().getNumber(), DataHelper.getSecondCardInfo().getNumber(), amount);
        APIHelper.sendRequestToMakeTransfer(tokenValue.getToken(), transferData, 200);
        cardsBalances = APIHelper.sendRequestToGetCardsBalances(tokenValue.getToken(), 200);
        var actualFirstCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var actualSecondCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());

        assertAll(() -> assertEquals(firstCardBalance - amount, actualFirstCardBalance),
                () -> assertEquals(secondCardBalance + amount, actualSecondCardBalance));

    }

    @Test
    public void shouldSuccessfullyTransferFromSecondToFirstCard() {
        var authInfo = DataHelper.getAuthInfo();
        APIHelper.sendRequestToLogin(authInfo, 200);
        var verificationCode = SQLHelper.getValidVerificationCode();
        var verificationData = new DataHelper.VerificationData(authInfo.getLogin(), verificationCode.getVerificationCode());
        var tokenValue = APIHelper.sendRequestToVerifyAndGetAPITokenValue(verificationData, 200);
        var cardsInfo = APIHelper.sendRequestToGetCardsInfo(tokenValue.getToken(), 200);
        var firstCardBalance = (int) cardsInfo.path("[1].balance");
        var secondCardBalance = (int) cardsInfo.path("[0].balance");
        var amount = DataHelper.generateValidAmount(secondCardBalance);
        var transferData = new APIHelper.TransferData(DataHelper.getSecondCardInfo().getNumber(), DataHelper.getFirstCardInfo().getNumber(), amount);
        APIHelper.sendRequestToMakeTransfer(tokenValue.getToken(), transferData, 200);
        cardsInfo = APIHelper.sendRequestToGetCardsInfo(tokenValue.getToken(), 200);
        var actualFirstCardBalance = cardsInfo.path("[1].balance");
        var actualSecondCardBalance = cardsInfo.path("[0].balance");

        assertAll(() -> assertEquals(firstCardBalance + amount, actualFirstCardBalance),
                () -> assertEquals(secondCardBalance - amount, actualSecondCardBalance));

    }
}
