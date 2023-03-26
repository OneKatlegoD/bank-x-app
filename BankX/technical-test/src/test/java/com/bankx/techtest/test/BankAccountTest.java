package com.bankx.techtest.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bankx.techtest.Domain.Account.BankAccount;
import com.bankx.techtest.Domain.AccountHolder;
import com.bankx.techtest.Constants.Constants;
import com.bankx.techtest.Domain.Transaction.Transaction;

public class BankAccountTest {

    @DisplayName("Test BankAccount:: null inputs test")
    @Test
    void testNullInput() {

        BankAccount bankAccount = new BankAccount();
        try {
            bankAccount.setAccountType(null);
        } catch (Exception ex) {
            assertEquals(ex.getMessage().compareTo("BankAccount:: setAccountType - received null input"),0);
        }

        try {
            bankAccount.setAccountHolder(null);
        } catch (Exception ex) {
            assertEquals(ex.getMessage().compareTo("BankAccount:: setAccountHolder - received null input"),0);
        }
    }

    @DisplayName("Test BankAccount::BankAccount - creation")
    @Test
    void testBankAccountCreation() {
        AccountHolder accountHolder = new AccountHolder(1l,"Tom","Jones",
                                "123456789","TestAddress","123456789",
                                "Test@email.com");

        try
        {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountHolder(accountHolder);
            bankAccount.setAccountId(22222l);
            bankAccount.setBalance(400f);
            bankAccount.setAccountType(Constants.SAVINGS_ACCOUNT);

            assertEquals(bankAccount.getAccountHolder().getAccountHolderId(),1l);
            assertEquals(bankAccount.getAccountHolder().getName(),"Tom");
            assertEquals(bankAccount.getAccountHolder().getSurname(),"Jones");
            assertEquals(bankAccount.getAccountHolder().getIdNumber(),"123456789");
            assertEquals(bankAccount.getAccountHolder().getAddress(),"TestAddress");
            assertEquals(bankAccount.getAccountHolder().getContactNumber(),"123456789");
            assertEquals(bankAccount.getAccountHolder().getEmailAddress(),"Test@email.com");

            assertEquals(bankAccount.getBalance(),400f);
            assertEquals(bankAccount.getAccountId(),22222l);
            assertEquals(bankAccount.getAccountType(),Constants.SAVINGS_ACCOUNT);
            assertEquals(bankAccount.getPendingAccountAdjustment(),0f);
        }
        catch(Exception e)
        {
            System.out.println("testBankAccountCreation threw exception: "+e.getMessage());
            assertEquals(e.getMessage(),""); // fail the test
        }


    }

    @DisplayName("Test BankAccount::BankAccount - general methods")
    @Test
    void testGeneralMethods() {
        AccountHolder accountHolder = new AccountHolder(1l,"Tom","Jones",
                "123456789","TestAddress","123456789",
                "Test@email.com");

        try
        {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountHolder(accountHolder);
            bankAccount.setAccountId(22222l);
            bankAccount.setBalance(400f);
            bankAccount.setAccountType(Constants.SAVINGS_ACCOUNT);


            bankAccount.setPendingAccountAdjustment(300f);
            assertEquals(bankAccount.getPendingAccountAdjustment(),300f);
            bankAccount.doPendingAdjustments();
            assertEquals(bankAccount.getBalance(),700f);
        }
        catch(Exception e)
        {
            System.out.println("testGeneralMethods threww exception: "+e.getMessage());
            assertEquals(e.getMessage(),""); // fail the test
        }

    }

    @DisplayName("Test BankAccount::BankAccount - doDebit method")
    @Test
    void testDoDebit() {
        AccountHolder accountHolder = new AccountHolder(12345l,"Tom","Jones",
                "123456789","TestAddress","123456789",
                "Test@email.com");

        try
        {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountHolder(accountHolder);
            bankAccount.setAccountId(22222l);
            bankAccount.setBalance(400f);
            bankAccount.setAccountType(Constants.SAVINGS_ACCOUNT);


            Transaction transaction = bankAccount.doDebit(50.50f,true);
            assertEquals(transaction.isImmediate(),true);
            assertEquals(transaction.isCredit(),false);
            assertEquals(transaction.getAmount(),50.50f);
            assertEquals(transaction.isSucessful(),true);
            assertEquals(transaction.getAccountHolderId(),12345l);
            assertEquals(transaction.getAccountId(),22222l);

            Transaction transactionTwo = bankAccount.doDebit(5000f,true);
            assertEquals(transactionTwo.isSucessful(),false);

        }
        catch(Exception e)
        {
            System.out.println("testDoDebit threww exception: "+e.getMessage());
            assertEquals(e.getMessage(),""); // fail the test
        }

    }

    @DisplayName("Test BankAccount::BankAccount - doCredit method")
    @Test
    void testDoCredit() {
        AccountHolder accountHolder = new AccountHolder(345456l,"Tom","Jones",
                "123456789","TestAddress","123456789",
                "Test@email.com");

        try {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountHolder(accountHolder);
        bankAccount.setAccountId(657678l);
        bankAccount.setBalance(400f);
        bankAccount.setAccountType(Constants.SAVINGS_ACCOUNT);

        Transaction transaction = bankAccount.doCredit(350.40f,true);
        assertEquals(transaction.isImmediate(),true);
        assertEquals(transaction.isCredit(),true);
        assertEquals(transaction.getAmount(),350.40f);
        assertEquals(transaction.isSucessful(),true);
        assertEquals(transaction.getAccountHolderId(),345456l);
        assertEquals(transaction.getAccountId(),657678l);

    }
        catch(Exception e)
    {
        System.out.println("testDoCredit threww exception: "+e.getMessage());
        assertEquals(e.getMessage(),""); // fail the test
    }

    }

    @DisplayName("Test BankAccount::BankAccount - getTransactionHistory method")
    @Test
    void testGetTransactionHistory() {
        AccountHolder accountHolder = new AccountHolder(1l,"Tom","Jones",
                "123456789","TestAddress","123456789",
                "Test@email.com");

        try {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountHolder(accountHolder);
            bankAccount.setAccountId(22222l);
            bankAccount.setBalance(400f);
            bankAccount.setAccountType(Constants.SAVINGS_ACCOUNT);

            bankAccount.doCredit(350.40f, true);
            bankAccount.doCredit(200.40f, false);
            bankAccount.doDebit(120.30f, true);
            bankAccount.doDebit(50.00f, true);

            assertEquals(bankAccount.getTransactionHistory().size(), 4);   // four transactions
            assertEquals(bankAccount.getTransactionHistory().get(0).getAmount(), 350.40f);
            assertEquals(bankAccount.getTransactionHistory().get(1).getAmount(), 200.40f);
            assertEquals(bankAccount.getTransactionHistory().get(2).getAmount(), 120.30f);
            assertEquals(bankAccount.getTransactionHistory().get(3).getAmount(), 50.00f);
        }
           catch(Exception e)
        {
            System.out.println("testGetTransactionHistory threww exception: "+e.getMessage());
            assertEquals(e.getMessage(),""); // fail the test
        }

    }

}
