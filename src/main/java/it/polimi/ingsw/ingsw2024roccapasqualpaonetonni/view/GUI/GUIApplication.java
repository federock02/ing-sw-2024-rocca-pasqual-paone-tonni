package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.GUI;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.HelloApplication;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.ConsolePrinter;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.Client;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.EnumConnectionType;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.EnumViewType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GUIApplication extends Application {
    private Client client;
    /**
     * we use a ThreadPoolExecutor to execute bacground tasks that call alow actions on the server
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private Parent createContent(){
        return new StackPane(new Text("Hello world!"));
    }
    @Override
    public void start(Stage stage) throws Exception {
        client = new Client(this,Objects.requireNonNull(EnumConnectionType.valueOf(getParameters().getRaw().get(0))));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lobby.fxml"));
        Parent root = loader.load();
        Controller1 controller = loader.getController();

        controller.setParameters(executor, client);

        stage.setScene(new Scene(root, 300, 200));
        stage.setTitle("Simple Button Example");
        stage.show();





        /*Button buttonNew = new Button("Create");
        Button buttonJoin = new Button("Join any");
        Button buttonJoinID = new Button("JoinById");
        Button buttonReconnect = new Button("Reconnect");
        HBox startButtons = new HBox();
        StackPane root = new StackPane();
        startButtons.getChildren().addAll(buttonNew,buttonJoin,buttonJoinID,buttonReconnect);

        buttonNew.setOnAction(event -> {
            executor.submit(()->{
                try {
                    client.receiveInput("/new 2 a");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NotBoundException e) {
                    throw new RuntimeException(e);
                }
            });});
        root.getChildren().add(startButtons);

        stage.setScene(new Scene(root,300,300));
        stage.setTitle("Codex naturalis");
        stage.show();*/
    }
    public void joinLobby(){
        ConsolePrinter.consolePrinter("joinLobby");
    }
    public void show_createdGame(int gameID){
        String message = String.format("Game created, with GameID: %d", gameID);
        ConsolePrinter.consolePrinter(message);
    }
    public void show_youJoinedGame(int gameID){
        String message = String.format("Joined game: %d", gameID);
        ConsolePrinter.consolePrinter(message);
    }
    public void show_noAvailableGame(){
        String message = String.format("no games available");
        ConsolePrinter.consolePrinter(message);
    }
}
