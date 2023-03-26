package com.bankx.techtest.Domain.Transaction;

import java.util.List;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Transaction {

    private Boolean immediate; // true - yes, false - no
    private Boolean credit;    // true - credit, false - debit
    private float amount;
    private Boolean successful;
    private LocalDateTime dateTime;
    private  DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private Long accountHolderId;
    private Long accountId;
    private String onAccount;

    public Transaction()
    {
        this.dateTime = LocalDateTime.now();
    }

    public Transaction(Boolean immediate, Boolean credit, float amount, Boolean successful,
                       Long accountHolderId, Long accountId,String onAccount)
    {
        this.immediate = immediate;
        this.credit = credit;
        this.amount = amount;
        this.successful = successful;
        this.accountHolderId = accountHolderId;
        this.accountId = accountId;
        this.onAccount = onAccount;
        this.dateTime = LocalDateTime.now();
    }

    public Boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(Boolean immediate) {
        this.immediate = immediate;
    }

    public Boolean isCredit() {
        return credit;
    }

    public void setCredit(Boolean credit) {
        this.credit = credit;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Boolean isSucessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDateTimeAsString()
    {
        return dateTimeFormatter.format(dateTime);
    }

    public Long getAccountHolderId() {
        return accountHolderId;
    }

    public void setAccountHolderId(Long accountHolderId) {
        this.accountHolderId = accountHolderId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getOnAccount() {
        return onAccount;
    }

    public void setOnAccount(String onAccount) {
        this.onAccount = onAccount;
    }

    public String toString()
    {
        String transactionType = "";
        String immediate = "";
        String sucessful = "";

        if(successful)
            sucessful = ". Status: sucessful";
        else
            sucessful = ". Status: unsucessful";

        if(this.credit)
            transactionType = " credit ";
        else
            transactionType = " debit ";

        if(this.immediate)
            immediate = ". Immidiate transaction";

        return "BankX. " + this.amount + transactionType + this.onAccount + immediate + sucessful;
    }


}