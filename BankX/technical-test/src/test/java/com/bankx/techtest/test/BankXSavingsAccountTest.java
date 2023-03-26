package com.bankx.techtest.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bankx.techtest.Domain.Account.BankXSavingsAccount;
import com.bankx.techtest.Domain.AccountHolder;
import com.bankx.techtest.Constants.Constants;
import com.bankx.techtest.Domain.Transaction.Transaction;

public class BankXSavingsAccountTest {

    @DisplayName("Test BankXSavingsAccount::BankXSavingsAccount - creation")
    @Test
    void testBankXSavingsAccountCreation() {
        AccountHolder accountHolder = new AccountHolder(1l,"Tom","Jones",
                "123456789","TestAddress","123456789",
                "Test@email.com");

        try
        {
            BankXSavingsAccount bankXSavingsAccount = new BankXSavingsAccount();
            bankXSavingsAccount.init();
            bankXSavingsAccount.setAccountHolder(accountHolder);
            bankXSavingsAccount.setAccountId(22222l);
            bankXSavingsAccount.setBalance(50f);
            bankXSavingsAccount.setJoiningBonus(400f);
            bankXSavingsAccount.setCreditReward(0.005f);

            assertEquals(bankXSavingsAccount.getBalance(),450f);
            assertEquals(bankXSavingsAccount.getAccountType(),Constants.SAVINGS_ACCOUNT);
        }
        catch(Exception e)
        {
            System.out.println("testBankXSavingsAccountCreation threww exception: "+e.getMessage());
            assertEquals(e.getMessage(),""); // fail the test
        }
    }

    @DisplayName("Test BankXSavingsAccount:: general methods")
    @Test
    void testBankXSavingsAccountMethods() {
        AccountHolder accountHolder = new AccountHolder(1l,"Tom","Jones",
                "123456789","TestAddress","123456789",
                "Test@email.com");

        try
        {
            BankXSavingsAccount bankXSavingsAccount = new BankXSavingsAccount();
            bankXSavingsAccount.setAccountHolder(accountHolder);
            bankXSavingsAccount.setAccountId(22222l);
            bankXSavingsAccount.init();
            bankXSavingsAccount.setJoiningBonus(700f);
            bankXSavingsAccount.setCreditReward(0.05f);

            assertEquals(bankXSavingsAccount.getJoiningBonus(),700f);
            assertEquals(bankXSavingsAccount.getCreditReward(),0.05f);
        }
        catch(Exception e)
        {
            System.out.println("testBankXSavingsAccountMethods threw exception: "+e.getMessage());
            assertEquals(e.getMessage(),"No exceptions"); // fail the test
        }

    }

    @DisplayName("Test BankXSavingsAccount::doCredit method")
    @Test
    void testDoCredit() {
        AccountHolder accountHolder = new AccountHolder(1l,"Tom","Jones",
                "123456789","TestAddress","123456789",
                "Test@email.com");

        try
        {
            BankXSavingsAccount bankXSavingsAccount = new BankXSavingsAccount();
            bankXSavingsAccount.setAccountHolder(accountHolder);
            bankXSavingsAccount.setAccountId(22222l);
            bankXSavingsAccount.init();
            bankXSavingsAccount.setJoiningBonus(400f);
            bankXSavingsAccount.setCreditReward(0.005f);


            Transaction transaction = bankXSavingsAccount.doCredit(1000f,true);
            assertEquals(transaction.isImmediate(),true);
            assertEquals(transaction.isCredit(),true);
            assertEquals(transaction.getAmount(),1002f);
            assertEquals(transaction.isSucessful(),true);

        }
        catch(Exception e)
        {
            System.out.println("testDoCredit threww exception: "+e.getMessage());
            assertEquals(e.getMessage(),"No exception"); // fail the test
        }

    }

}
