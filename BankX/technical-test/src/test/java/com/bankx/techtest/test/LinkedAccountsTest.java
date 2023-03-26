package com.bankx.techtest.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bankx.techtest.Domain.Account.LinkedAccounts;
import com.bankx.techtest.Domain.Account.BankXSavingsAccount;
import com.bankx.techtest.Domain.Account.BankXCurrentAccount;
import com.bankx.techtest.Domain.AccountHolder;
import com.bankx.techtest.Constants.Constants;
import com.bankx.techtest.Domain.Transaction.Transaction;

public class LinkedAccountsTest {

    @DisplayName("Test LinkedAccounts::LinkedAccounts - null inputs")
    @Test
    void testLinkedAccountsNullInput() {

        Long accountHolderId = 212345678l;

        try
        {
            LinkedAccounts linkedAccounts = new LinkedAccounts(accountHolderId,null,null);
        }
        catch(Exception e)
        {
            assertEquals(e.getMessage().compareTo("LinkedAccounts::LInkedAccounts - received null input"),0);
        }
    }


    @DisplayName("Test LinkedAccounts::LinkedAccounts - methods")
    @Test
    void testLinkedAccountsCreation() {
        AccountHolder accountHolder = new AccountHolder(55555l,"Tom","Jones",
                "123456789","TestAddress","123456789",
                "Test@email.com");
        try
        {
            BankXSavingsAccount bankXSavingsAccount = new BankXSavingsAccount();
            bankXSavingsAccount.init();
            bankXSavingsAccount.setAccountHolder(accountHolder);
            bankXSavingsAccount.setAccountId(1234l);
            bankXSavingsAccount.setBalance(500f);
            bankXSavingsAccount.setJoiningBonus(400f);
            bankXSavingsAccount.setCreditReward(0.005f);

            BankXCurrentAccount bankXCurrentAccount = new BankXCurrentAccount();
            bankXCurrentAccount.init();
            bankXCurrentAccount.setAccountHolder(accountHolder);
            bankXCurrentAccount.setAccountId(24680l);
            bankXCurrentAccount.setBalance(100f);
            bankXCurrentAccount.setPaymentsFee(0.0005f);

            LinkedAccounts linkedAccounts =
                    new LinkedAccounts(accountHolder.getAccountHolderId(),bankXSavingsAccount,bankXCurrentAccount);

            String customerNotification = linkedAccounts.transferToCurrent(150f);
            assertEquals(linkedAccounts.getSavingsAccount().getBalance(),750f);
            assertEquals(linkedAccounts.getCurrentAccount().getBalance(),250f);
            String expectedNotification = "BankX. 150.0 debit SavingsAccount:1234. New balance: 750.0. Immidiate transaction. Status: sucessful\r\nBankX. 150.0 credit CurrentAccount:24680. New balance: 250.0. Immidiate transaction. Status: sucessful";
            assertEquals(customerNotification.compareTo(expectedNotification),0);


            customerNotification = linkedAccounts.transferToSavings(100f);
            assertEquals(linkedAccounts.getSavingsAccount().getBalance(),853.75f);
            assertEquals(linkedAccounts.getCurrentAccount().getBalance(),150f);
            expectedNotification = "BankX. 100.0 debit CurrentAccount:24680. New balance: 150.0. Immidiate transaction. Status: sucessful\r\nBankX. 103.75 credit SavingsAccount:1234. New balance: 853.75. Immidiate transaction. Status: sucessful";
            assertEquals(customerNotification.compareTo(expectedNotification),0);

            customerNotification = linkedAccounts.doCustomerPayment(100f);
            assertEquals(linkedAccounts.getCurrentAccount().getBalance(),49.949997f); // need to check to round to 2 places
            expectedNotification = "BankX. 100.05 debit CurrentAccount:24680. New balance: 49.949997. Immidiate transaction. Status: sucessful";
            assertEquals(customerNotification.compareTo(expectedNotification),0);
        }
        catch(Exception e)
        {
            System.out.println("testLinkedAccountsCreation threw exception: "+e.getMessage());
            assertEquals(e.getMessage(),"No exception"); // fail the test
        }
    }

}
