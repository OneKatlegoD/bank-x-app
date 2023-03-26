package com.bankx.techtest.Domain.Account;

import com.bankx.techtest.Domain.Transaction.Transaction;
import com.bankx.techtest.Domain.AccountHolder;
import java.lang.Exception;

public interface BankXTransactionalAccount
{
    public Transaction doTransactionalPayment(float amount);
}