package com.example.task5;

import com.example.task5.model.CurrencyCode;
import com.example.task5.model.UserAccount;
import com.example.task5.module.ExchangeModule;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Task5Demo {

        public static void main(String[] args) throws Exception {

                // Create the application module.
                // It already contains:
                // - FileAccountDao
                // - AccountService
                // - ExchangeRateService
                // - ExchangeService
                ExchangeModule module = new ExchangeModule(new File("data/accounts"));

                // Create a unique accountId so the demo can be run multiple times
                // without getting "account already exists"
                String accountId = "demo-" + System.currentTimeMillis();

                // Create an account with initial balances
                UserAccount account = new UserAccount(accountId);
                account.setBalance(CurrencyCode.USD, new BigDecimal("1000.00"));
                account.setBalance(CurrencyCode.EUR, new BigDecimal("100.00"));
                account.setBalance(CurrencyCode.GBP, new BigDecimal("50.00"));

                // Save the account through the module
                module.createAccount(account);

                // Add exchange rates
                module.addExchangeRate(CurrencyCode.USD, CurrencyCode.EUR, new BigDecimal("0.92"));
                module.addExchangeRate(CurrencyCode.EUR, CurrencyCode.USD, new BigDecimal("1.08"));
                module.addExchangeRate(CurrencyCode.USD, CurrencyCode.GBP, new BigDecimal("0.79"));

                // Print the initial account state
                printAccount("INITIAL ACCOUNT STATE", module.getAccount(accountId));

                // Create a thread pool with 4 threads
                ExecutorService executor = Executors.newFixedThreadPool(4);

                // CountDownLatch is used so all tasks wait first
                // and then start almost at the same time
                CountDownLatch startLatch = new CountDownLatch(1);

                // Store futures to get task results later
                List<Future<String>> futures = new ArrayList<Future<String>>();

                // Task 1:
                // exchange 100 USD -> EUR
                futures.add(executor.submit(
                                createExchangeTask(
                                                startLatch,
                                                module,
                                                accountId,
                                                CurrencyCode.USD,
                                                CurrencyCode.EUR,
                                                "100.00")));

                // Task 2:
                // exchange 50 USD -> EUR
                futures.add(executor.submit(
                                createExchangeTask(
                                                startLatch,
                                                module,
                                                accountId,
                                                CurrencyCode.USD,
                                                CurrencyCode.EUR,
                                                "50.00")));

                // Task 3:
                // exchange 200 USD -> GBP
                futures.add(executor.submit(
                                createExchangeTask(
                                                startLatch,
                                                module,
                                                accountId,
                                                CurrencyCode.USD,
                                                CurrencyCode.GBP,
                                                "200.00")));

                // Task 4:
                // exchange 70 EUR -> USD
                futures.add(executor.submit(
                                createExchangeTask(
                                                startLatch,
                                                module,
                                                accountId,
                                                CurrencyCode.EUR,
                                                CurrencyCode.USD,
                                                "70.00")));

                System.out.println();
                System.out.println("All tasks are ready.");
                System.out.println("Starting all threads now...");
                System.out.println();

                // Release all waiting tasks at the same time
                startLatch.countDown();

                // No more tasks will be submitted
                executor.shutdown();

                // Read the result of each task
                for (Future<String> future : futures) {
                        try {
                                // future.get() waits until the task is completed
                                // and returns the result string
                                String resultMessage = future.get();
                                System.out.println(resultMessage);
                        } catch (ExecutionException e) {
                                // If an exception happened inside a task,
                                // it will be available here
                                System.out.println("Task failed: " + e.getCause().getMessage());
                        }
                }

                // Wait for all threads to finish, up to 1 minute
                executor.awaitTermination(1, TimeUnit.MINUTES);

                // Load the final account state after all exchanges
                UserAccount finalAccount = module.getAccount(accountId);

                printAccount("FINAL ACCOUNT STATE", finalAccount);

                // Print expected balances for manual verification
                System.out.println("EXPECTED FINAL BALANCES:");
                System.out.println("USD = 725.60");
                System.out.println("EUR = 168.00");
                System.out.println("GBP = 208.00");
        }

        // Helper method:
        // creates one exchange task
        private static Callable<String> createExchangeTask(final CountDownLatch startLatch,
                        final ExchangeModule module,
                        final String accountId,
                        final CurrencyCode fromCurrency,
                        final CurrencyCode toCurrency,
                        final String amountText) {

                return new Callable<String>() {
                        @Override
                        public String call() throws Exception {

                                // Wait for the common start signal
                                startLatch.await();

                                // Get the current thread name for logging/demo output
                                String threadName = Thread.currentThread().getName();

                                // Convert the text amount into BigDecimal
                                BigDecimal amount = new BigDecimal(amountText);

                                // Perform currency exchange through the module
                                BigDecimal convertedAmount = module.exchange(
                                                accountId,
                                                fromCurrency,
                                                toCurrency,
                                                amount);

                                // Return a readable task result
                                return threadName
                                                + " | exchanged "
                                                + amount + " " + fromCurrency
                                                + " -> "
                                                + convertedAmount + " " + toCurrency;
                        }
                };
        }

        // Helper method:
        // prints account balances in a readable format
        private static void printAccount(String title, UserAccount account) {
                System.out.println(title);
                System.out.println("accountId = " + account.getAccountId());
                System.out.println("USD = " + account.getBalance(CurrencyCode.USD));
                System.out.println("EUR = " + account.getBalance(CurrencyCode.EUR));
                System.out.println("GBP = " + account.getBalance(CurrencyCode.GBP));
                System.out.println();
        }
}