//Douglas Hammarstam doha6991


package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.concurrent.locks.Lock;


public class Factorizer implements Runnable {

    public static class WorkStatus {
        private boolean completed;

        public boolean isCompleted() {
            return completed;
        }

        public synchronized void markCompleted(boolean completed) {
            this.completed = completed;
        }

    }

    private final WorkStatus workStatus;
    private BigInteger start;
    private BigInteger product;
    private BigInteger step;
    private BigInteger max;


    public Factorizer(BigInteger step, BigInteger start, WorkStatus workStatus, BigInteger max, BigInteger product) {
        this.step = step;
        this.start = start;
        this.workStatus = workStatus;
        this.max = max;
        this.product = product;
    }


    public boolean isPrime(BigInteger number) {
        boolean result = true;
        for (BigInteger d = new BigInteger("2"); d.compareTo(number.sqrt()) <= 0; d = d.add(BigInteger.ONE)) {
            if (number.remainder(d).equals(BigInteger.ZERO))
                result = false;
        }
        return result;
    }


    @Override
    public void run() {


        BigInteger number = start;
        while (number.compareTo(max) < 0 && !workStatus.isCompleted()) {
            if (product.remainder(number).compareTo(BigInteger.ZERO) == 0 && isPrime(number)) {
                synchronized (workStatus) {
                    // System.out.println("FOUND STOP");
                    if (workStatus.isCompleted()) {
                        return;
                    }

                    workStatus.markCompleted(true);
                    System.out.println(this + " Factor1: " + number + " Factor2: " + product.divide(number));
                    return;
                }


            }
            number = number.add(step);


        }


    }


    public static void main(String[] args) throws InterruptedException {
        // Read input.

        InputStreamReader streamReader = new InputStreamReader(System.in);
        BufferedReader consoleReader = new BufferedReader(streamReader);
        String input;
        int numThreads;

        try {
            System.out.print("Input (numThreads)>");
            input = consoleReader.readLine();
            numThreads = Integer.parseInt(input);
            System.out.print("Input (inputNumber)>");
            BigInteger inputNumber = new BigInteger(consoleReader.readLine());
            consoleReader.close();


            long start = System.nanoTime();
            Thread[] threads = new Thread[numThreads];
            Factorizer[] factorizers = new Factorizer[numThreads];
            WorkStatus workStatus = new WorkStatus();
            if (inputNumber.compareTo(BigInteger.valueOf(numThreads)) < 0) {
                numThreads = inputNumber.intValue();
            }
            for (int i = 0; i < numThreads; i++) {
                factorizers[i] = new Factorizer(BigInteger.valueOf(numThreads), BigInteger.TWO.add(BigInteger.valueOf(i)), workStatus, inputNumber.sqrt(), inputNumber);
                threads[i] = new Thread(factorizers[i]);

            }
            for (int i = 0; i < numThreads; i++) {
                threads[i].start();
            }
            for (int i = 0; i < numThreads; i++) {
                threads[i].join();
            }
            //if it aint done it aint done
            if(!workStatus.isCompleted()){
                System.out.println("No factorization possible");
            }


            long stop = System.nanoTime();
            System.out.println("\nExecution time (seconds): " + (stop - start) / 1000000000.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
