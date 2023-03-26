package com.bankx.techtest.Domain.Account;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.lang.Exception;

import com.bankx.techtest.Domain.Transaction.Transaction;
import com.bankx.techtest.Domain.AccountHolder;

public class BankAccount  {

    protected AccountHolder accountHolder;
    protected Long accountId;
    protected float balance;
    protected float pendingAccountAdjustment;
    protected List<Transaction> transactionHistory;
    protected String accountType;

    public BankAccount()
    {
        this.pendingAccountAdjustment = 0;
        this.balance = 0;
        this.accountId = 0l;
        this.transactionHistory = new ArrayList<>();
        this.accountHolder = new AccountHolder();
    }
    public void setAccountHolder(AccountHolder accountHolder) throws Exception {

        if(accountHolder == null)
            throw new Exception("BankAccount:: setAccountHolder - received null input");

        this.accountHolder = accountHolder;
    }
    public AccountHolder getAccountHolder() {
        return accountHolder;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance)
    {
        this.balance = balance;
    }

    public float getPendingAccountAdjustment() {
        return pendingAccountAdjustment;
    }

    public void setPendingAccountAdjustment(float pendingAccountAdjustment)
    {
        this.pendingAccountAdjustment = pendingAccountAdjustment;
    }

    public List<Transaction> getTransactionHistory()
    {
        return transactionHistory;
    }

    public String getAccountType() {
        return accountType;
    }
    public void setAccountType(String accountType) throws Exception
    {
        if(accountType == null)
            throw new Exception("BankAccount:: setAccountType - received null input");
      this.accountType = accountType;
    }

    public Transaction doDebit(float amount, boolean immediate)
    {
        Transaction transaction;
        String onAccountInfo;
        if(amount <= this.balance)
        {
            if(immediate)
                this.balance -= amount;
            else
                this.pendingAccountAdjustment += amount;

            onAccountInfo = this.accountType+":"+this.accountId+". New balance: "+ this.balance;

            transaction = new Transaction(immediate,false,amount,true,
                    this.accountHolder.getAccountHolderId(), this.accountId, onAccountInfo);

        }
        else
        {
            onAccountInfo = this.accountType+":"+this.accountId;

            transaction = new Transaction(immediate,false,amount,false,
                    this.accountHolder.getAccountHolderId(), this.accountId,onAccountInfo);
        }

        this.transactionHistory.add(transaction);

        return transaction;
    }

    public Transaction doCredit(float amount, boolean immediate)
    {
        if(immediate)
            this.balance += amount;
        else
            this.pendingAccountAdjustment += amount;

        String onAccountInfo = this.accountType+":"+this.accountId+". New balance: "+ this.balance;

        Transaction transaction = new Transaction(immediate,true,amount,true,
                this.accountHolder.getAccountHolderId(), this.accountId,onAccountInfo);

        this.transactionHistory.add(transaction);

        return transaction;
    }

    public void doPendingAdjustments()
    {
        this.balance += this.pendingAccountAdjustment;
    }




}
