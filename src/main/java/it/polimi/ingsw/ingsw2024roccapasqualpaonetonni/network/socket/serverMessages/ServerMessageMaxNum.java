package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.socket.serverMessages;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view.GameListener;

import java.rmi.RemoteException;

public class ServerMessageMaxNum extends ServerGenericMessage{
    private final int max;
    public ServerMessageMaxNum(int m){
        max=m;
    }

    @Override
    public void launchMessage(GameListener listener) {
        try {
            listener.maxNumPlayersSet(max);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
