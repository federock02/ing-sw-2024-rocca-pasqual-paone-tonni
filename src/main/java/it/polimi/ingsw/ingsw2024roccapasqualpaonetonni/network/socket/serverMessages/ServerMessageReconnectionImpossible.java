package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.socket.serverMessages;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.GameListener;

import java.rmi.RemoteException;

public class ServerMessageReconnectionImpossible extends ServerGenericMessage{
    private final String nickname;

    public ServerMessageReconnectionImpossible(String nickname) {
        this.nickname = nickname;
    }
    @Override
    public void launchMessage(GameListener listener) {
        try {
            listener.reconnectionImpossible(nickname);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
