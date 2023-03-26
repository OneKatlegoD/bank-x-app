package com.bankx.techtest.Domain.Account;

import com.bankx.techtest.Domain.Transaction.Transaction;

public class LinkedAccounts {

    Long accountHolderId;
    BankXSavingsAccount savingsAccount;
    BankXCurrentAccount currentAccount;

    public LinkedAccounts(Long accountHolderId, BankXSavingsAccount savingsAccount,
                          BankXCurrentAccount currentAccount) throws Exception
    {
        if(savingsAccount == null || currentAccount == null)
            throw new Exception("LinkedAccounts::LInkedAccounts - received null input");

        this.accountHolderId = accountHolderId;
        this.savingsAccount = savingsAccount;
        this.currentAccount = currentAccount;
    }

    public Long getAccountHolderId() {
        return accountHolderId;
    }

    public void setAccountHolderId(Long accountHolderId) {
        this.accountHolderId = accountHolderId;
    }

    public BankXSavingsAccount getSavingsAccount() {
        return savingsAccount;
    }

    public void setSavingsAccount(BankXSavingsAccount savingsAccount) {
        this.savingsAccount = savingsAccount;
    }

    public BankXCurrentAccount getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(BankXCurrentAccount currentAccount) {
        this.currentAccount = currentAccount;
    }

    public String transferToCurrent(float amount)
    {
        Transaction transactionOne = savingsAccount.doDebit(amount,true);
        Transaction transactionTwo = null;
        if(transactionOne.isSucessful())
            transactionTwo = currentAccount.doCredit(amount,true);

        String nofification = transactionOne.toString();
        if(transactionTwo != null)
        {
            nofification += "\r\n";
            nofification += transactionTwo.toString();
        }

        return nofification;
    }

    public String transferToSavings(float amount)
    {
        Transaction transactionOne = currentAccount.doDebit(amount,true);
        Transaction transactionTwo = null;
        if(transactionOne.isSucessful())
            transactionTwo = savingsAccount.doCredit(amount,true);

        String nofification = transactionOne.toString();
        if(transactionTwo != null)
        {
            nofification += "\r\n";
            nofification += transactionTwo.toString();
        }

        return nofification;
    }

    public String doCustomerPayment(float amount)
    {
        String notification = currentAccount.doTransactionalPayment(amount).toString();
        return notification;
    }
}