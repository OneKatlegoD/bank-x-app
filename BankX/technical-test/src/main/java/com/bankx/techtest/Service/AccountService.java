package com.bankx.techtest.Service;

import java.util.List;

import com.bankx.techtest.Domain.Transaction.Transaction;
import com.bankx.techtest.Domain.AccountHolder;
import com.bankx.techtest.Domain.Account.LinkedAccounts;

public interface AccountService {

    public String onboardNewCustomer( AccountHolder accountHolder, float savingsAccountOpeningBalance,
                                    float currentAccountOpeningBalance, Long savingsAccId, Long currentAccId) throws Exception;
    public AccountHolder findCustomer(Long accountHolderId);
    public LinkedAccounts findCustomerAccounts(Long accountHolderId);
    public boolean doInterAccountTransfer(AccountHolder accountHolder, String fromAccount,float amount);
    public boolean makePayment(AccountHolder accountHolder, float amount, Long destinationAccountId);
    public boolean findDestinationAccount(Long destinationAccountId);
    public List<Transaction> provideCustomerTransactionHistory(AccountHolder accountHolder, String accountType);
    public boolean doBankZImmidiateTransactions(List<Transaction> transactions);
    public boolean doBankZEodTransactions();
    public void setSavingsAccountJoiningBonus(float savingsAccountJoiningBonus);
    public void setSavingsAccountCreditReward(float savingsAccountCreditReward);
    public void setTransactionalAccountPaymentsFee(float transactionalAccountPaymentsFee);
    public void setBankZEndOfDayTransactionsFilePath(String bankZEndOfDayTransactionsFilePath) throws Exception;
    public String getBankZEndOfDayTransactionsFilePath();

}
