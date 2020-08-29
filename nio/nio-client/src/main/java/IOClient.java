import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class IOClient implements Runnable{

    static Socket socket;

    public IOClient() throws IOException {

    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            byte[] buffer = new byte[256];
            while (true) {
                System.out.println(1);
                int cnt = is.read(buffer);
                System.out.println(2);
                System.out.println(new String(buffer, 0, cnt));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        socket = new Socket("localhost", 8189);
        new Thread(new IOClient()).start();
        new Thread(new ConsoleReader(socket)).start();
    }


}
