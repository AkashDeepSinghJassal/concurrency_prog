import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicLong;

public class ReaderWriterDoubleBuffer {
    static class IntRef {
        int value;

        public IntRef(int value) {
            this.value = value;
        }
    }
    static class DoubleBuffer {
        private int[] buffer1;
        private int[] buffer2;
        private boolean isBuffer1Active;
        private IntRef length1;
        private IntRef length2;

        public DoubleBuffer(int size) {
            buffer1 = new int[size];
            buffer2 = new int[size];
            length1 = new IntRef(0);
            length2 = new IntRef(0);
        }

        public void write(String message) {
            
            int[] bufferToWrite;
            IntRef length;
            if(isBuffer1Active) {
                bufferToWrite = buffer2;
                length = length2;
            } else {
                bufferToWrite = buffer1;
                length = length1;
            }
            // reset value to overwrite inactive buffer
            length.value = 0;
            for (int i = 0; i < message.length(); i++) {
                bufferToWrite[i] = message.charAt(i);
                length.value++;
            }
            if  (isBuffer1Active) {
                isBuffer1Active = false;
            } else {
                isBuffer1Active = true;
            }
        }

        public String read() {
            int[] bufferToRead;
            IntRef length;
            if(isBuffer1Active) {
                bufferToRead = buffer1;
                length = length1;
            } else {
                bufferToRead = buffer2;
                length = length2;
            }

            StringBuilder message = new StringBuilder();
            for (int i = 0; i < length.value; i++) {
                message.append((char) bufferToRead[i]);
            }
            return message.toString();
        }
    }

    public static void main(String[] args) {
        
        

        final int SIZE = 100000000;
        final int COUNT = 10_000;
        final DoubleBuffer doubleBuffer = new DoubleBuffer(SIZE);

        AtomicLong totalTimeWriter1 = new AtomicLong(0);
        Thread writerThread1 = new Thread(() -> {
            for (int i = 0; i < COUNT; i++) {
                long startTime = System.nanoTime();
                doubleBuffer.write("Hello");
                totalTimeWriter1.addAndGet(System.nanoTime() - startTime);
            }
        }, "WriterThread1");

        AtomicLong totalTimeWriter2 = new AtomicLong(0);
        Thread writerThread2 = new Thread(() -> {
            for (int i = 0; i < COUNT; i++) {
                long startTime = System.nanoTime();
                doubleBuffer.write("Goodbye, World!");
                totalTimeWriter2.addAndGet(System.nanoTime() - startTime);

            }
        }, "WriterThread2");

        AtomicLong totalTimeReader = new AtomicLong(0);
        Thread readerThread = new Thread(() -> {
            for (int i = 0; i < COUNT; i++) {
                long startTime = System.nanoTime();
                doubleBuffer.read();
                totalTimeReader.addAndGet(System.nanoTime() - startTime);
            }

        }, "ReaderThread");

        writerThread1.start();
        readerThread.start();
        writerThread2.start();

        try {

            writerThread1.join();
            writerThread2.join();
            readerThread.join();

            double avgWriter1Time = totalTimeWriter1.get() / (double) COUNT / 1_000_000.0;
            double avgWriter2Time = totalTimeWriter2.get() / (double) COUNT / 1_000_000.0;
            double avgReaderTime = totalTimeReader.get() / (double) COUNT / 1_000_000.0;

            System.out.printf("Time metrics with buffer size : %s%n", SIZE);
            System.out.printf("Average Writer1 Time (ms): %.6f%n", avgWriter1Time);
            System.out.printf("Average Writer2 Time (ms): %.6f%n", avgWriter2Time);
            System.out.printf("Average Reader Time (ms): %.6f%n", avgReaderTime);

            String csvFile = "reader_writer_double_buffer.csv";
            try (FileWriter fw = new FileWriter(csvFile);
                    PrintWriter pw = new PrintWriter(fw)) {

                File file = new File(csvFile);
                if (file.length() == 0) {
                    pw.println("AvgWriter1Time,AvgWriter2Time,AvgReaderTime");
                }
                pw.printf("%.6f,%.6f,%.6f%n", avgWriter1Time, avgWriter2Time, avgReaderTime);

            } catch (IOException e) {
                System.err.println("Error writing to CSV file: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    
    }
}
