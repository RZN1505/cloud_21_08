import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final DataInputStream is;
    private final DataOutputStream os;
    private final IOServer server;
    private final Socket socket;
    private static int counter = 0;
    private final String name;

    public ClientHandler(Socket socket, IOServer ioServer) throws IOException {
        server = ioServer;
        this.socket = socket;
        counter++;
        name = "user#" + counter;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client handled: ip = " + socket.getInetAddress());
        System.out.println("Nick:" + name);
    }

    public void sendMessage(String message) throws IOException {
        os.writeUTF(message);
        os.flush();
    }

    public void run() {
        while (true) {
            try {
                String message = is.readUTF();
                System.out.println("message from " + name + ": " + message);
                server.broadCastMessage(message);
                if (message.equals("quit")) {
                    server.kick(this);
                    os.close();
                    is.close();
                    socket.close();
                    System.out.println("client " + name + " disconnected");
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
