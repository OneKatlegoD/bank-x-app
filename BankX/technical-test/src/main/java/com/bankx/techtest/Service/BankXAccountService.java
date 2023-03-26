package com.bankx.techtest.Service;

import java.util.List;
import java.util.ArrayList;
import java.lang.Exception;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.bankx.techtest.Domain.Account.BankXTransactionalAccount;
import com.bankx.techtest.Domain.Account.BankXSavingsAccount;
import com.bankx.techtest.Domain.Account.BankXCurrentAccount;
import com.bankx.techtest.Domain.Account.LinkedAccounts;
import com.bankx.techtest.Domain.AccountHolder;
import com.bankx.techtest.Domain.Transaction.Transaction;
import com.bankx.techtest.Constants.Constants;


public class BankXAccountService implements AccountService {

    private List<AccountHolder> customerList;
    private List<LinkedAccounts> linkedAccountsList;
    private float savingsAccountJoiningBonus;
    private float savingsAccountCreditReward;
    private float transactionalAccountPaymentsFee;
    private String bankZEndOfDayTransactionsFilePath;
    private BlockingQueue<String> customerNotifications;
    private CustomerNotificationService customerNotificationService;
    private BankXSavingsAccount destinationSavingsAccount;
    private BankXCurrentAccount destinationCurrentAccount;

    public BankXAccountService()
    {
        this.customerList = new ArrayList<>();
        this.linkedAccountsList = new ArrayList<>();
        this.savingsAccountJoiningBonus = 0f;
        this.savingsAccountCreditReward = 0f;
        this.transactionalAccountPaymentsFee = 0f;
        this.bankZEndOfDayTransactionsFilePath = "";
        this.customerNotifications = new LinkedBlockingQueue<>();
        this.customerNotificationService = new CustomerNotificationService(this.customerNotifications);
        this.customerNotificationService.startRunning();
        this.destinationSavingsAccount = null;
        this.destinationCurrentAccount = null;
    }

    public List<AccountHolder> getCustomerList() {
        return customerList;
    }

