package com.project.hundirlaflota.Client;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.project.hundirlaflota.Client.Main.ctrlPlay;
import static com.project.hundirlaflota.Client.Main.ctrlWait;

public class CtrlPagePrincipal {
    @FXML
    public Label txtMessage;
    @FXML
    public TextField username;
    public static UtilsWS wsClient; // Suponiendo que esta clase ya está definida
    public static String clientId = "";

    public void changeView(ActionEvent actionEvent) {
        UtilsViews.setView("ViewOptions");
    }

    @FXML
    public void searchAction(ActionEvent event) {
        txtMessage.setTextFill(Color.BLACK);
        txtMessage.setText("Connecting ...");

        pauseDuring(1500, this::connectToWebSocket);
    }

    private void connectToWebSocket() {
        try {
            // Cambia a tu URL de WebSocket válida aquí
            wsClient = UtilsWS.getSharedInstance("ws://localhost:3000"); // Asegúrate de que esto sea correcto
            sendUsername();
            // Registro de mensajes
            wsClient.onMessage(response -> {
                Platform.runLater(() -> handleMessage(response));
            });

        } catch (Exception e) {
            e.printStackTrace();
            txtMessage.setTextFill(Color.RED);
            txtMessage.setText("Failed to connect");
        }
    }

    private void handleMessage(String response) {
        Platform.runLater(() -> wsMessage(response));
    }


    private void wsMessage(String response) {
        JSONObject msgObj = new JSONObject(response);
        switch (msgObj.getString("type")) {
            case "clients":
                if (clientId.isEmpty()) {
                    clientId = msgObj.getString("id");
                }
                if (!UtilsViews.getActiveView().equals("ViewWait")) {
                    UtilsViews.setViewAnimating("ViewWait");
                }
                List<String> stringList = jsonArrayToList(msgObj.getJSONArray("list"), String.class);
                if (!stringList.isEmpty()) { ctrlWait.player1.setText(stringList.get(0)); }
                if (stringList.size() > 1) { ctrlWait.player2.setText(stringList.get(1)); }
                break;
            case "countdown":
                int value = msgObj.getInt("value");
                String txt = String.valueOf(value);
                if (value == 0) {
                    UtilsViews.setView("ViewPlay");
                    UtilsViews.setViewAnimating("ViewPlay");
                    txt = "GO";
                }
                ctrlWait.countdown.setText(txt);
                break;
            case "serverMouseMoving":
                ctrlPlay.setPlayersMousePositions(msgObj.getJSONObject("positions"));
                break;
//            case "serverSelectableObjects":
//                ctrlPlay.setSelectableObjects(msgObj.getJSONObject("selectableObjects"));
//                break;
        }
    }
    

    public static void pauseDuring(long milliseconds, Runnable action) {
        PauseTransition pause = new PauseTransition(Duration.millis(milliseconds));
        pause.setOnFinished(event -> Platform.runLater(action));
        pause.play();
    }

    public static <T> List<T> jsonArrayToList(JSONArray array, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            T value = clazz.cast(array.get(i));
            list.add(value);
        }
        return list;
    }

    public void sendUsername() {
        if (wsClient != null && wsClient.isOpen()) {
            String usernameText = username.getText(); // Obtén el nombre de usuario
            JSONObject usernameMessage = new JSONObject();
            usernameMessage.put("type", "registerUsername");
            usernameMessage.put("username", usernameText);
            wsClient.send(usernameMessage.toString()); // Envía el mensaje al servidor
        } else {
            txtMessage.setTextFill(Color.RED);
            txtMessage.setText("WebSocket is not connected.");
        }
    }
}
