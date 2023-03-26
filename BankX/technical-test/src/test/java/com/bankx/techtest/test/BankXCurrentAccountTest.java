package com.bankx.techtest.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bankx.techtest.Domain.Account.BankXCurrentAccount;
import com.bankx.techtest.Domain.AccountHolder;
import com.bankx.techtest.Constants.Constants;
import com.bankx.techtest.Domain.Transaction.Transaction;

public class BankXCurrentAccountTest {

    @DisplayName("Test BankXCurrentAccount::BankXCurrentAccount - creation")
    @Test
    void testBankXCurrentAccountCreation() {
        AccountHolder accountHolder = new AccountHolder(1l,"Tom","Jones",
                "123456789","TestAddress","123456789",
                "Test@email.com");

        try
        {
            BankXCurrentAccount bankXCurrentAccount = new BankXCurrentAccount();
            bankXCurrentAccount.init();
            bankXCurrentAccount.setAccountHolder(accountHolder);
            bankXCurrentAccount.setAccountId(22222l);
            bankXCurrentAccount.setBalance(100f);
            bankXCurrentAccount.setPaymentsFee(0.0005f);

            assertEquals(bankXCurrentAccount.getBalance(),100f);
            assertEquals(bankXCurrentAccount.getAccountType(),Constants.CURRENT_ACCOUNT);
        }
        catch(Exception e)
        {
            System.out.println("testBankXCurrentAccountCreation threww exception: "+e.getMessage());
            assertEquals(e.getMessage(),"No exception"); // fail the test
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
            BankXCurrentAccount bankXCurrentAccount = new BankXCurrentAccount();
            bankXCurrentAccount.init();
            bankXCurrentAccount.setAccountHolder(accountHolder);
            bankXCurrentAccount.setAccountId(22222l);
            bankXCurrentAccount.setPaymentsFee(0.0009f);

            assertEquals(bankXCurrentAccount.getPaymentsFee(),0.0009f);
        }
        catch(Exception e)
        {
            System.out.println("testBankXSavingsAccountMethods threw exception: "+e.getMessage());
            assertEquals(e.getMessage(),"No exceptions"); // fail the test
        }

    }

    @DisplayName("Test BankXSavingsAccount::doTransactionalPayment method")
    @Test
    void testDoTransactionalPayment() {
        AccountHolder accountHolder = new AccountHolder(1l,"Tom","Jones",
                "123456789","TestAddress","123456789",
                "Test@email.com");

        try
        {
            BankXCurrentAccount bankXCurrentAccount = new BankXCurrentAccount();
            bankXCurrentAccount.init();
            bankXCurrentAccount.setAccountId(22222l);
            bankXCurrentAccount.setBalance(20000f);
            bankXCurrentAccount.setAccountHolder(accountHolder);
            bankXCurrentAccount.setPaymentsFee(0.0005f);

            Transaction transaction = bankXCurrentAccount.doTransactionalPayment(10000f);
            assertEquals(transaction.isImmediate(),true);
            assertEquals(transaction.isCredit(),false);
            assertEquals(transaction.getAmount(),10005f);
            assertEquals(transaction.isSucessful(),true);
            assertEquals(bankXCurrentAccount.getBalance(),9995f);

        }
        catch(Exception e)
        {
            System.out.println("testDoTransactionalPayment threw exception: "+e.getMessage());
            assertEquals(e.getMessage(),"No exception"); // fail the test
        }

    }

}
