package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.socket.serverMessages;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.GameListener;

import java.rmi.RemoteException;

/**
 * The type Server message ask player ready.
 */
public class ServerMessageAskPlayerReady extends ServerGenericMessage{

    /**
     * Instantiates a new Server message ask player ready.
     */
    public ServerMessageAskPlayerReady(){
    }

    /**
     * Launch message.
     *
     * @param listener the listener
     * @throws RemoteException the remote exception
     */
    @Override
    public void launchMessage(GameListener listener) throws RemoteException {
        listener.areYouReady();
    }
}
