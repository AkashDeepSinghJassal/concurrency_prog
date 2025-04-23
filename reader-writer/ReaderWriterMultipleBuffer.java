public class ReaderWriterMultipleBuffer {
    static class BufferRef {
        int[] buffer;
        int length;
        public BufferRef(int[] buffer, int length) {
            this.buffer = buffer;
            this.length = length;
        }
    }
    static class DoubleBuffer {
        private BufferRef bufferRef;

        public DoubleBuffer() {
            
        }

        public void write(String message) {
            
            int[] bufferToWrite = new int[100];
            int currLength = 0;

            for (int i = 0; i < message.length(); i++) {
                bufferToWrite[i] = message.charAt(i);
                currLength++;
            }
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
        
        

        final DoubleBuffer doubleBuffer = new DoubleBuffer();
        
        Thread writerThread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                doubleBuffer.write("Hello");
            }
        });
        
        Thread writerThread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
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
            for (int i = 0; i < 100; i++) {
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
