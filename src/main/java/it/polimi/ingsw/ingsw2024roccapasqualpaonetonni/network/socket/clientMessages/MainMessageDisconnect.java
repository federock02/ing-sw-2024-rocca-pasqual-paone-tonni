package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.socket.clientMessages;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.RMI.remoteinterfaces.GameControllerInterface;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.RMI.remoteinterfaces.MainControllerInterface;

import java.rmi.RemoteException;

public class MainMessageDisconnect extends ClientGenericMessage{
    int idToDisconnect;

    public MainMessageDisconnect(String nickname, int idToDisconnect){
        this.nickname = nickname;
        this.isForMainController = true;
        this.idToDisconnect = idToDisconnect;
    }

    @Override
    public GameControllerInterface launchMessage(MainControllerInterface mainControllerInterface) throws RemoteException {
        return mainControllerInterface.leaveGame(nickname,idToDisconnect);
    }

    @Override
    public void launchMessage(GameControllerInterface gameControllerInterface) {

    }
}
