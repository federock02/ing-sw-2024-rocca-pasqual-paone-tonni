package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.listener.GameListener;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.listener.GameListenersHandler;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.listener.ListenersHandler;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.exception.GameAlreadyFullException;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.exception.PlayerAlreadyInException;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.chat.*;

import java.util.*;

public class Game {
    private final int gameId;
    private final GameStatus[] status; //current and previous status needed for reconnection
    private int maxNumberOfPlayer;
    private final Queue<Player> players;
    private final Queue<Player> winner;
    private Player firstPlayer;
    private BoardDeck gameBoardDeck;
    private DrawableDeck gameDrawableDeck;
    private final Chat chat;

    private GameListenersHandler gameListenersHandler;
    public Game(int id){
        players = new LinkedList<>();
        winner = new LinkedList<>();
        firstPlayer = null;
        status = new GameStatus[2];
        status[0] = GameStatus.PREPARATION;
        status[1] = GameStatus.PREPARATION;
        this.maxNumberOfPlayer=0;
        this.gameId = id;

        gameBoardDeck = null;
        gameDrawableDeck = null;

        chat = new Chat();
        gameListenersHandler = null;
    }

    public void addListeners(GameListener me){
        gameListenersHandler.addListener(me);
    }
    public void removeListener(GameListener me){
        gameListenersHandler.removeListener(me);
        for(Player p: players){
            p.setPlayerListeners((List<GameListener>) gameListenersHandler);
        }
    }
//---------------------------------PLAYER SECTION
    public int getGameId(){return  gameId;}
    public void setMaxNumberOfPlayer(int number){

        this.maxNumberOfPlayer=number;
        gameListenersHandler.notify_setMaxNumPlayers(gameId,maxNumberOfPlayer);
    }
    public int getMaxNumberOfPlayer(){
        return maxNumberOfPlayer;
    }
    public void addPlayer (Player px) throws GameAlreadyFullException, PlayerAlreadyInException {
        if(!players.contains(px)){
            if(players.size() < maxNumberOfPlayer){
                players.add(px);
                for(Player p: players){
                    p.setPlayerListeners((List<GameListener>) gameListenersHandler);
                }
                gameListenersHandler.notify_addPlayer(px);
            }
            else {
                gameListenersHandler.notify_gameFull();
                throw new GameAlreadyFullException("The game is full");
            }
        }
        else {
            gameListenersHandler.notify_playerAlredyIn();
            throw new PlayerAlreadyInException("The player is alrady in");
        }

    }
    public void removePlayer(Player p){
        players.remove(p);
        if(status[0].equals(GameStatus.RUNNING) || status[0].equals(GameStatus.LAST_TURN)){
            status[0] = GameStatus.ENDED;
        }
        //here before calling this method the client should call removeListener to remove itself from the listeners list, or the server should
        gameListenersHandler.notify_removePlayer(p);
    }
    public void reconnectPlayer(String nickname) {
        Player p = players.stream().filter(player -> Objects.equals(player.getNickname(), nickname)).findFirst().orElse(null);
        if(p!=null){
            p.setIsConnected(true);
            gameListenersHandler.notify_reconnectPlayer(nickname);
        }
        else {
            gameListenersHandler.notify_reconnectionImpossible(nickname);
        }
    }
    public void disconnectPlayer(String nickname) {
        Player p = players.stream().filter(player -> Objects.equals(player.getNickname(), nickname)).findFirst().orElse(null);
        if(p!=null){
            p.setIsConnected(false);
            gameListenersHandler.notify_disconnectedPlayer(nickname);
        }
        else {
            gameListenersHandler.notify_disconnectionImpossible(nickname);
        }
    }
    public void setFirstPlayer(Player fp){

        this.firstPlayer=fp;
        gameListenersHandler.notify_setFirstPlayer(fp);
    }
    public void setStatus(GameStatus status) {

        this.status[0] = status;
        gameListenersHandler.notify_setStatus(status);
    }
    public void setLastStatus() {

        status[1] =status[0];
        gameListenersHandler.notify_setLastStatus(status[0]);
    }
    public void resetLastStatus() {

        status[1] = null;
        gameListenersHandler.notify_resetLastStatus();
    }
    public GameStatus getGameStatus(){

        return status[0];
    }
    public GameStatus getLastStatus(){
        return status[1];
    }
    public Queue<Player> getPlayers() {
        return players;
    }
    public Player getCurrentPlayer(){
        return players.peek();
    }
    public DrawableDeck getGameDrawableDeck(){
        return gameDrawableDeck;
    }
    public BoardDeck getGameBoardDeck(){
        return gameBoardDeck;
    }
    public void nextPlayer(){
        Player temp;
        temp = players.poll();
        players.add(temp);
        Player newCurrent = players.peek();
        if (newCurrent != null && newCurrent.equals(firstPlayer) && status[0].equals(GameStatus.WAITING_LAST_TURN)) {
            status[0] = GameStatus.LAST_TURN;
            gameListenersHandler.notify_lastTurn();
        }
        gameListenersHandler.notify_nextPlayer(newCurrent);
    }

//---------------------------------POINT SECTION
    public int checkPlayerTotalPoint(Player p){
        return p.getCurrentPoints()
                + p.getGoal().pointCard(p.getBoard())
                + gameBoardDeck.getCommonObjective(0).pointCard(p.getBoard())
                + gameBoardDeck.getCommonObjective(1).pointCard(p.getBoard())
                ;
    }
    public void checkWinner(){
       int max=0;
        for (Player cplayer : players ){
            int p_point = checkPlayerTotalPoint(cplayer);
            //2 players with equal point
            if(p_point == max){
                winner.add(cplayer);
            }
            //winner
            else if(p_point > max) {
                winner.clear();
                winner.add(cplayer);
                max= p_point;
            }
        }
    }

    public Queue<Player> getWinners(){
        return winner;
    }

//---------------------------------READY SECTION
    public void playerIsReadyToStart(Player p){

        p.setReadyToStart();
        gameListenersHandler.notify_playerIsReadyToStart(p);
    }
    public Boolean arePlayerReady(){
        return players.stream().filter(Player::getReadyToStart).count() == players.size()
                && players.size() == maxNumberOfPlayer;
    }

    public void setGameDrawableDeck(DrawableDeck deck) {

        this.gameDrawableDeck = deck;
        gameListenersHandler.notify_setGameDrawableDeck(deck);
    }

    public void setGameBoardDeck(BoardDeck deck) {

        this.gameBoardDeck = deck;
        gameListenersHandler.notify_setGameBoardDeck(deck);
    }

//---------------------------------CHAT SECTION

    public Chat getChat() {
        return chat;
    }
}
