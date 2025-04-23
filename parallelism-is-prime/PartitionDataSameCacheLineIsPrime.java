
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartitionDataSameCacheLineIsPrime {

    static class SumAggregator {
        private int[] sum;

        public SumAggregator(int threadCount) {
            sum = new int[threadCount];
        }
        public void add(int idx) {
            sum[idx] += 1;
        }

        public int getSum() {
            return Arrays.stream(sum).sum();
        }
    }

    static class MyThread extends Thread {
        private int idx;
        private int start;
        private int end;
        private SumAggregator sumAggregator;

        public MyThread(int idx, int start, int end, SumAggregator sumAggregator) {
            this.idx = idx;
            this.start = start;
            this.end = end;
            this.sumAggregator = sumAggregator;
        }

        @Override
        public void run() {
            super.run();
            // System.out.format("Thread %s started processing from %s to %s\n",
            // Thread.currentThread().getName(), start, end);
            for (int i = start; i <= end; i++) {
                if (isPrime(i)) {
                    sumAggregator.add(idx);
                }
            }
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
        // reset sum
        SumAggregator sumAggregator = new SumAggregator(threadCount);
        List<Thread> threads = new ArrayList<>();
        
        int blockSize = size / threadCount;
        
        for (int i = 0; i < threadCount; i++) {
            int start = i * blockSize + 1;
            int end = (i == threadCount - 1) ? size : (i + 1) * blockSize;
            
            MyThread myThread = new MyThread(i, start, end, sumAggregator);
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
        
        try (java.io.FileWriter csvWriter = new java.io.FileWriter("prime_results_partition_same_cache_line.csv")) {
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