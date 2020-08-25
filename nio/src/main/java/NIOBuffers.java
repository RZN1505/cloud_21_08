import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;

public class NIOBuffers {
    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        buffer.put("Hello world".getBytes());
        buffer.flip();
        while (buffer.hasRemaining()) {
            System.out.print((char)buffer.get());
        }
        buffer.rewind();
        //Files.createFile(Path.of("nio/dir/bufferTest.txt"));
        RandomAccessFile rf = new RandomAccessFile("nio/dir/bufferTest.txt", "rw");
        rf.getChannel().write(buffer, rf.getChannel().size());
        A a = AFactory.getA();
        B b = B.getInstance();
//        buffer.put((byte) 65);
//        buffer.put((byte) 66);
//        buffer.flip();
//        while (buffer.hasRemaining()) {
//            System.out.println(buffer.get());
//        }
//        buffer.clear(); // read from start
//        while (buffer.hasRemaining()) {
//            System.out.println(buffer.get());
//        }
    }
}
