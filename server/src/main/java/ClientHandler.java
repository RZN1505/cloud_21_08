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
   /*synchronized*/ public void recieveFiles(String message) throws IOException {
       String fileName = is.readUTF();
       long fileLength = is.readLong();
       System.out.println("recieveFiles" + message);
       File dir = createDirectory("client" + name + "Dir");
       //String [] strFiles = message.split("&")[1].split(",");
      File file = new File(dir + "/" + fileName);
      file.createNewFile();
       try (FileOutputStream fos = new FileOutputStream(file)) {
           byte[] buffer = new byte[256];
           if (fileLength < 256) {
               fileLength += 256;
           }
           int read = 0;
           for (int i = 0; i < fileLength / 256; i++) {
               read = is.read(buffer);
               fos.write(buffer, 0, read);
           }
           os.writeUTF("OK");
           ;
           is.close();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
    /*synchronized*/ public void sendFiles(String message) throws IOException {
        String fileName = is.readUTF();
        System.out.println("sendFiles" + fileName);
        File dir = createDirectory("server/ServerStorage");
        File file = new File(dir + "/" + fileName);
        if (file.exists()) {
            os.writeUTF("OK");
            System.out.println("sendFilesOk" + file.getAbsolutePath());
        } else {
            //file.createNewFile();
            os.writeUTF("WRONG");
            System.out.println("sendFilesNOTOk");
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            os.writeLong(file.length());
            byte[] buffer = new byte[256];
            int read = 0;
            while ((read = fis.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            os.flush();
        }
    }
   /*synchronized*/ public File createDirectory(String directoryPath) throws IOException {
        File dir = new File(directoryPath);
        if (dir.exists()) {
            return dir;
        }
        if (dir.mkdirs()) {
            return dir;
        }
        throw new IOException("Failed to create directory '" + dir.getAbsolutePath() + "' for an unknown reason.");
    }

    public void run() {
        while (true) {
            try {
                String message = is.readUTF();
                System.out.println("message from " + name + ": " + message);
                //server.broadCastMessage(message);
                if (message.equals("quit")) {
                    os.writeUTF("disconnected");
                    Thread.sleep(1000);
                    os.close();
                    is.close();
                    socket.close();
                    System.out.println("client " + name + " disconnected");
                    break;
                }
                if (message.equals("/upload")) {
                    recieveFiles(message);

                } else if (message.equals("/download")) {
                    sendFiles(message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
