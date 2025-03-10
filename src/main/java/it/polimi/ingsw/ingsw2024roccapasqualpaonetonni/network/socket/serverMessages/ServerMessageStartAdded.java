package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.socket.serverMessages;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.Player;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.ConsolePrinter;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.GameListener;

import java.rmi.RemoteException;

/**
 * The type Server message start added.
 */
public class ServerMessageStartAdded extends ServerGenericMessage{
    /**
     * The Player.
     */
    private final Player player;

    /**
     * Instantiates a new Server message start added.
     *
     * @param player the player
     */
    public ServerMessageStartAdded(Player player){
        this.player=player;
    }

    /**
     * Launch message.
     *
     * @param listener the listener
     */
    @Override
    public void launchMessage(GameListener listener) {
        try {
            listener.startAdded(player);
        }
        catch (RemoteException e) {
            ConsolePrinter.consolePrinter("[ERROR]: message failed");
        }
    }
}
