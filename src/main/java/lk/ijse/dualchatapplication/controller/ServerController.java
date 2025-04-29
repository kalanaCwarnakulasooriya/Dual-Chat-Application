package lk.ijse.dualchatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class ServerController {
    @FXML
    private ImageView imageView;

    @FXML
    private TextArea textArea;

    @FXML
    private TextField textField;

    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    String message = "";

    public void initialize() {
        new Thread(()->{
            try {
                serverSocket = new ServerSocket(5000);
                textArea.appendText("Server started.\n");
                socket = serverSocket.accept();
                textArea.appendText("Client connected\n");
                dataInputStream = new DataInputStream(socket.getInputStream());

                while (!message.equals("exit")){
                    message = dataInputStream.readUTF();
                    if(message.equals("IMG")) {
                        textArea.appendText("Image received\n");
                        long fileSize = dataInputStream.readLong();
                        byte[] imageBytes = new byte[(int) fileSize];
                        dataInputStream.readFully(imageBytes);
                        Image image = new Image(new ByteArrayInputStream(imageBytes));
                        imageView.setImage(image);
                    }else{
                        textArea.appendText("Client: " +message + "\n");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @FXML
    void sendOnAction(ActionEvent event) throws IOException {
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        message = textField.getText();
        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();
        textArea.appendText("Server: " + message + "\n");
        textField.setText("");
        if (message.equals("exit")) {
            socket.close();
            serverSocket.close();
        }
    }

    public void uploadOnAction(ActionEvent event) {
        Window window = ((Node) (event.getSource())).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(window);
        event.consume();
        if (file != null) {
            try {
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                dataOutputStream.writeUTF("IMG");
                dataOutputStream.writeLong(fileBytes.length);
                dataOutputStream.write(fileBytes);
                dataOutputStream.flush();
                textArea.appendText("Image sent successfully.\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            textArea.appendText("File selection cancelled.\n");
        }
    }
}
