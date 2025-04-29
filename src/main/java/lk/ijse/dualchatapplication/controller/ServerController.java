package lk.ijse.dualchatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerController {

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
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000);
                textArea.appendText("Server started\n");
                socket = serverSocket.accept();
                textArea.appendText("Client connected\n");
                dataInputStream = new DataInputStream(socket.getInputStream());

                while (!message.equals("exit")) {
                    message = dataInputStream.readUTF();
                    textArea.appendText(message + "\n");
                    textField.clear();
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
}
