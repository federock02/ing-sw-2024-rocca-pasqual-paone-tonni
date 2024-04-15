package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.controller;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.*;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.exception.GameAlreadyFullException;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.exception.PlayerAlreadyInException;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.immutable.GameImmutable;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.RMI.remoteinterfaces.GameControllerInterface;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.utils.JSONUtils;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

public class GameController implements GameControllerInterface,Runnable{
    private final Game model;
    private final Random random;
    private final String path;

    public GameController(int id) {
        model = new Game(id);
        random = new Random();
        //new Thread(this).start();
        path = "src/main/java/it/polimi/ingsw/ingsw2024roccapasqualpaonetonni/utils/DataBase";
    }

    @SuppressWarnings("BusyWait")
    public void run() {
        while (!Thread.interrupted()) {
            //some code here
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

//---------------------------------PLAYER SECTION
    public int getID(){
        return model.getGameId();
    }
    @Override
    public void addPlayer(String nickname){
        Player px;
        int player_number = model.getPlayers().size()+1;
        px = new Player(nickname,player_number);
        try {
            model.addPlayer(px);

        }catch (GameAlreadyFullException ex1){/*_*/}
        catch (PlayerAlreadyInException ex2){/**/}

        model.playerIsReadyToStart(px);
    }
    public Queue<Player> getAllPlayer(){
        return model.getPlayers();
    }
    public Player getCurrentPlayer(){
        return model.getCurrentPlayer();
    }
    @Override
    public Boolean isCurrentPlaying(Player p){
        return getCurrentPlayer().equals(p);
    }
    @Override
    public void setMaxNumberOfPlayer(int num) throws RemoteException {
        model.setMaxNumberOfPlayer(num);
    }
    public int getMaxNumberOfPlayer(){
        return model.getMaxNumberOfPlayer();
    }
    public void nextTurn(){
        model.nextPlayer();
    }
    public void reconnectPlayer(String nickname) {
        model.reconnectPlayer(nickname);
        model.setStatus(model.getLastStatus());
        model.resetLastStatus();
    }
    public void disconnectPlayer(String nickname) {
        model.disconnectPlayer(nickname);
        model.setLastStatus();
        model.setStatus(GameStatus.WAITING_RECONNECTION);
    }
    @Override
    public void removePlayer(Player player){
        model.removePlayer(player);
    }
    public GameStatus getGameStatus(){
        return model.getGameStatus();
    }
    public GameStatus getLastStatus(){
        return  model.getLastStatus();
    }
    public Boolean playersAreReady(){
        return model.arePlayerReady();
    }


//---------------------------------TABLE AND INIT SECTION
    @Override
    public Boolean createTable(){
        if(model.arePlayerReady()) {
            Map<String, List<Card>> cardsMap = null;
            try {
                cardsMap = JSONUtils.createCardsFromJson(path);
            }
            catch (IOException e) {
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
            BoardDeck boardDeck = new BoardDeck();

            //set the BoardDeck
            boardDeck.setObjectiveCards(decks.drawFirstObjective(),0);
            boardDeck.setObjectiveCards(decks.drawFirstObjective(),1);
            boardDeck.setResourceCards(decks.drawFirstResource(),0);
            boardDeck.setResourceCards(decks.drawFirstResource(),1);
            boardDeck.setGoldCards(decks.drawFirstGold(),0);
            boardDeck.setGoldCards(decks.drawFirstGold(),1);

            model.setGameDrawableDeck(decks);
            model.setGameBoardDeck(boardDeck);

            //random first player
            randomFirstPlayer();

            //run TurnZero
            turnZero();

            model.setStatus(GameStatus.RUNNING);
            return true;
        }
        else return false;
    }
    private void randomFirstPlayer(){
        int first = random.nextInt(4);

        for(int i=0; i<first; i++){
            model.nextPlayer();
        }

        model.setFirstPlayer(model.getCurrentPlayer());
    }
    private void turnZero() {
        for(Player player : getAllPlayer()){
            player.drawStarting(model.getGameDrawableDeck());
            player.drawGoals(model.getGameDrawableDeck());
            player.drawResourcesFromDeck(model.getGameDrawableDeck());
            player.drawResourcesFromDeck(model.getGameDrawableDeck());
            player.drawGoldFromDeck(model.getGameDrawableDeck());
        }
    }


//---------------------------------ADD CARD SECTION
    public void addCard(PlayingCard cardToAdd, PlayingCard cardOnBoard, int cornerToAttach, Boolean flip){
        if(!(model.getGameStatus().equals(GameStatus.RUNNING) || model.getGameStatus().equals(GameStatus.WAITING_LAST_TURN) || model.getGameStatus().equals(GameStatus.LAST_TURN))){
            // listener you cannot draw in this phase
            return;
        }
        if(flip){
            cardToAdd.flip();
        }
        getCurrentPlayer().addToBoard(cardToAdd,cardOnBoard,cornerToAttach);
        checkPoints20Points();
    }
    public void addStartingCard(Boolean flip){
        if(!(model.getGameStatus().equals(GameStatus.RUNNING) || model.getGameStatus().equals(GameStatus.WAITING_LAST_TURN) || model.getGameStatus().equals(GameStatus.LAST_TURN))){
            // listener you cannot draw in this phase
            return;
        }
        if(flip){
            getCurrentPlayer().getStartingCard().flip();
        }
        getCurrentPlayer().addStarting();
    }
    public void choosePlayerGoal(int choice){
        if(!(model.getGameStatus().equals(GameStatus.RUNNING))){
            // listener you cannot draw in this phase
            return;
        }
        getCurrentPlayer().chooseGoal(choice);
    }


//---------------------------------DRAW SECTION
    private boolean decksAreAllEmpty() {
        return model.getGameDrawableDeck().getDecks().get("resources").isEmpty()
                && model.getGameDrawableDeck().getDecks().get("gold").isEmpty()
                && model.getGameBoardDeck().getResourceCards()[0] == null
                && model.getGameBoardDeck().getResourceCards()[1] == null
                && model.getGameBoardDeck().getGoldCards()[0] == null
                && model.getGameBoardDeck().getGoldCards()[1] == null;
    }
    public void drawResourceFromDeck(){
        if(!(model.getGameStatus().equals(GameStatus.RUNNING) || model.getGameStatus().equals(GameStatus.WAITING_LAST_TURN) )){
            // listener you cannot draw in this phase
            return;
        }
        if (decksAreAllEmpty()) {
            model.setStatus(GameStatus.WAITING_LAST_TURN);

        }
        else if(!model.getGameDrawableDeck().getDecks().get("resources").isEmpty()){
            getCurrentPlayer().drawResourcesFromDeck(model.getGameDrawableDeck());
        }
        else {
            // listener change deck
            return;
        }

    }
    public void drawGoldFromDeck(){
        if(!(model.getGameStatus().equals(GameStatus.RUNNING) || model.getGameStatus().equals(GameStatus.WAITING_LAST_TURN) )){
            // listener you cannot draw in this phase
            return;
        }
        if (decksAreAllEmpty()) {
            model.setStatus(GameStatus.WAITING_LAST_TURN);

        }
        else if(!model.getGameDrawableDeck().getDecks().get("gold").isEmpty()) {
            getCurrentPlayer().drawGoldFromDeck(model.getGameDrawableDeck());
        }
        else {
            // listener change deck
            return;
        }
    }
    public void drawFromBoard(int position){
        if(!(model.getGameStatus().equals(GameStatus.RUNNING) || model.getGameStatus().equals(GameStatus.WAITING_LAST_TURN) )){
            // listener you cannot draw in this phase
            return;
        }
        if (decksAreAllEmpty()) {
            model.setStatus(GameStatus.WAITING_LAST_TURN);

        }
        else if((position <= 2 && model.getGameBoardDeck().getResourceCards()[position-1]!=null) ||
                (position >  2 && model.getGameBoardDeck().getGoldCards()[position-3] !=null)) {
            getCurrentPlayer().drawFromBoard(position,model.getGameBoardDeck(),model.getGameDrawableDeck());
        }
        else {
            // listener change deck
            return;
        }
    }


//---------------------------------END SECTION
    private void checkPoints20Points(){
        for(Player player: getAllPlayer()){
            // ATTENZIONE: aggiornare il currentPlayer a fine turno, prima di chiamare questa funzione
            if(player.getCurrentPoints() >= 20){
                model.setStatus(GameStatus.WAITING_LAST_TURN);
            }
        }
    }
    public void checkWinner(){
        model.checkWinner();
        model.setStatus(GameStatus.ENDED);
    }

//---------------------------------GET SECTION TO TEST
    public GameImmutable getImmutableGame(){
        return new GameImmutable(model);
    }

}
