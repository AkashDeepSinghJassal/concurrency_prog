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

        public DoubleBuffer() {
            buffer1 = new int[100];
            buffer2 = new int[100];
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
        
        

        final DoubleBuffer doubleBuffer = new DoubleBuffer();
        
        Thread writerThread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                doubleBuffer.write("Hello");
            }
        });
        
        Thread writerThread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(10);
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
                Thread.sleep(10);
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
