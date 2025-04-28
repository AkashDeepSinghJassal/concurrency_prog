import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicLong;

public class ReaderWriterMultipleBuffer {
    static class BufferRef {
        int[] buffer;
        int length;

        public BufferRef(int[] buffer, int length) {
            this.buffer = buffer;
            this.length = length;
        }
    }

    static class Buffer {
        private BufferRef bufferRef;
        int size;

        public Buffer(int size) {
            this.size = size;
        }

        public void write(String message) {

            int[] bufferToWrite = new int[size];
            int currLength = 0;

            for (int i = 0; i < message.length(); i++) {
                bufferToWrite[i] = message.charAt(i);
            }
            currLength = message.length();
            bufferRef = new BufferRef(bufferToWrite, currLength);
        }

        public String read() {
            BufferRef bufferRefToRead = bufferRef;
            if (bufferRefToRead == null) {
                return null;
            }
            StringBuilder message = new StringBuilder();
            for (int i = 0; i < bufferRefToRead.length; i++) {
                message.append((char) bufferRefToRead.buffer[i]);
            }
            return message.toString();
        }
    }

    public static void main(String[] args) {

        final int SIZE = 100000000;
        final int COUNT = 10_000;
        final Buffer buffer = new Buffer(SIZE);

        AtomicLong totalTimeWriter1 = new AtomicLong(0);
        Thread writerThread1 = new Thread(() -> {
            for (int i = 0; i < COUNT; i++) {
                long startTime = System.nanoTime();
                buffer.write("Hello");
                totalTimeWriter1.addAndGet(System.nanoTime() - startTime);
            }
        }, "WriterThread1");

        AtomicLong totalTimeWriter2 = new AtomicLong(0);
        Thread writerThread2 = new Thread(() -> {
            for (int i = 0; i < COUNT; i++) {
                long startTime = System.nanoTime();
                buffer.write("Goodbye, World!");
                totalTimeWriter2.addAndGet(System.nanoTime() - startTime);

            }
        }, "WriterThread2");

        AtomicLong totalTimeReader = new AtomicLong(0);
        Thread readerThread = new Thread(() -> {
            for (int i = 0; i < COUNT; i++) {
                long startTime = System.nanoTime();
                buffer.read();
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

            String csvFile = "reader_writer_multiple_buffer.csv";
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
