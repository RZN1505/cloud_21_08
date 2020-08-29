import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class ConsoleReader implements Runnable {

    OutputStream os;
    Socket socket;

    public ConsoleReader(Socket socket) throws IOException, InterruptedException {
        this.socket = socket;
        os = socket.getOutputStream();
    }

    @Override
    public void run() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println(3);
            String message = in.next();
            try {
                System.out.println(4);
                socket.getChannel().write(ByteBuffer.wrap(message.getBytes()));
                System.out.println(5);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
