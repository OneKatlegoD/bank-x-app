package com.bankx.techtest.Domain.Account;

import com.bankx.techtest.Constants.Constants;
import com.bankx.techtest.Domain.AccountHolder;
import com.bankx.techtest.Domain.Transaction.Transaction;


public class BankXCurrentAccount extends BankAccount implements BankXTransactionalAccount
{

    private float paymentsFee;

    public BankXCurrentAccount()
    {
            super();
            this.paymentsFee = 0f;
    }

    public void init() throws Exception
    {
        try
        {
            super.setAccountType(Constants.CURRENT_ACCOUNT);
        }
        catch (Exception e)
        {
            throw e;
        }

    }


    public Transaction doTransactionalPayment(float amount)
    {
            float fee = paymentsFee * amount;
            return super.doDebit(fee + amount, true);

    }

    public float getPaymentsFee() {
        return paymentsFee;
    }

    public void setPaymentsFee(float paymentsFee) {
        this.paymentsFee = paymentsFee;
    }
}
