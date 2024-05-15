package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.socket.clientMessages;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.controller.controllerInterface.GameControllerInterface;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.controller.controllerInterface.MainControllerInterface;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.network.NotifierInterface;

import java.rmi.RemoteException;

/**
 * The type Message add starting.
 */
public class MessageAddStarting extends ClientGenericMessage{
    /**
     * The Flip.
     */
    private final Boolean flip;

    /**
     * Instantiates a new Message add starting.
     *
     * @param nickname the nickname
     * @param flip     the flip
     */
    public MessageAddStarting(String nickname, Boolean flip){
        this.flip=flip;
        this.isForMainController = false;
        this.nickname = nickname;
    }


    @Override
    public GameControllerInterface launchMessage(MainControllerInterface mainControllerInterface, NotifierInterface notifier) throws RemoteException {
        return null;
    }

    @Override
    public void launchMessage(GameControllerInterface gameControllerInterface) throws RemoteException {
        gameControllerInterface.addStartingCard(nickname,flip);
    }
}