    public List<LinkedAccounts> getLinkedAccountsList() {
        return linkedAccountsList;
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

    public void setBankZEndOfDayTransactionsFilePath(String bankZEndOfDayTransactionsFilePath) throws Exception
    {
        if(bankZEndOfDayTransactionsFilePath == null || bankZEndOfDayTransactionsFilePath.isEmpty())
            throw new Exception("BankXAccountService::setBankZEndOfDayTransactionsFilePath - received null or empty input");

        this.bankZEndOfDayTransactionsFilePath = bankZEndOfDayTransactionsFilePath;
    }

    public String onboardNewCustomer(AccountHolder accountHolder, float savingsAccountOpeningBalance,
                                   float currentAccountOpeningBalance, Long savingsAccId, Long currentAccId) throws Exception
    {
        String result = "";

        if(accountHolder == null || savingsAccId == null || currentAccId == null)
            throw new Exception("BankXAccountService::onboardNewCustomer received null input");

        if(
                (findCustomer(accountHolder.getAccountHolderId()) != null) ||
                (findDestinationAccount(savingsAccId) != false) ||
                (findDestinationAccount(currentAccId) != false)
          )
            throw new Exception("BankXAccountService::onboardNewCustomer - Cannot onboard new customer with account id's that have been already allocated, or a customer id that already exists on the system");

        try
        {
            BankXSavingsAccount savingsAcc = new BankXSavingsAccount();
            savingsAcc.init();
            savingsAcc.setAccountHolder(accountHolder);
            savingsAcc.setAccountId(savingsAccId);
            savingsAcc.setBalance(savingsAccountOpeningBalance);
            savingsAcc.setJoiningBonus(savingsAccountJoiningBonus);
            savingsAcc.setCreditReward(savingsAccountCreditReward);

            BankXCurrentAccount currentAcc = new BankXCurrentAccount();
            currentAcc.init();
            currentAcc.setAccountHolder(accountHolder);
            currentAcc.setAccountId(currentAccId);
            currentAcc.setBalance(currentAccountOpeningBalance);
            currentAcc.setPaymentsFee(transactionalAccountPaymentsFee);

            LinkedAccounts linkedAccounts = new LinkedAccounts(accountHolder.getAccountHolderId(),
                    savingsAcc, currentAcc);

            linkedAccountsList.add(linkedAccounts);
            customerList.add(accountHolder);

            result = "\r\nNew customer onboarded.\r\nCustomer id="+accountHolder.getAccountHolderId()+".\r\nsavingsAccount number="+savingsAcc.getAccountId()+". balance="+savingsAcc.getBalance()+
                    ".\r\ncurrentAccount number="+currentAcc.getAccountId()+". balance="+currentAcc.getBalance()+"\r\n";
				
        }
        catch (Exception e)
        {
            throw new Exception("BankXAccountService::onboardNewCustomer failed - Internal processing exception");
        }

        return result;

    }

    public boolean doInterAccountTransfer(AccountHolder accountHolder, String fromAccount,float amount)
    {
        try
        {
            LinkedAccounts customerLinkedAccounts = findCustomerAccounts(accountHolder.getAccountHolderId());
            if(fromAccount.compareTo(Constants.SAVINGS_ACCOUNT) == 0)
                customerNotifications.put(customerLinkedAccounts.transferToCurrent(amount));
            else if(fromAccount.compareTo(Constants.CURRENT_ACCOUNT) == 0)
                customerNotifications.put(customerLinkedAccounts.transferToSavings(amount));
            else
            {
                System.out.println("Received undefined account type: "+ fromAccount);
                return false;
            }

        }
        catch (Exception e)
        {
            System.out.println("Failed to find linked accounts for customer with id: " + accountHolder.getAccountHolderId());
            return false;
        }

        return true;
    }
    public boolean makePayment(AccountHolder accountHolder, float amount, Long destinationAccountId)
    {
        String notification;
        BankXCurrentAccount sourceCurrentAccount;
        try
        {
            LinkedAccounts customerLinkedAccounts = findCustomerAccounts(accountHolder.getAccountHolderId());
            if(customerLinkedAccounts == null)
            {
                System.out.println("specified customer account does not exist");
                return false;
            }
            sourceCurrentAccount = customerLinkedAccounts.getCurrentAccount();

            if(!findDestinationAccount(destinationAccountId))
            {
                System.out.println("specified destination account does not exist");
                return false;   // specified destination account does not exist
            }

            if(destinationSavingsAccount != null && destinationCurrentAccount != null)
            {
                System.out.println("internal error. Two accounts with same accountId.");
                return false;   // internal error. Two accounts with same accountId. Can only happen if the accountId generator on customer onboarding issues the same id to two accounts
            }

            Transaction transaction = sourceCurrentAccount.doTransactionalPayment(amount);
            notification = transaction.toString();
            customerNotifications.put(notification);

            if(transaction.isSucessful())  // then credit the destination account
            {
                if(destinationSavingsAccount != null) // destination account is savings
                {
                    notification = destinationSavingsAccount.doCredit(amount,true).toString();
                    customerNotifications.put(notification);
                }

                if(destinationCurrentAccount != null)
                {
                    notification = destinationCurrentAccount.doCredit(amount,true).toString();
                    customerNotifications.put(notification);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed to find source account for customer with id: " + accountHolder.getAccountHolderId());
            return false;
        }
        return true;
    }

    public boolean findDestinationAccount(Long destinationAccountId)
    {
        destinationSavingsAccount = null;
        destinationCurrentAccount = null;

        for(LinkedAccounts customerLinkedAccounts: linkedAccountsList)
        {
            if(customerLinkedAccounts.getSavingsAccount().getAccountId().equals(destinationAccountId))
            {
                destinationSavingsAccount = customerLinkedAccounts.getSavingsAccount();
                return true;
            }
            else if(customerLinkedAccounts.getCurrentAccount().getAccountId().equals(destinationAccountId))
            {
                destinationCurrentAccount = customerLinkedAccounts.getCurrentAccount();
                return true;
            }
        }

        return false;
    }

    public List<Transaction> provideCustomerTransactionHistory(AccountHolder accountHolder, String accountType)
    {
        List<Transaction> transactions = new ArrayList<>();
        try
        {
            LinkedAccounts customerLinkedAccounts = findCustomerAccounts(accountHolder.getAccountHolderId());
            if(customerLinkedAccounts == null)
                return null; // searching for history of customer that does not exits.
            if(accountType.compareTo(Constants.SAVINGS_ACCOUNT) == 0)
                transactions = customerLinkedAccounts.getSavingsAccount().getTransactionHistory();
            else if(accountType.compareTo(Constants.CURRENT_ACCOUNT) == 0)
                transactions = customerLinkedAccounts.getCurrentAccount().getTransactionHistory();
            else
            {
                return null; // searching for transactions on undefined account type
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return transactions;
    }

    public boolean doBankZImmidiateTransactions(List<Transaction> transactions)
    {
        boolean result = false;
        try
        {
            result = processTransactions(transactions);
        }
        catch (Exception e)
        {
            return false;
        }
        return result;
    }


    public boolean doBankZEodTransactions()
    {
        List<Transaction> eodTransactions = new ArrayList<>();
        boolean result = false;

        try
        {
            eodTransactions = loadEodTransactions();
            result = processTransactions(eodTransactions);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
			return false;
        }
        return result;
    }

    private boolean processTransactions(List<Transaction> transactions)
    {
        for(Transaction transaction: transactions)
        {
            try
            {
                String notification = "";
                LinkedAccounts customerLinkedAccounts = findCustomerAccounts(transaction.getAccountHolderId());
                if (customerLinkedAccounts == null)
                    return false;

                if (transaction.getAccountId().equals(customerLinkedAccounts.getSavingsAccount().getAccountId()))
                {
                    if (transaction.isCredit())
                        notification = customerLinkedAccounts.getSavingsAccount().doCredit(transaction.getAmount(),transaction.isImmediate()).toString();
                    else
                        notification = customerLinkedAccounts.getSavingsAccount().doDebit(transaction.getAmount(),transaction.isImmediate()).toString();

                    customerNotifications.put(notification);
                }
                else if (transaction.getAccountId().equals(customerLinkedAccounts.getCurrentAccount().getAccountId()))
                {
                    if (transaction.isCredit())
                        notification = customerLinkedAccounts.getCurrentAccount().doCredit(transaction.getAmount(), transaction.isImmediate()).toString();
                    else
                        notification = customerLinkedAccounts.getCurrentAccount().doDebit(transaction.getAmount(), transaction.isImmediate()).toString();

                    customerNotifications.put(notification);
                }
                else
                    return false;

            }
            catch(Exception e)
            {
                return false;
            }
        }

        return true;
    }

    public AccountHolder findCustomer(Long accountHolderId)
    {
        for(AccountHolder accountHolder: customerList)
        {
            if(accountHolder.getAccountHolderId().equals(accountHolderId))
                return accountHolder;
        }
        return null;
    }

    public LinkedAccounts findCustomerAccounts(Long accountHolderId)
    {
		
		for(LinkedAccounts customerLinkedAccounts: linkedAccountsList)
		{
			if(customerLinkedAccounts.getAccountHolderId().equals(accountHolderId))
				return customerLinkedAccounts;
		}
        return null;
    }

    private List<Transaction> loadEodTransactions() throws Exception
    {
        if(this.bankZEndOfDayTransactionsFilePath == null || this.bankZEndOfDayTransactionsFilePath.isEmpty())
            throw new Exception("BankXAccountService::loadEodTransactions - received null or empty eod file path string");

        List<Transaction> eofReconTransactions = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(this.bankZEndOfDayTransactionsFilePath))
        {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray eodReconTrans = (JSONArray) jsonObject.get("transactions");
            for(Object transaction: eodReconTrans)
            {
                Transaction trans = objectMapper.convertValue(transaction, Transaction.class);
                eofReconTransactions.add(trans);
            }
        } catch (Exception e) {
            throw new Exception("BankXAccountService::loadEodTransactions() - exception reading in eod recon file: "+e.getMessage());
        }

        return eofReconTransactions;

    }
}
