package com.bankx.techtest.Domain.Account;

import com.bankx.techtest.Constants.Constants;
import com.bankx.techtest.Domain.AccountHolder;
import com.bankx.techtest.Domain.Transaction.Transaction;


public class BankXSavingsAccount extends BankAccount {

    private float joiningBonus;
    private float creditReward;
    private boolean joiningBonusAdded;

    public BankXSavingsAccount()
    {
        super();
        this.joiningBonus = 0;
        this.creditReward = 0;
        this.joiningBonusAdded = false;

    }

    public void init() throws Exception
    {
        try
        {
            super.setAccountType(Constants.SAVINGS_ACCOUNT);
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public void setJoiningBonus(float joiningBonus)
    {
        this.joiningBonus = joiningBonus;
        if(!joiningBonusAdded)
        {
            super.setBalance(super.getBalance() + joiningBonus);
            this.joiningBonusAdded = true;
        }
    }

    public float getJoiningBonus() {
        return joiningBonus;
    }

    public float getCreditReward() {
        return creditReward;
    }

    public void setCreditReward(float creditReward) {

        this.creditReward = creditReward;
    }

    @Override public Transaction doCredit(float amount, boolean immediate)
    {
            float finalAmmout = amount + (super.balance * this.creditReward);
            return super.doCredit(finalAmmout, immediate);
    }
}
