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

    public ListView<String> listViewClient;
    public ListView<String> listViewServer;
    public TextField text;
    public TextField textStatus;
    public Button send;
    private Socket socket;
    private static DataInputStream is;
    private static DataOutputStream os;
    FileInputStream fis;
    BufferedInputStream bis;
    InputStream in;
    OutputStream out;
    private String clientPath = "client/ClientStorage";
    private String[] commands = {"/upload&", "/download&"};
    private HashSet<String> sendFiles = new HashSet<>();
    private HashSet<String> recieveFiles = new HashSet<>();
    private File dir;
    String oldText = "";
    String oldTextOneClick = "";


    public static void stop() {
        try {
            os.writeUTF("quit");
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recieveFile(String command, String fileName) throws IOException {
        System.out.println("recievetName() " + fileName);
        try {
            os.writeUTF(command);
            os.writeUTF(fileName);
            String response = is.readUTF();
            System.out.println("OKres" + response);
            if (response.equals("OK")) {
                System.out.println("OK");
                long fileLength = is.readLong();
            System.out.println("fileLength" + fileLength);
                File file = new File("client/ClientStorage/" + fileName);
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
                    /*if (file.length() == fileLength) {
                        System.out.println("fileLength3=" );*/
                        textStatus.setText("File downloaded!");
                    //}
                }
            } else {
                textStatus.setText("file not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     public void sendFile(File file, String command, String fileName) throws IOException {
        System.out.println("transferFile2=" + file.getName() + " " + (int) file.length());

        try (FileInputStream fis = new FileInputStream(file)) {
            os.writeUTF(command);
            os.writeUTF(fileName);
            os.writeLong(file.length());
            byte[] buffer = new byte[256];
            int read = 0;
            while ((read = fis.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            os.flush();
            String response = is.readUTF();
            if (response.equals("OK")) {
                textStatus.setText("File uploaded!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(ActionEvent actionEvent) {
        String message = text.getText();
        String[] tokens = message.split("&");
        String command = tokens[0];

        try {
            os.writeUTF(message);
            os.flush();
            if (command.equals("/upload")) {
                for (File file : dir.listFiles()) {
                    if (sendFiles.contains(file.getName())) {
                        System.out.println("transferFileList" + file.getName() + " " + (int) file.length());
                        sendFile(file, command, file.getName());
                    }
                }
            } else if (command.equals("/download")) {
                //for (String fileName : recieveFiles) {
                    recieveFile(command, tokens[1]);
                //}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        text.clear();
    }

    public void connect(/*String host, int port*/) throws Exception {
        socket = new Socket("localhost", 8189);
        in = this.socket.getInputStream();
        out = this.socket.getOutputStream();
        is = new DataInputStream(in);
        os = new DataOutputStream(out);
    }


    public void initialize(URL location, ResourceBundle resources) {
        dir = new File(clientPath);

        for (File file : dir.listFiles()) {
            listViewClient.getItems().add(file.getName()/* + "|" + file.length() + " bytes"*/);
        }

        text.setOnAction(this::sendMessage);

        listViewClient.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {
                String currentItemSelected = (String) listViewClient.getSelectionModel()
                        .getSelectedItem();
                String str = currentItemSelected;
                if (click.getClickCount() == 2) {
                    String oldTextHelper = oldText != "" ? oldText + ", " : commands[0];
                    oldText = oldTextHelper + str;
                    text.setText(oldText);
                    System.out.println("currentItemSelected" + text.getText());
                    sendFiles.add(str);
                } else if (click.getClickCount() == 1) {
                    String oldTextHelperOne = oldTextOneClick != "" ? oldTextOneClick + ", " : commands[1];
                    oldTextOneClick = oldTextHelperOne + str;
                    text.setText(oldTextOneClick);
                    System.out.println("currentItemSelectedOne" + text.getText());
                    recieveFiles.add(str);
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
            /*new Thread(() -> {
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
            }).start();*/
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
