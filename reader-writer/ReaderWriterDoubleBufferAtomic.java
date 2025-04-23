import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ReaderWriterDoubleBufferAtomic {
    
    static class DoubleBuffer {
        private int[] buffer1;
        private int[] buffer2;
        private AtomicBoolean isBuffer1Active;
        private AtomicInteger length1;
        private AtomicInteger length2;

        public DoubleBuffer() {
            buffer1 = new int[100];
            buffer2 = new int[100];
            isBuffer1Active = new AtomicBoolean(true);
            length1 = new AtomicInteger(0);
            length2 = new AtomicInteger(0);
        }

        public void write(String message) {
            
            int[] bufferToWrite;
            AtomicInteger atomicLength;
            if(isBuffer1Active.get()) {
                bufferToWrite = buffer2;
                atomicLength = length2;
            } else {
                bufferToWrite = buffer1;
                atomicLength = length1;
            }
            atomicLength.set(0);

            for (int i = 0; i < message.length(); i++) {
                bufferToWrite[i] = message.charAt(i);
                atomicLength.getAndIncrement();
            }
            if (isBuffer1Active.get()) {
                isBuffer1Active.set(false);
            } else {
                isBuffer1Active.set(true);
            }
        }

        public String read() {
            int[] bufferToRead;
            int length;
            if(isBuffer1Active.get()) {
                bufferToRead = buffer1;
                length = length1.get();
            } else {
                bufferToRead = buffer2;
                length = length2.get();
            }
            StringBuilder message = new StringBuilder();
            for (int i = 0; i < length; i++) {
                message.append((char) bufferToRead[i]);
            }
            return message.toString();
        }
    }

    public static void main(String[] args) {
        

        final DoubleBuffer doubleBuffer = new DoubleBuffer();
        
        Thread writerThread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                doubleBuffer.write("Hello");
            }
        });
        
        Thread writerThread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                doubleBuffer.write("Goodbye, World!");
            }
        });
        
        Thread readerThread = new Thread(() -> {
            try {
            // Give writers a chance to write
            for (int i = 0; i < 1000; i++) {
                Thread.sleep(1);
                System.out.println("Reader read: " + doubleBuffer.read());
            }
            } catch (Exception e) {
            e.printStackTrace();
            }
        });
        
        writerThread1.start();
        readerThread.start();
        writerThread2.start();
        
        try {
            writerThread1.join();
            writerThread2.join();
            readerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
