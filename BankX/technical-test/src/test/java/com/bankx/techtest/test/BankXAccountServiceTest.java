package com.bankx.techtest.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bankx.techtest.Constants.Constants;
import com.bankx.techtest.Service.*;
import com.bankx.techtest.Domain.*;
import com.bankx.techtest.Domain.Transaction.Transaction;

public class BankXAccountServiceTest {

    @DisplayName("Test BankXAccountServiceTest:: configuration inputs test")
    @Test
    void testBankXAccountServiceTest() {

        BankXAccountService bankXAccountService = new BankXAccountService();
        bankXAccountService.setSavingsAccountJoiningBonus(400f);
        bankXAccountService.setSavingsAccountCreditReward(0.005f);
        bankXAccountService.setTransactionalAccountPaymentsFee(0.0005f);
        assertEquals(bankXAccountService.getSavingsAccountJoiningBonus(), 400f);
        assertEquals(bankXAccountService.getSavingsAccountCreditReward(), 0.005f);
        assertEquals(bankXAccountService.getTransactionalAccountPaymentsFee(), 0.0005f);
        try {
            bankXAccountService.setBankZEndOfDayTransactionsFilePath(null);
        } catch (Exception ex) {
            assertEquals(ex.getMessage().compareTo("BankXAccountService::setBankZEndOfDayTransactionsFilePath - received null or empty input"), 0);
        }

        try {
            bankXAccountService.setBankZEndOfDayTransactionsFilePath("eodTransactions/BankZEodReconTransactions.json");
            assertEquals(bankXAccountService.getBankZEndOfDayTransactionsFilePath(), "eodTransactions/BankZEodReconTransactions.json");
        } catch (Exception ex) {
            assertEquals(ex.getMessage().compareTo("No exception"), 0);  // fail the test
        }
    }

    @DisplayName("Test BankXAccountServiceTest:: onboardNewCustomer")
    @Test
    void testOnboardNewCustomer() {

        String result = "";
        AccountHolder accountHolder = new AccountHolder(11111l, "Tom", "Jones",
                "11111", "TestAddress", "0728297019",
                "Test@email.com");

        BankXAccountService bankXAccountService = new BankXAccountService();
        bankXAccountService.setSavingsAccountJoiningBonus(400f);
        bankXAccountService.setSavingsAccountCreditReward(0.005f);
        bankXAccountService.setTransactionalAccountPaymentsFee(0.0005f);

        try {
            bankXAccountService.setBankZEndOfDayTransactionsFilePath("eodTransactions/BankZEodReconTransactions.json");
            result = bankXAccountService.onboardNewCustomer(null, 500f, 1000f, null, null);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "BankXAccountService::onboardNewCustomer received null input");
        }

