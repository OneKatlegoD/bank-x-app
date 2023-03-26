# bank-x-app
Bank X Java implementation
Used maven project
To run:
1.After doing mvn clean install, go into the target directory and run the command:
java -jar bankX-technical-test-1.0-jar-with-dependencies.jar config/config.json

2. config/config.json is the location of the config file that contains the app configuration settings.

3. config.json contains the settings for:
savingsAccountJoiningBonus - set to 400.00 as per requirement
savingsAccountCreditReward - set to 0.005 as ....
transactionalAccountPaymentsFee - set to 0.0005   ....
bankZEndOfDayTransactionsFilePath - location of EOD recon file from bankZ

4. I made the customer notifications runs as a thread, the notifications are sent to the console screen. This sometimes clutters the console UI a bit.
