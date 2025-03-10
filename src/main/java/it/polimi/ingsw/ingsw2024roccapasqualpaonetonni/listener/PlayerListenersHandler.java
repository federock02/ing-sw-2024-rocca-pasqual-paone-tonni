package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.listener;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.BoardDeck;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.DrawableDeck;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.Player;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.ConsolePrinter;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.NotifierInterface;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * The type Player listeners handler.
 * this class handles the listeners of the player class in the model: the listeners are elements related to each client, when a change
 * occurs in the model (the controller does something) one of these methods gets called in order to show the
 * change to each of the clients. These methods call other methods on the actual single listener giving it the change occurred
 * these are not the methods that directly notify the clients, they are the ones that call those methods on all the clients.
 */
public class PlayerListenersHandler extends ListenersHandler implements Serializable {

    /**
     * Instantiates a new Player listeners handler.
     */
    public PlayerListenersHandler(){
        super();
    }

    /**
     * Reset player listeners.
     *
     * @param gameListenersMap the game listeners map
     */
    public void resetPlayerListeners(HashMap<String, NotifierInterface> gameListenersMap){
        //listenersMap=null;
        synchronized (listenersMap){
            for (String name : listenersMap.keySet()){
                //ConsolePrinter.consolePrinter("PlayerListenerHandler has listener " + name);
            }
            listenersMap.clear();
            for(String name : gameListenersMap.keySet()){
                try {
                    //ConsolePrinter.consolePrinter("PlayerListenerHandler adding " + name);
                    addListener(name, gameListenersMap.get(name));
                } catch (RemoteException e) {
                    ConsolePrinter.consolePrinter("[ERROR]: player "+name+" not notified");
                }
            }}
    }

    /**
     * Notify choose goal.
     *
     * @param p the p
     */
    public void notify_chooseGoal(Player p) {
        for(String name : listenersMap.keySet()){
            try {
                listenersMap.get(name).sendPersonalGoalChosen(p);
            } catch (IOException e) {
                ConsolePrinter.consolePrinter("[ERROR]: player "+name+" not notified");
            }
        }
    }

    /**
     * Notify draw gold from deck.
     *
     * @param p the p
     * @param d the d
     */
    public void notify_drawGoldFromDeck(Player p,DrawableDeck d) {
        for(String name : listenersMap.keySet()){
            try {
                listenersMap.get(name).sendGoldDrawn(p,d);
            } catch (Exception e) {
                ConsolePrinter.consolePrinter("[ERROR]: player "+name+" not notified");
            }
        }
    }

    /**
     * Notify draw resource from deck.
     *
     * @param p the p
     * @param d the d
     */
    public void notify_drawResourceFromDeck(Player p,DrawableDeck d) {
        for(String name : listenersMap.keySet()){
            try {
                listenersMap.get(name).sendResourceDrawn(p,d);
            } catch (Exception e) {
                ConsolePrinter.consolePrinter("[ERROR]: player "+name+" not notified");
            }
        }
    }

    /**
     * Notify draw from board.
     *
     * @param p the p
     * @param b the b
     * @param d the d
     */
    public void notify_drawFromBoard(Player p, BoardDeck b, DrawableDeck d) {
        for(String name : listenersMap.keySet()){
            try {
                //ConsolePrinter.consolePrinter("PlayerListenerHandler drewFromBoard " + name);
                listenersMap.get(name).sendDrewFromBoard(p,b,d);
            } catch (Exception e) {
                ConsolePrinter.consolePrinter("[ERROR]: player "+name+" not notified");
            }
        }
    }

    /**
     * Notify add starting.
     *
     * @param p the p
     */
    public void notify_addStarting(Player p) {
        for(String name : listenersMap.keySet()){
            try {
                listenersMap.get(name).sendStartAdded(p);
            } catch (IOException e) {
                ConsolePrinter.consolePrinter("[ERROR]: player "+name+" not notified");
            }
        }
    }

    /**
     * Notify add to board.
     *
     * @param p the p
     */
    public void notify_addToBoard(Player p,int cardID) {
        for(String name : listenersMap.keySet()){
            try {
                listenersMap.get(name).sendCardAdded(p,cardID);
            } catch (IOException e) {
                ConsolePrinter.consolePrinter("[ERROR]: player "+name+" not notified");
            }
        }
    }

    /**
     * Notify player generic error.
     *
     * @param s the s
     */
    public void notify_playerGenericError(String s){
        for(String name : listenersMap.keySet()){
            try {
                listenersMap.get(name).genericError(s);
            } catch (IOException e) {
                ConsolePrinter.consolePrinter("[ERROR]: player "+name+" not notified");
            }
        }
    }
}
