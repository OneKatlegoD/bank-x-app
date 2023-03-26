package com.bankx.techtest;

import java.util.List;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Float;
import java.math.BigDecimal;
import java.io.*;

import com.bankx.techtest.Service.*;
import com.bankx.techtest.Domain.*;
import com.bankx.techtest.Constants.Constants;
import com.bankx.techtest.Domain.Account.LinkedAccounts;
import com.bankx.techtest.Domain.Transaction.Transaction;
import com.bankx.techtest.Configuration.BankAccountServiceConfiguration;

public class Main {

    static String inputLine = "";
    static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    static Long savingsAccountId = 123456l;  // starting points of accountId's. Increment with every new customer boarding
    static Long currentAccountId = 246810l;
    static AccountService bankXAccountService;

    public static void main(String[] args)
    {
        String serviceConfigFilePath="";
        BankAccountServiceConfiguration bankAccountServiceConfiguration;

        if(args.length == 0)
        {
            System.out.println("Program expects argument for configuration file location ( i.e 'config/config.json')");
            System.exit(1);
        }
        else
        {
            for(String arg: args)
            {
                serviceConfigFilePath = arg;
                break;
            }
            System.out.println("Starting BankXAccountService with configuration located in: "+ serviceConfigFilePath);
            bankAccountServiceConfiguration = loadConfiguration(serviceConfigFilePath);

            if(bankAccountServiceConfiguration == null)
            {
                System.out.println("Failed to load bankX account service configuration. Check config file");
                System.exit(1);
            }
            else
            {
                bankXAccountService = new BankXAccountService();
                bankXAccountService.setSavingsAccountJoiningBonus(bankAccountServiceConfiguration.savingsAccountJoiningBonus);
                bankXAccountService.setSavingsAccountCreditReward(bankAccountServiceConfiguration.savingsAccountCreditReward);
                bankXAccountService.setTransactionalAccountPaymentsFee(bankAccountServiceConfiguration.transactionalAccountPaymentsFee);
				try
				{
                    bankXAccountService.setBankZEndOfDayTransactionsFilePath(bankAccountServiceConfiguration.bankZEndOfDayTransactionsFilePath);
                    doUI();
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
            }

        }

    }

    public static void doUI() throws IOException
    {

        while(inputLine.compareTo("quit") != 0)
        {
            printMainMenu();
            inputLine = bufferedReader.readLine();
            switch(inputLine)
            {
                case "1":
                        String result = onboardNewCustomer();
                            if(result != null)
                                System.out.println(result);
                            else
                                System.out.println("Onboard customer failed. Please enter input in the correct format");
                        exitSubMenu();
                        break;
                case "2":
                        boolean interAccountResult = doInterAccountTranfer();
                        if(interAccountResult)
                            System.out.println("Inter account transaction submitted successfully.");
                        else
                            System.out.println("Inter account transaction submission failed. Required data was entered incorrectly or customer has not been onboarded.");
                        exitSubMenu();
                        break;
                case "3":
                        boolean paymentResult = makePaymentToOtherAccount();
                            if(paymentResult)
                                System.out.println("Payment submitted successfully.");
                            else
                                System.out.println("Payment submission failed. Required data was entered incorrectly or Source/destination customer has not been onboarded.");
                        exitSubMenu();
                        break;
                case "4":
                        boolean viewTransHistoryResult = viewTransactionHistory();
                            if(viewTransHistoryResult)
                                System.out.println("Transaction history retrieval successful.");
                            else
                                System.out.println("Transaction history retrieval failed. Required data was entered incorrectly or selected customer does not exist on BankX system.");
                        exitSubMenu();
                        break;
                case "5":
                        boolean doBankZImmidiateTransResult = doBankZImmediateTransactions();
                        if(doBankZImmidiateTransResult)
                            System.out.println("BankZ immidiate transactions successful.");
                        else
                            System.out.println("BankZ immidiate transactions un-successful. \r\n1. Ensure that transactions data is for customers that have been on-boarded on Bank X.");
                        exitSubMenu();
                        break;
                case "6":
                        boolean doBankZReconResult = doBankZEndOfDayRecon();
                            if(doBankZReconResult)
                                System.out.println("BankZ end of day recon successful.");
                            else
                                System.out.println("BankZ end of day recon un-successful.\r\n1. Ensure that recon file exists at the specified location: "+bankXAccountService.getBankZEndOfDayTransactionsFilePath()
                                                + "\r\n2. Ensure that the user account data and the account data in the recon file is for customers that have been on-boarded on Bank X.");
                        exitSubMenu();
                    break;
                case "quit":
                    System.out.println("exiting application....");
                    System.exit(1);
                    break;
                default:
                    System.out.println("Invalid selection made.");
                    break;

            }
        }

    }

    public static void printMainMenu()
    {
        System.out.println("\r\n---------------------BankX---------------------------");
        System.out.println("1. Onboard new customer\r\n2. Do inter account transfer\r\n3. Make payments to other accounts" +
                "\r\n4. View transaction history\r\n5. Do bankZ immediate transaction(s)\r\n6. Do BankZ end of day recon\r\n'quit' to exit application");
        System.out.println("\r\n---------------------BankX---------------------------");
    }

    public static void exitSubMenu()
    {
        try
        {
            System.out.println("\r\nEnter any key to go back to main menu...");
            inputLine = bufferedReader.readLine();
        }
        catch(Exception e)
        {
            System.out.println("\r\nExiting application. exitSubMenu raised exception: "+e.getMessage());
            System.exit(1);
        }

    }
    public static String onboardNewCustomer()
    {
        String result = "";
        inputLine = "";

        try
        {
            System.out.println("\r\nEnter customer data as one line in the following format and order:\r\nidNumber,name,surname,address,contactNumber,emailAddress,savingsAccountOpeningBalance,currentAccountOpeningBalance");
            inputLine = bufferedReader.readLine();

                String[] customerData = inputLine.split(",");
                AccountHolder accountHolder = new AccountHolder(Long.valueOf(customerData[0]),customerData[1],customerData[2],
                        customerData[0],customerData[3],customerData[4],
                        customerData[5]);

                result = bankXAccountService.onboardNewCustomer(accountHolder,Float.valueOf(customerData[6]),Float.valueOf(customerData[7]),savingsAccountId,currentAccountId);
        }
        catch (Exception e)
        {
            return null;
        }

        savingsAccountId += 1;
        currentAccountId += 1;
        return result;
    }

    public static boolean doInterAccountTranfer()
    {

        boolean result = false;
        inputLine = "";
        AccountHolder accountHolder;
        String fromAccount="";
        try
        {
            System.out.println("\r\nEnter account holder id \r\n");
            inputLine = bufferedReader.readLine();
            accountHolder = bankXAccountService.findCustomer(Long.valueOf(inputLine));
            if(accountHolder == null)
                return false;

            LinkedAccounts linkedAccounts = bankXAccountService.findCustomerAccounts(Long.valueOf(inputLine));
            System.out.println("\r\nSavings account balance="+linkedAccounts.getSavingsAccount().getBalance()+"\r\nCurrent account balance="+linkedAccounts.getCurrentAccount().getBalance());
            System.out.println("\r\nTransfer options:\r\n1. From savings to current account.\r\n2. From current to savings account.");
            inputLine = bufferedReader.readLine();
            if(inputLine.compareTo("1") == 0)
                fromAccount = Constants.SAVINGS_ACCOUNT;
            else if(inputLine.compareTo("2") == 0)
                fromAccount = Constants.CURRENT_ACCOUNT;
            else
                return false;

            System.out.println("\r\nEnter amount to transfer.");
            inputLine = bufferedReader.readLine();
            result = bankXAccountService.doInterAccountTransfer(accountHolder,fromAccount ,Float.valueOf(inputLine));
        }
        catch (Exception e)
        {
            return false;
        }
        return result;
    }

    public static boolean makePaymentToOtherAccount()
    {
        boolean result = false;
        inputLine = "";
        AccountHolder accountHolder;
        Long destinationAccountId=0l;
        try
        {
            System.out.println("\r\nEnter account holder id \r\n");
            inputLine = bufferedReader.readLine();
            accountHolder = bankXAccountService.findCustomer(Long.valueOf(inputLine));
            if(accountHolder == null)
                return false;

            LinkedAccounts linkedAccounts = bankXAccountService.findCustomerAccounts(Long.valueOf(inputLine));
            System.out.println("\r\nCurrent account balance="+linkedAccounts.getCurrentAccount().getBalance());

            System.out.println("\r\nEnter destination account id\r\n");
            inputLine = bufferedReader.readLine();
            destinationAccountId = Long.valueOf(inputLine);
            if(!bankXAccountService.findDestinationAccount(destinationAccountId))
                return false;

            System.out.println("\r\nEnter payment amount.");
            inputLine = bufferedReader.readLine();
            result = bankXAccountService.makePayment(accountHolder, Float.valueOf(inputLine),destinationAccountId);
        }
        catch (Exception e)
        {
            return false;
        }
        return result;
    }

    public static boolean viewTransactionHistory()
    {
        List<Transaction> transactions = new ArrayList<>();
        boolean result = true;
        inputLine = "";
        AccountHolder accountHolder;
        String accountType="";
        try
        {
            System.out.println("\r\nEnter account holder id \r\n");
            inputLine = bufferedReader.readLine();
            accountHolder = bankXAccountService.findCustomer(Long.valueOf(inputLine));
            if(accountHolder == null)
                return false;

            LinkedAccounts linkedAccounts = bankXAccountService.findCustomerAccounts(Long.valueOf(inputLine));
            String accountInfo = "";
            System.out.println("\r\nSelect account type\r\n1. Savings Account\r\n2. Current Account\r\n");
            inputLine = bufferedReader.readLine();
            if(inputLine.compareTo("1") == 0)
            {
                accountType = Constants.SAVINGS_ACCOUNT;
                accountInfo = "Account holder="+accountHolder.getName()+" "+accountHolder.getSurname()+". Savings Account number="+linkedAccounts.getSavingsAccount().getAccountId();
            }
            else if(inputLine.compareTo("2") == 0)
            {
                accountType = Constants.CURRENT_ACCOUNT;
                accountInfo = "Account holder="+accountHolder.getName()+" "+accountHolder.getSurname()+". Current Account number="+linkedAccounts.getCurrentAccount().getAccountId();
            }
            else
                return false;

            transactions = bankXAccountService.provideCustomerTransactionHistory(accountHolder,accountType);

            String successful = "";
            String type = "";
            String immediate = "";
            System.out.println("\r\n-----------Transaction history for: "+accountInfo+"----------------\r\n");
            for(Transaction transaction: transactions)
            {
                if(transaction.isSucessful())
                    successful = "successful";
                else
                    successful = "unsuccessful";

                if(transaction.isCredit())
                    type = "credit";
                else
                    type = "debit";

                if(transaction.isImmediate())
                    immediate = "immediate-yes";
                else
                    immediate = "immediate-no";

                String lineEntry = transaction.getAmount() + ":" + type + ":"+ immediate +":"+successful+":TransactionDate="+transaction.getDateTimeAsString();
                System.out.println(lineEntry);
            }
            System.out.println("\r\n---------End of transaction history for: "+accountInfo+"---------------\r\n");
        }
        catch (Exception e)
        {
            return false;
        }

        return result;
    }

    public static boolean doBankZImmediateTransactions()
    {
        boolean result = false;
        inputLine = "";
        List<Transaction> transactions = new ArrayList<>();

        try
        {
            System.out.println("\r\nEnter each transaction data as one line in the following format and order:\r\ncredit(as true for credit and false for deposit),amount,accountHolderId,accountId" +
                    "\r\ni.e true,250.50,32432,234342 - is credit 250.50 into accountId 234342, held by customer with id 32432\r\n");
            while(true)
            {
                inputLine = bufferedReader.readLine();
                if(inputLine.compareTo("-1") == 0)
                    break;

                String[] transactionData = inputLine.split(",");
                Transaction transaction = new Transaction(true,Boolean.valueOf(transactionData[0]),Float.valueOf(transactionData[1]),true,Long.valueOf(transactionData[2]),
                        Long.valueOf(transactionData[3]),"");  // need to generate acountInfo
                transactions.add(transaction);
                System.out.println("\r\nEnter another transaction. Enter -1 if done entering transactions.");
            }
            System.out.println("\r\nDoing BankZ immediate transactions...");
            result = bankXAccountService.doBankZImmidiateTransactions(transactions);
        }
        catch (Exception e)
        {
            return false;
        }
        return result;
    }


    public static boolean doBankZEndOfDayRecon()
    {
        boolean result = false;

        try
        {
            System.out.println("\r\nDoing BankZ EOD recon, with recon transactions file locatated in location:\r\n"+bankXAccountService.getBankZEndOfDayTransactionsFilePath());
            result = bankXAccountService.doBankZEodTransactions();
        }
        catch (Exception e)
        {
            return false;
        }
        return result;
    }


    public static BankAccountServiceConfiguration loadConfiguration(String configFilePath)
    {
        JSONParser jsonParser = new JSONParser();
        BankAccountServiceConfiguration bankAccountServiceConfiguration = new BankAccountServiceConfiguration();

        try (FileReader reader = new FileReader(configFilePath))
        {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;

            bankAccountServiceConfiguration.savingsAccountJoiningBonus = Float.valueOf( (String) jsonObject.get("savingsAccountJoiningBonus"));
            bankAccountServiceConfiguration.savingsAccountCreditReward = Float.valueOf( (String) jsonObject.get("savingsAccountCreditReward"));
            bankAccountServiceConfiguration.transactionalAccountPaymentsFee = Float.valueOf( (String) jsonObject.get("transactionalAccountPaymentsFee"));
            bankAccountServiceConfiguration.bankZEndOfDayTransactionsFilePath = (String) jsonObject.get("bankZEndOfDayTransactionsFilePath");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return bankAccountServiceConfiguration;

    }




}