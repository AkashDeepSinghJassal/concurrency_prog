import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounterIsPrime {

    static AtomicInteger counter = new AtomicInteger(0);
    static class SumAggregator {
        private int sum = 0;

        public synchronized void add(int value) {
            sum += value;
        }

        public int getSum() {
            return sum;
        }
    }
    static class MyThread extends Thread {
        private int size;
        private int count;
        private SumAggregator sumAggregator;
        public MyThread(int size, SumAggregator sumAggregator) {
            count = 0;
            this.sumAggregator = sumAggregator;
            this.size = size;
        }
    
        @Override
        public void run() {
            super.run();
            // System.out.format("Thread %s started processing from %s to %s\n",
            // Thread.currentThread().getName(), start, end);
            int i = 0;
            while((i = counter.getAndIncrement()) < size) {
                if (isPrime(i)) {
                    count++;
                }
            }
            sumAggregator.add(count);
        }
    }

    private static boolean isPrime(int n) {
        if (n <= 1)
            return false;
        if (n == 2 || n == 3)
            return true;
        if (n % 2 == 0)
            return false;

        for (int i = 5; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    
    private static int countPrimesWithThreads(int threadCount, int size)
    throws InterruptedException {
        // reset sum and counter
        counter.set(0); 
        SumAggregator sumAggregator = new SumAggregator();
        List<Thread> threads = new ArrayList<>();
        
        for (int i = 0; i < threadCount; i++) {
            MyThread myThread = new MyThread(size, sumAggregator);
            threads.add(myThread);
            myThread.start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        return sumAggregator.getSum();
    }
    public static void main(String[] args) throws InterruptedException {
        final int SIZE = 10_000_000; // total number
    
        try (java.io.FileWriter csvWriter = new java.io.FileWriter("prime_results_atomic_counter.csv")) {
            // Write CSV header
            csvWriter.append("ThreadCount,TimeTaken(ms),PrimeCount\n");
        
            for (int threadCount = 1; threadCount <= 16; threadCount++) {
            long totalTime = 0;
            int primeCount = 0;
            int runs = 5;
            
            for (int run = 0; run < runs; run++) {
                long startTime = System.currentTimeMillis();
                
                primeCount = countPrimesWithThreads(threadCount, SIZE);
                
                totalTime += System.currentTimeMillis() - startTime;
            }
            
            long averageTime = totalTime / runs;
            csvWriter.append(String.format("%d,%d,%d\n", threadCount, averageTime, primeCount));
            System.out.printf("Threads: %d, Average time: %d ms, Primes: %d%n", 
                threadCount, averageTime, primeCount);
            }
        
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}