        try {
            result = bankXAccountService.onboardNewCustomer(accountHolder, 500f, 1000f, 123456l, 246810l);
            String expectedResult = "\r\nNew customer onboarded.\r\nCustomer id=" + 11111l + ".\r\nsavingsAccount number=" + 123456l + ". balance=" + 900f +
                    ".\r\ncurrentAccount number=" + 246810l + ". balance=" + 1000f + "\r\n";
            assertEquals(result.compareTo(expectedResult), 0);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "No exception expected"); //fail the test
        }

        try {
            result = bankXAccountService.onboardNewCustomer(accountHolder, 500f, 1000f, 123456l, 246810l);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "BankXAccountService::onboardNewCustomer - Cannot onboard new customer with account id's that have been already allocated, or a customer id that already exists on the system");
        }

    }

    @DisplayName("Test BankXAccountService::doInterAccountTransfer")
    @Test
    void testDoInterAccountTransfer() {

        boolean result;
        AccountHolder accountHolder = new AccountHolder(11111l, "Tom", "Jones",
                "11111", "TestAddress", "0728297019",
                "Test@email.com");

        AccountHolder accountHolderUnboarded = new AccountHolder(22222l, "Tim", "Cat",
                "22222", "Test1Address", "0728297015",
                "Test1@email.com");

        BankXAccountService bankXAccountService = new BankXAccountService();
        bankXAccountService.setSavingsAccountJoiningBonus(400f);
        bankXAccountService.setSavingsAccountCreditReward(0.005f);
        bankXAccountService.setTransactionalAccountPaymentsFee(0.0005f);

        try {
            bankXAccountService.setBankZEndOfDayTransactionsFilePath("eodTransactions/BankZEodReconTransactions.json");
            bankXAccountService.onboardNewCustomer(accountHolder, 500f, 1000f, 123456l, 246810l);
            result = bankXAccountService.doInterAccountTransfer(accountHolder, Constants.SAVINGS_ACCOUNT, 100f);
            assertEquals(result, true);
            result = bankXAccountService.doInterAccountTransfer(accountHolder, Constants.CURRENT_ACCOUNT, 300f);
            assertEquals(result, true);
            result = bankXAccountService.doInterAccountTransfer(accountHolder, "ANOTHER_ACCOUNT", 300f); // invalid account type
            assertEquals(result, false);
            result = bankXAccountService.doInterAccountTransfer(accountHolderUnboarded, Constants.SAVINGS_ACCOUNT, 300f); // unboarded customer
            assertEquals(result, false);
        } catch (Exception e) {
            assertEquals(e.getMessage(), ""); // fail the test - expecting no exceptions
        }

    }

    @DisplayName("Test BankXAccountService::makePayment")
    @Test
    void testMakePayment() {

        boolean result;
        AccountHolder sourceAccountHolder = new AccountHolder(11111l, "Tom", "Jones",
                "11111", "TestAddress", "0728297019",
                "Test@email.com");

        AccountHolder destinationAccountHolder = new AccountHolder(22222l, "Tim", "Cat",
                "22222", "Test1Address", "0728297015",
                "Test1@email.com");

        AccountHolder unboardedCustomer = new AccountHolder(33333l, "Jim", "Black",
                "33333", "Test3Address", "0738297015",
                "Jim@email.com");

        BankXAccountService bankXAccountService = new BankXAccountService();
        bankXAccountService.setSavingsAccountJoiningBonus(400f);
        bankXAccountService.setSavingsAccountCreditReward(0.005f);
        bankXAccountService.setTransactionalAccountPaymentsFee(0.0005f);

        try {
            bankXAccountService.setBankZEndOfDayTransactionsFilePath("eodTransactions/BankZEodReconTransactions.json");
            bankXAccountService.onboardNewCustomer(sourceAccountHolder, 500f, 1000f, 123456l, 246810l);
            bankXAccountService.onboardNewCustomer(destinationAccountHolder, 250.0f, 800.70f, 123457l, 246811l);
            result = bankXAccountService.makePayment(sourceAccountHolder, 250.30f, 123457l);
            assertEquals(result, true);
            result = bankXAccountService.makePayment(sourceAccountHolder, 70.75f, 454565675l); // to account that does not exist on the system
            assertEquals(result, false);
            result = bankXAccountService.makePayment(unboardedCustomer, 450.30f, 123456l); // from customer that does not exist on the system
            assertEquals(result, false);
        } catch (Exception e) {
            assertEquals(e.getMessage(), ""); // fail the test - not expecting any exceptions
        }
    }

    @DisplayName("Test BankXAccountService::provideCustomerTransactionHistory")
    @Test
    void testProvideCustomerTransactionHistory() {

        List<Transaction> history;
        boolean result;

        AccountHolder accountHolder = new AccountHolder(11111l, "Tom", "Jones",
                "11111", "TestAddress", "0728297019",
                "Test@email.com");

        AccountHolder accountHolderOne = new AccountHolder(22222l, "Tim", "Cat",
                "22222", "Test1Address", "0728297015",
                "Test1@email.com");

        AccountHolder unboardedCustomer = new AccountHolder(33333l, "Jim", "Black",
                "33333", "Test3Address", "0738297015",
                "Jim@email.com");

        BankXAccountService bankXAccountService = new BankXAccountService();
        bankXAccountService.setSavingsAccountJoiningBonus(400f);
        bankXAccountService.setSavingsAccountCreditReward(0.005f);
        bankXAccountService.setTransactionalAccountPaymentsFee(0.0005f);

        try {
            bankXAccountService.setBankZEndOfDayTransactionsFilePath("eodTransactions/BankZEodReconTransactions.json");

            bankXAccountService.onboardNewCustomer(accountHolder, 1600f, 3000f, 123456l, 246810l);
            bankXAccountService.onboardNewCustomer(accountHolderOne, 1500f, 1200f, 123457l, 246811l);
            history = bankXAccountService.provideCustomerTransactionHistory(accountHolder, Constants.SAVINGS_ACCOUNT);
            assertEquals(history.size(), 0);  // no transactions have been made

            bankXAccountService.makePayment(accountHolder, 250.30f, 123457l);
            bankXAccountService.doInterAccountTransfer(accountHolder, Constants.SAVINGS_ACCOUNT, 300f);
            history = bankXAccountService.provideCustomerTransactionHistory(accountHolder, Constants.SAVINGS_ACCOUNT);
            assertEquals(history.size(), 1);  // one inter account transfer made from savings account
            assertEquals(history.get(0).getAmount(), 300f);
            history = bankXAccountService.provideCustomerTransactionHistory(accountHolder, Constants.CURRENT_ACCOUNT);
            assertEquals(history.size(), 2);  // one payment  made
            assertEquals(history.get(0).getAmount(), 250.42516f);
            assertEquals(history.get(1).getAmount(), 300f);

            // transaction history for unboarded customer
            history = bankXAccountService.provideCustomerTransactionHistory(unboardedCustomer, Constants.CURRENT_ACCOUNT);
            assertEquals(history, null);

            // transaction history for undefined account type
            history = bankXAccountService.provideCustomerTransactionHistory(accountHolder, "ANOTHER ACCOUNT TYPE");
            assertEquals(history, null);
        } catch (Exception e) {
            assertEquals(e.getMessage(), ""); // fail the test - not expecting any exceptions
        }
    }

    @DisplayName("Test BankXAccountService::doBankZImmidiateTransactions")
    @Test
    void testDoBankZImmidiateTransactions() {

        boolean result;
        Transaction transaction;
        List<Transaction> transactions = new ArrayList<>();

        AccountHolder accountHolder = new AccountHolder(11111l,"Tom","Jones",
                "11111","TestAddress","0728297019",
                "Test@email.com");

        AccountHolder accountHolderOne = new AccountHolder(22222l,"Tim","Cat",
                "22222","Test1Address","0728297015",
                "Test1@email.com");

        AccountHolder unboardedCustomer = new AccountHolder(33333l,"Jim","Black",
                "33333","Test3Address","0738297015",
                "Jim@email.com");

        BankXAccountService bankXAccountService = new BankXAccountService();
        bankXAccountService.setSavingsAccountJoiningBonus(400f);
        bankXAccountService.setSavingsAccountCreditReward(0.005f);
        bankXAccountService.setTransactionalAccountPaymentsFee(0.0005f);

        try
        {
            bankXAccountService.setBankZEndOfDayTransactionsFilePath("eodTransactions/BankZEodReconTransactions.json");
            bankXAccountService.onboardNewCustomer(accountHolder,1600f,3000f,123456l,246810l);
            bankXAccountService.onboardNewCustomer(accountHolderOne,1500f,1200f,123457l,246811l);

            // unboarded customer and non existant account id
            transactions.add(new Transaction(true,true,700f,true,33333l,123456l,""));
            transactions.add(new Transaction(true,false,800f,true,11111l,5656764l,""));
            result = bankXAccountService.doBankZImmidiateTransactions(transactions);
            assertEquals(result,false);

            transactions = new ArrayList<>();  // valid onboarded customer data
            transactions.add(new Transaction(true,true,700f,true,11111l,123456l,""));
            transactions.add(new Transaction(true,false,800f,true,22222l,246811l,""));
            result = bankXAccountService.doBankZImmidiateTransactions(transactions);
            assertEquals(result,true);
        }
        catch(Exception e)
        {
            assertEquals(e.getMessage(),""); // fail the test - not expecting any exceptions
        }
    }

    @DisplayName("Test BankXAccountService::doBankZEodTransactions")
    @Test
    void testDoBankZEodTransactions() {

        boolean result;

        AccountHolder accountHolder = new AccountHolder(11111l,"Tom","Jones",
                "11111","TestAddress","0728297019",
                "Test@email.com");

        AccountHolder accountHolderOne = new AccountHolder(22222l,"Tim","Cat",
                "22222","Test1Address","0728297015",
                "Test1@email.com");

        AccountHolder accountHolderTwo = new AccountHolder(33333l,"Jim","White",
                "33333","Test3Address","0724297015",
                "Test3@email.com");

        BankXAccountService bankXAccountService = new BankXAccountService();
        bankXAccountService.setSavingsAccountJoiningBonus(400f);
        bankXAccountService.setSavingsAccountCreditReward(0.005f);
        bankXAccountService.setTransactionalAccountPaymentsFee(0.0005f);
        try
        {
            System.out.println("Directory of tests is: "+ System.getProperty("user.dir"));
            bankXAccountService.onboardNewCustomer(accountHolder,1600f,3000f,123456l,246810l);
            bankXAccountService.onboardNewCustomer(accountHolderOne,1500f,1200f,123457l,246811l);
            bankXAccountService.onboardNewCustomer(accountHolderTwo,1500f,1200f,123458l,246812l);
            // file with customer and account ids that dont exist on the system.
            bankXAccountService.setBankZEndOfDayTransactionsFilePath("eodTransactions/BankZEodReconTransactionsUnboardedData.json");
            result = bankXAccountService.doBankZEodTransactions();
            assertEquals(result,false);

            // file with customers and account id that exist on the system.
            bankXAccountService.setBankZEndOfDayTransactionsFilePath("eodTransactions/BankZEodReconTransactions.json");
            result = bankXAccountService.doBankZEodTransactions();
            assertEquals(result,true);
        }
        catch(Exception e)
        {
            assertEquals(e.getMessage(),""); // fail the test - not expecting any exceptions
        }
    }

}


