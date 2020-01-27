//Douglas Hammarstam doha6991


package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.concurrent.locks.Lock;


public class Factorizer implements Runnable {

    public static class WorkStatus {
        private volatile boolean completed;

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
    BigInteger factor1;
    BigInteger factor2;

    public Factorizer(BigInteger step, BigInteger start, WorkStatus workStatus, BigInteger max, BigInteger product) {
        this.step = step;
        this.start = start;
        this.workStatus = workStatus;
        this.max = max;
        this.product = product;
    }


/*    public boolean isPrime(BigInteger number) {
        boolean result = true;
        for (BigInteger d = new BigInteger("2"); d.compareTo(number.sqrt()) <= 0; d = d.add(BigInteger.ONE)) {
            if (number.remainder(d).equals(BigInteger.ZERO))
                result = false;
        }
        return result;
    }*/

    public boolean isPrimeLong(long number) {
        if (number <= 1)
            return false;
        boolean result = true;
        for (long denominator = 2; denominator < Math.sqrt(number); denominator++) {
            if (number % denominator == 0)
                result = false;
        }
        return result;
    }


    @Override
    public void run() {
/*
        if (isPrime(product)) {
            System.out.println("No factorization possible");
            workStatus.markCompleted(true);
        }
*/


        //BigInteger number = start;
        long number = start.longValue();
        long longMax = max.longValue();
        long longProduct = product.longValue();
        long longStep = step.longValue();

        while (number <= longMax && !workStatus.isCompleted()) {
            //System.out.println(this.hashCode() + " Checking " + number + " and " + longProduct / number);
            if (longProduct % number == 0 && isPrimeLong(number)) {
                synchronized (workStatus) {
                   // System.out.println("FOUND STOP");
                    if(workStatus.isCompleted()){
                        return;
                    }
                    workStatus.markCompleted(true);
                    System.out.println(this + " Factor1: " + number + " Factor2: " + longProduct / number);
                }



            }

//            if (!isPrime(number))
//                number = number.add(step);
//            System.out.println(this.hashCode() + " Checking " + number + " and " + product.divide(number));
//            if (product.remainder(number).compareTo(BigInteger.ZERO) == 0 && isPrime(number)) {
//                synchronized (workStatus) {
//                   // System.out.println("FOUND STOP");
//                    if(workStatus.isCompleted()){
//                        return;
//                    }
//                    factor1 = number;
//                    factor2 = number.divide(product);
//                    workStatus.markCompleted(true);
//                    System.out.println(this + " Factor1: " + number + " Factor2: " + product.divide(number));
//                }
//
//
//            }
            number = number + longStep;

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

            long stop = System.nanoTime();
            System.out.println("\nExecution time (seconds): " + (stop - start) / 1000000000.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
