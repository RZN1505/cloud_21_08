import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class Controller implements Initializable {

    public ListView<String> listView;
    public TextField text;
    public Button send;
    private Socket socket;
    private static DataInputStream is;
    private static DataOutputStream os;
    FileInputStream fis;
    BufferedInputStream bis;
    InputStream in;
    OutputStream out;
    private String clientPath = "client/ClientStorage";
    private String [] commands = {"/upload&"};
    private HashSet<String> sendFiles = new HashSet<>();
    private File dir;
    String oldText ="";

    public static void stop() {
        try {
            os.writeUTF("quit");
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    synchronized public void sendFile(File file) throws IOException {
        System.out.println("transferFile2=" + file.getName() + " " + (int) file.length());

        String message = text.getText();

        try {

            byte[] mybytearray = new byte[(int) file.length()];

            fis = new FileInputStream(file);

            is = new DataInputStream(fis);

            byte[] buffer = new byte[5];
            boolean var5 = false;

            int count;
            while((count = is.read(buffer)) != -1) {
                os.write(buffer, 0, count);
                System.out.println("readClient " + count + " bytes");
            }
            os.flush();

            closeConnection();
            System.out.println("Done");
        } catch (IOException ex) {
            System.out.println("Errr");
        } catch (Exception e) {
            System.err.println(e + " file to server send");;
        }
    }
    public void sendMessage(ActionEvent actionEvent) {
        String message = text.getText();


        try {
            os.writeUTF(message);
            os.flush();

            for (File file : dir.listFiles()) {
                if (sendFiles.contains(file.getName())) {
                    System.out.println("transferFileList" + file.getName() + " " + (int) file.length());
                    sendFile(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        text.clear();
    }

    public void connect(/*String host, int port*/) throws Exception {
        socket = new Socket("localhost", 8189);;

        in = this.socket.getInputStream();
        out = this.socket.getOutputStream();
        is = new DataInputStream(in);
        os = new DataOutputStream(out);
    }

    public void closeConnection() throws Exception {
        in.close();
        out.close();
        os.close();
        is.close();
        socket.close();
    }
    public void initialize(URL location, ResourceBundle resources) {
        dir = new File(clientPath);

        for (File file : dir.listFiles()) {
            listView.getItems().add(file.getName()/* + "|" + file.length() + " bytes"*/);
        }

        text.setOnAction(this::sendMessage);

        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2) {
                    String currentItemSelected = (String) listView.getSelectionModel()
                            .getSelectedItem();
                    String str = currentItemSelected/*.split("|")*/;
                    String oldTextHelper = oldText != "" ? oldText : commands[0];
                    oldText = oldTextHelper  + ", " + str;
                    text.setText(oldText);
                    System.out.println("currentItemSelected" + text.getText());
                    sendFiles.add(str);
                }
            }
        });

        try {
            connect();
//            new Task<String>() {
//                @Override
//                protected String call() throws Exception {
//                    return is.readUTF();
//                }
//
//                @Override
//                protected void succeeded() {
//                    try {
//                        listView.getItems().add(get());
//                    } catch (InterruptedException | ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
            new Thread(() -> {
                while (true) {
                    try {
                        String message = is.readUTF();
                        if (message.equals("quit")) {
                            break;
                        }
                        Platform.runLater(() -> listView.getItems().add(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
