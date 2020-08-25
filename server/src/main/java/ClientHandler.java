import java.io.*;
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
   /* public void recieveFile(String message) throws IOException {
        File dir = ClientHandler.createDirectory("client" + name + "Dir");
        os.flush();
    }*/
   synchronized public void recieveFiles(String message) throws IOException {
       System.out.println("recieveFiles" + message);
        File dir = createDirectory("client" + name + "Dir");
        //String [] strFiles = message.split("&")[1].split(",");
        OutputStream osf = new FileOutputStream(dir + "/" + "fileName");
        byte[] buffer = new byte[1024];

        int count = 0;
        while((count = is.read(buffer)) != -1) {
            osf.write(buffer, 0, count);
            System.out.println("read " + count + " bytes");
        }
        osf.close();
        is.close();
    }
    synchronized public File createDirectory(String directoryPath) throws IOException {
        File dir = new File(directoryPath);
        if (dir.exists()) {
            return dir;
        }
        if (dir.mkdirs()) {
            return dir;
        }
        throw new IOException("Failed to create directory '" + dir.getAbsolutePath() + "' for an unknown reason.");
    }
    public void closeConnection() throws Exception {
        os.close();
        is.close();
        socket.close();
        System.out.println("client " + name + " disconnected");
    }
    public void run() {
        while (true) {
            try {
                String message = is.readUTF();
                System.out.println("message from " + name + ": " + message);
                server.broadCastMessage(message);
                if (message.equals("quit")) {
                    closeConnection();
                    server.kick(this);
                    break;
                }
                if (message.startsWith("/upload")) {
                    recieveFiles(message);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
