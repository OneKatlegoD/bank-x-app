package com.bankx.techtest.Configuration;

public class BankAccountServiceConfiguration {

    public float savingsAccountJoiningBonus;
    public float savingsAccountCreditReward;
    public float transactionalAccountPaymentsFee;
    public String bankZEndOfDayTransactionsFilePath;

    public BankAccountServiceConfiguration()
    {
        this.savingsAccountJoiningBonus = 0;
        this.savingsAccountCreditReward = 0;
        this.transactionalAccountPaymentsFee = 0;
        this.bankZEndOfDayTransactionsFilePath = "";
    }

    public float getSavingsAccountJoiningBonus() {
        return savingsAccountJoiningBonus;
    }

    public void setSavingsAccountJoiningBonus(float savingsAccountJoiningBonus) {
        this.savingsAccountJoiningBonus = savingsAccountJoiningBonus;
    }

    public float getSavingsAccountCreditReward() {
        return savingsAccountCreditReward;
    }

    public void setSavingsAccountCreditReward(float savingsAccountCreditReward) {
        this.savingsAccountCreditReward = savingsAccountCreditReward;
    }

    public float getTransactionalAccountPaymentsFee() {
        return transactionalAccountPaymentsFee;
    }

    public void setTransactionalAccountPaymentsFee(float transactionalAccountPaymentsFee) {
        this.transactionalAccountPaymentsFee = transactionalAccountPaymentsFee;
    }

    public String getBankZEndOfDayTransactionsFilePath() {
        return bankZEndOfDayTransactionsFilePath;
    }

    public void setBankZEndOfDayTransactionsFilePath(String bankZEndOfDayTransactionsFilePath) {
        this.bankZEndOfDayTransactionsFilePath = bankZEndOfDayTransactionsFilePath;
    }
}
