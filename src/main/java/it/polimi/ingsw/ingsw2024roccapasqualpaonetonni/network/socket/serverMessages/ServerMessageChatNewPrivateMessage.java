package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.socket.serverMessages;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.chat.PrivateMessage;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.GameListener;

import java.rmi.RemoteException;

/**
 * The type Server message chat new private message.
 */
public class ServerMessageChatNewPrivateMessage extends ServerGenericMessage{
    /**
     * The Message.
     */
    private final PrivateMessage message;

    /**
     * Instantiates a new Server message chat new private message.
     *
     * @param message the message
     */
    public ServerMessageChatNewPrivateMessage(PrivateMessage message){
        this.message = message;
    }

    /**
     * Launch message.
     *
     * @param listener the listener
     */
    @Override
    public void launchMessage(GameListener listener) {
        try {
            if(listener.getNickname().equals(message.getReceiver()) || listener.getNickname().equals(message.getSender())){
                listener.newPrivateMessage(message);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
