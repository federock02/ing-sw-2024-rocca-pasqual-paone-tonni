package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.controller;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.ConsolePrinter;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.GameListener;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.NotifierInterface;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.*;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.cards.Card;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.cards.PlayingCard;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.exception.DeckEmptyException;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.exception.GameAlreadyFullException;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.exception.NoCardException;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.exception.PlayerAlreadyInException;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.immutable.GameImmutable;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.controller.controllerInterface.GameControllerInterface;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.utils.JSONUtils;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.utils.DefaultControllerValues;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameController implements GameControllerInterface {
    private final Game model;
    private final Random random;
    private final String path;

    // attributes needed to implement the executor
    private transient final ExecutorService executorService;

    private transient final PingPongThread pingPongThread;

    public GameController(int id) throws RemoteException {
        super();
        this.model = new Game(id);
        this.random = new Random();
        this.path = DefaultControllerValues.jsonPath;
        this.executorService = Executors.newSingleThreadExecutor();
        this.pingPongThread = new PingPongThread();
        this.pingPongThread.start();
    }

    //---------------------------------EXECUTOR SECTION
    // the executor is a thread that can be fed a queue of Runnable or Callable, such as lambda expressions or
    // method-like expressions, and that executes them in order
    // it is used to de-synchronize the RMI calls, that now don't wait for the return at the end of the method
    // execution, but return after submitting the Runnable to the executor
    private void stopExecutor() {
        executorService.shutdown();
    }

    //---------------------------------SERVER SECTION
    private class PingPongThread extends Thread {

        List<String> clientsRunning = new ArrayList<>();
        List<String> clients;

        private void addClient(String client) {
            synchronized (clientsRunning) {
                clientsRunning.add(client);
                ConsolePrinter.consolePrinter("added listener");
                ConsolePrinter.consolePrinter(client);
            }
        }

        private void pong(String client) {
            synchronized (clientsRunning) {
                clientsRunning.add(client);
                ConsolePrinter.consolePrinter("ponged");
                ConsolePrinter.consolePrinter(client);
            }
        }

        @Override
        public void run() {
            while (true) {
                // Send ping message to client
                synchronized (clientsRunning) {
                    clients = new ArrayList<>(clientsRunning);
                    clientsRunning.clear();
                }
                for (String client : clients) {
                    model.ping(client);
                    ConsolePrinter.consolePrinter("pinging");
                    ConsolePrinter.consolePrinter(client);
                }

                // Wait for a certain period before sending the next ping
                try {
                    Thread.sleep(5000); // 7 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (clientsRunning) {
                    for (String client : clientsRunning) {
                        ConsolePrinter.consolePrinter("safe");
                        ConsolePrinter.consolePrinter(client);
                        clients.remove(client);
                    }
                }
                for (String deadClient : clients) {
                    ConsolePrinter.consolePrinter("dead client");
                    ConsolePrinter.consolePrinter(deadClient);

                    // CONTROLLARE SE FUNZIONA CON REMOTE OBJECT
                    disconnectPlayer(deadClient);
                }
            }
        }
    }

    public int getGameID() {
        return model.getGameId();
    }

    @Override
    public void pong(String client) throws RemoteException {
        this.pingPongThread.pong(client);
    }

    public void addToPingPong(String client) throws RemoteException {
        this.pingPongThread.addClient(client);
    }

    //---------------------------------LISTENERS SECTION
    @Override
    public void addMyselfAsListener(GameListener me, NotifierInterface notifier) throws RemoteException{
        model.addListeners(me, notifier);
    }

    @Override
    public void removeMyselfAsListener(GameListener me) throws RemoteException {
        Runnable runnable = () -> {
            model.removeListener(me);
        };
        executorService.submit(runnable);
    }

    //---------------------------------GAME CREATION PHASE
    @Override
    public void addPlayer(String nickname) {
        Player px;
        int player_number = model.getPlayerNum() + 1;
        px = new Player(nickname, player_number);
        try {
            model.addPlayer(px);
        } catch (GameAlreadyFullException | PlayerAlreadyInException e) {
            e.printStackTrace();
        }
        if (model.getPlayerNum() == model.getMaxNumberOfPlayer()) {
            Runnable runnable = () -> {
                model.askPlayersReady();
            };
            executorService.submit(runnable);
        }
    }

    @Override
    public void setMaxNumberOfPlayer(int num) throws RemoteException {
        model.setMaxNumberOfPlayer(num);
    }

    @Override
    public void ready(String nickname) throws RemoteException {
        synchronized (model) {
            model.setPlayerReady(nickname);
            if (model.getReadyPlayersNum() == model.getPlayerNum()) {

                //create Table
                createTable();
                //random first player
                randomFirstPlayer();
                //run TurnZero
                turnZero();
                //set Running
                model.setStatus(GameStatus.RUNNING);
                //notify ALL
                model.gameReady();
            }
        }
    }

    //---------------------------------PLAYER SECTION
    public Queue<Player> getAllPlayer() {
        return model.getPlayers();
    }

    public Player getCurrentPlayer() {
        return model.getCurrentPlayer();
    }

    public int getMaxNumberOfPlayer() {
        return model.getMaxNumberOfPlayer();
    }

    public void reconnectPlayer(String nickname) {
        model.reconnectPlayer(nickname);
        if (model.getMaxNumberOfPlayer() - model.numberDisconnectedPlayers() > 1) {
            model.setStatus(model.getLastStatus());
            model.resetLastStatus();
        }
    }

    public void disconnectPlayer(String nickname) {
        model.disconnectPlayer(nickname);
        if (model.getMaxNumberOfPlayer() - model.numberDisconnectedPlayers() == 1) {
            model.setLastStatus();
            model.setStatus(GameStatus.WAITING_RECONNECTION);
        }
    }

    public void removePlayer(Player player) {
        Runnable runnable = () -> {
            model.removePlayer(player);
        };
        executorService.submit(runnable);
    }

    public GameStatus getLastStatus() {
        return model.getLastStatus();
    }

    //---------------------------------TABLE AND INIT SECTION
    public void createTable() {
        Map<String, List<Card>> cardsMap = null;
        try {
            cardsMap = JSONUtils.createCardsFromJson(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Queue<Card>> shuffledDecks = new HashMap<>();
        for (Map.Entry<String, List<Card>> entry : cardsMap.entrySet()) {
            String type = entry.getKey(); // Get the card type
            List<Card> cards = entry.getValue(); // Get the list of cards for this type

            // Shuffle the list of cards
            Collections.shuffle(cards);

            // Create a new deck as a Queue
            Queue<Card> deck = new LinkedList<>(cards);

            // Put the shuffled deck into the map
            shuffledDecks.put(type, deck);
        }
        //create decks
        DrawableDeck decks = new DrawableDeck(shuffledDecks);
        BoardDeck boardDeck = new BoardDeck(model);

        //set the BoardDeck
        try {
            boardDeck.setObjectiveCards(decks.drawFirstObjective(), 0);
        } catch (DeckEmptyException e) {
            e.printStackTrace();
        }
        try {
            boardDeck.setObjectiveCards(decks.drawFirstObjective(), 1);
        } catch (DeckEmptyException e) {
            e.printStackTrace();
        }
        try {
            boardDeck.setResourceCards(decks.drawFirstResource(), 0);
        } catch (DeckEmptyException e) {
            e.printStackTrace();
        }
        try {
            boardDeck.setResourceCards(decks.drawFirstResource(), 1);
        } catch (DeckEmptyException e) {
            e.printStackTrace();
        }
        try {
            boardDeck.setGoldCards(decks.drawFirstGold(), 0);
        } catch (DeckEmptyException e) {
            e.printStackTrace();
        }
        try {
            boardDeck.setGoldCards(decks.drawFirstGold(), 1);
        } catch (DeckEmptyException e) {
            e.printStackTrace();
        }

        model.setGameDrawableDeck(decks);
        model.setGameBoardDeck(boardDeck);

    }

    private void randomFirstPlayer() {
        int first = random.nextInt(4);

        for (int i = 0; i < first; i++) {
            model.nextPlayer();
        }

        model.setFirstPlayer(model.getCurrentPlayer());
    }

    private void turnZero() {
        for (Player player : getAllPlayer()) {
            try {
                player.drawStarting(model.getGameDrawableDeck());
            } catch (DeckEmptyException e) {
                e.printStackTrace();
            }
            try {
                player.drawGoals(model.getGameDrawableDeck());
            } catch (DeckEmptyException e) {
                e.printStackTrace();
            }
            try {
                player.drawResourcesFromDeck(model.getGameDrawableDeck());
            } catch (DeckEmptyException e) {
                e.printStackTrace();
            }
            try {
                player.drawResourcesFromDeck(model.getGameDrawableDeck());
            } catch (DeckEmptyException e) {
                e.printStackTrace();
            }
            try {
                player.drawGoldFromDeck(model.getGameDrawableDeck());
            } catch (DeckEmptyException e) {
                e.printStackTrace();
            }
        }

    }


    //---------------------------------ADD CARD SECTION
    @Override
    public void addCard(String nickname, PlayingCard cardToAdd, PlayingCard cardOnBoard, int cornerToAttach, Boolean flip) {
        if (!getCurrentPlayer().getNickname().equals(nickname)){
            model.gameError("Not your turn");
            // listener invalid action
            return;
        }
        if (!(model.getGameStatus().equals(GameStatus.RUNNING) || model.getGameStatus().equals(GameStatus.WAITING_LAST_TURN) || model.getGameStatus().equals(GameStatus.LAST_TURN))) {
            // listener you cannot draw in this phase
            model.gameError("You cannot add a Card in this Phase");
            return;
        }
        if (flip) {
            cardToAdd.flip();
        }
        getCurrentPlayer().addToBoard(cardToAdd, cardOnBoard, cornerToAttach);
        checkPoints20Points();
    }

    @Override
    public void addStartingCard(String nickname, Boolean flip) {
        if (!getCurrentPlayer().getNickname().equals(nickname)){
            // listener invalid action
            model.gameError("Not your turn");
            return;
        }
        if (!(model.getGameStatus().equals(GameStatus.RUNNING) || model.getGameStatus().equals(GameStatus.WAITING_LAST_TURN) || model.getGameStatus().equals(GameStatus.LAST_TURN))) {
            // listener you cannot draw in this phase
            model.gameError("You cannot add a Starting Card in this Phase");
            return;
        }
        if (flip) {
            getCurrentPlayer().getStartingCard().flip();
        }
        getCurrentPlayer().addStarting();
    }

    @Override
    public void choosePlayerGoal(String nickname, int choice) {
        if (!getCurrentPlayer().getNickname().equals(nickname)){
            // listener invalid action
            model.gameError("Not your turn");
            return;
        }
        if (!(model.getGameStatus().equals(GameStatus.RUNNING))) {
            // listener you cannot draw in this phase
            model.gameError("You cannot choose the Objective Card in this Phase");
            return;
        }
        getCurrentPlayer().chooseGoal(choice);
    }

    //---------------------------------DRAW SECTION
    private boolean decksAreAllEmpty() {
        return model.getGameDrawableDeck().getDecks().get("resources").isEmpty()
                && model.getGameDrawableDeck().getDecks().get("gold").isEmpty()
                && model.getGameBoardDeck().isEmpty();
    }

    @Override
    public void drawResourceFromDeck(String nickname) {
        if (!getCurrentPlayer().getNickname().equals(nickname)){
            // listener invalid action
            model.gameError("Not your turn");
            return;
        }
        if (!(model.getGameStatus().equals(GameStatus.RUNNING) || model.getGameStatus().equals(GameStatus.WAITING_LAST_TURN))) {
            // listener you cannot draw in this phase
            model.gameError("You cannot draw a Resource Card in this phase");
            return;
        }
        if (decksAreAllEmpty()) {
            model.setStatus(GameStatus.WAITING_LAST_TURN);
        }
        else {
            try {
                getCurrentPlayer().drawResourcesFromDeck(model.getGameDrawableDeck());
                model.nextPlayer();
            } catch (DeckEmptyException e) {
                // listener change deck
                model.gameError("Resource deck is empty");
            }
        }
    }

    @Override
    public void drawGoldFromDeck(String nickname) {
        if (!getCurrentPlayer().getNickname().equals(nickname)){
            // listener invalid action
            model.gameError("Not your turn");
            return;
        }
        if (!(model.getGameStatus().equals(GameStatus.RUNNING) || model.getGameStatus().equals(GameStatus.WAITING_LAST_TURN))) {
            // listener you cannot draw in this phase
            model.gameError("You cannot draw a Gold Card in this phase");
            return;
        }
        if (decksAreAllEmpty()) {
            model.setStatus(GameStatus.WAITING_LAST_TURN);

        } else {
            try {
                getCurrentPlayer().drawGoldFromDeck(model.getGameDrawableDeck());
                model.nextPlayer();
            } catch (DeckEmptyException e) {
                // listener change deck
                model.gameError("Gold deck is empty");
            }
        }
    }

    @Override
    public void drawFromBoard(String nickname, int position) {
        if (!getCurrentPlayer().getNickname().equals(nickname)){
            // listener invalid action
            model.gameError("Not your turn");
            return;
        }
        if (!(model.getGameStatus().equals(GameStatus.RUNNING) || model.getGameStatus().equals(GameStatus.WAITING_LAST_TURN))) {
            // listener you cannot draw in this phase
            model.gameError("You cannot draw from Common Board in this phase");
            return;
        }
        if (decksAreAllEmpty()) {
            model.setStatus(GameStatus.WAITING_LAST_TURN);

        } else {
            try {
                getCurrentPlayer().drawFromBoard(position, model.getGameBoardDeck());
                model.nextPlayer();
            } catch (NoCardException e) {
                model.gameError("This position is empty");
            }
        }
    }

    //---------------------------------CHAT
    @Override
    public void sendMessage(String txt, String nickname) {
        model.sendMessage(txt,nickname);
    }

    @Override
    public void sendPrivateMessage(String senderName, String recieverName, String txt) {
        model.sendPrivateMessage(senderName,recieverName,txt);
    }

    @Override
    public void getPublicChatLog(String requesterName) throws RemoteException {
        model.getPublicChatLog(requesterName);
    }

    @Override
    public void getPrivateChatLog(String yourName, String otherName) throws RemoteException {
        model.getPrivateChatLog(yourName,otherName);
    }


    //---------------------------------GAME ID
    @Override
    public int getGameId() {
        return model.getGameId();
    }


    //---------------------------------END SECTION
    private void checkPoints20Points() {
        for (Player player : getAllPlayer()) {
            // ATTENZIONE: aggiornare il currentPlayer a fine turno, prima di chiamare questa funzione
            if (player.getCurrentPoints() >= 20) {
                model.setStatus(GameStatus.WAITING_LAST_TURN);
            }
        }
    }

    public void checkWinner() {
        //model.checkWinner();
        model.setStatus(GameStatus.ENDED);
    }

    //---------------------------------GET SECTION TO DISPLAY THE PUBLIC PART
    public Game getGame() {
        return model;
    }

    public GameImmutable getImmutableGame() {
        return new GameImmutable(model);
    }

}
