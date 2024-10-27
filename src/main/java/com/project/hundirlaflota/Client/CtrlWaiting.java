package com.project.hundirlaflota.Client;

import javafx.application.Platform; // Importar Platform
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class CtrlWaiting {
    @FXML
    Label countdown;
    @FXML
    Label player1;
    @FXML
    Label player2;
    static WebSocketClient webSocketClient;

    public void initialize() {

    }

}
