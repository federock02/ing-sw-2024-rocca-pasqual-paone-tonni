package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.socket.client;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.listener.GameListener;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.cards.PlayingCard;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.ConsolePrinter;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.socket.clientMessages.*;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.socket.serverMessages.ServerGenericMessage;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.utils.DefaultNetworkValues;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.Client;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.ServerInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.ConsolePrinter.consolePrinter;

public class SocketClient extends Thread implements ServerInterface {
    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String nickname;
    private Client client;

    //private final SocketNotifier socketNotifier;

    public SocketClient(Client client){
        this.client=client;
        connect();
        this.start();
    }

    public void run(){
        while (true){
            try{
                ServerGenericMessage serverGenericMessage = (ServerGenericMessage) inputStream.readObject();
                //serverGenericMessage.launchMessage();
            } catch (IOException | ClassNotFoundException e) {
                consolePrinter("[SOCKET] Connection Lost!");
                System.exit(-1);
            }
        }

    }

    private void connect(){
        boolean retry = false;
        int attempt = 1;
        int i;

        do{
            try{
                clientSocket = new Socket(DefaultNetworkValues.Server_Ip_address,DefaultNetworkValues.Default_SOCKET_port);
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
                retry = false;

            }catch (Exception e){
                if (!retry) {
                    ConsolePrinter.consolePrinter("[ERROR] CONNECTING TO SOCKET SERVER" );
                }
                consolePrinter("[#" + attempt + "]Waiting to reconnect to SOCKET Server on port: '" + DefaultNetworkValues.Default_SOCKET_port + "' with name: '" + DefaultNetworkValues.Default_servername_RMI + "'");

                i = 0;
                while (i < DefaultNetworkValues.seconds_reconnection) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    consolePrinter(". ");
                    i++;
                }
                consolePrinter("\n");

                if (attempt >= DefaultNetworkValues.num_of_attempt) {
                    consolePrinter("[SOCKET] Give up!");
                    System.exit(-1);
                }
                retry = true;
                attempt++;
            }

        }while(retry);
    }
    private void disconnect() throws IOException {
        inputStream.close();
        outputStream.close();
        clientSocket.close();
    }
    private void messageDone() throws IOException {
        outputStream.flush();
        outputStream.reset();
    }

    public ObjectOutputStream getOutputStream(){
        return this.outputStream;
    }
    public ObjectInputStream getInputStream(){
        return this.inputStream;
    }
    @Override
    public void createGame(String name, int maxNumPlayers, GameListener me) throws IOException, NotBoundException {
        outputStream.writeObject(new MainMessageCreateGame(name,maxNumPlayers));
        messageDone();
    }

    @Override
    public void joinFirstAvailable(String name, GameListener me) throws IOException, NotBoundException {
        outputStream.writeObject(new MainMessageJoinFirstAvailable(name));
        messageDone();
    }

    @Override
    public void joinGameByID(String name, int idGame, GameListener me) throws IOException, NotBoundException {
        outputStream.writeObject(new MainMessageJoinGameById(name,idGame));
        messageDone();
    }

    @Override
    public void reconnect(String name, int idGame, GameListener me) throws IOException, NotBoundException {
        outputStream.writeObject(new MainMessageReconnect(name,idGame));
        messageDone();
    }

    @Override
    public void leave(String name, int idGame, GameListener me) throws IOException, NotBoundException {
        outputStream.writeObject(new MainMessageReconnect(name,idGame));
        messageDone();
    }

    @Override
    public void ready(String nickname) throws IOException, NotBoundException {
        outputStream.writeObject(new MessagePlayerReady(nickname));
        messageDone();
    }

    @Override
    public void addCard(PlayingCard cardToAdd, PlayingCard cardOnBoard, int cornerToAttach, Boolean flip) throws IOException {
        outputStream.writeObject(new MessageAddCard(cardToAdd,cardOnBoard,cornerToAttach,flip));
        messageDone();
    }

    @Override
    public void addStartingCard(Boolean flip) throws IOException {
        outputStream.writeObject(new MessageAddStarting(flip));
        messageDone();
    }

    @Override
    public void choosePlayerGoal(int choice) throws IOException {
        outputStream.writeObject(new MessageObjectiveChosen(choice));
        messageDone();
    }

    @Override
    public void drawResourceFromDeck() throws IOException {
        outputStream.writeObject(new MessageDrawResources());
        messageDone();
    }

    @Override
    public void drawGoldFromDeck() throws IOException {
        outputStream.writeObject(new MessageDrawGold());
        messageDone();
    }

    @Override
    public void drawFromBoard(int position) throws IOException {
        outputStream.writeObject(new MessageDrawFromBoard(position));
        messageDone();
    }

}
