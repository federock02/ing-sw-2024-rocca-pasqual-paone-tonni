package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.GUI.controllers;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.Client;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.GUI.GUIApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.concurrent.ExecutorService;

public class LobbyController extends GenericController {
    @FXML
    private Button button;
    private ExecutorService executor;
    private Client client;
    private GUIApplication application;

    public void setParameters(ExecutorService executor, Client client,GUIApplication application){
        this.executor = executor;
        this.client = client;
        this.application = application;
    }
    
    @FXML
    public void handleNewGameButtonClick(ActionEvent event){
        Platform.runLater(()-> {
            try {
                application.changeScene("/NameNumber.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    public void handleJoinGameButtonClick(ActionEvent event){
        Platform.runLater(()-> {
            try {
                application.changeScene("/Name.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    public void handleJoinGameIDButtonClick(ActionEvent event){
        Platform.runLater(()-> {
            try {
                application.changeScene("/NameGameId.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    public void handleReconnectButtonClick(ActionEvent event){
        Platform.runLater(()-> {
            try {
                application.changeScene("/Reconnect.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
