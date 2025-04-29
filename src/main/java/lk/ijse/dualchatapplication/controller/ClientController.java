package lk.ijse.dualchatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientController {

    @FXML
    private TextArea textArea;

    @FXML
    private TextField textField;

    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    String message = "";

    public void initialize() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost",5000);
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
    void sendOnAction(ActionEvent event) {
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            message = textField.getText();
            textArea.appendText(message + "\n");
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            textField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
