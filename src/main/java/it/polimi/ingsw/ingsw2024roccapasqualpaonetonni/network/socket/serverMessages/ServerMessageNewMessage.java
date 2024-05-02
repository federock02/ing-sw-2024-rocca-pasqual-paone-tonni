package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.socket.serverMessages;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.listener.GameListener;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.chat.Message;

import java.rmi.RemoteException;

public class ServerMessageNewMessage extends ServerGenericMessage{
    Message message;
    public ServerMessageNewMessage(Message message){
        this.message = message;
    }
    @Override
    public void launchMessage(GameListener listener) {
        try {
            listener.newMessage(message);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
