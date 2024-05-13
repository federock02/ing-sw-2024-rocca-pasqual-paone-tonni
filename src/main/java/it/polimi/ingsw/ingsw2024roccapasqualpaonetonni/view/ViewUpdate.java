package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.view;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.immutable.GameImmutable;

public interface ViewUpdate {
    void joinLobby();
    void show_All(GameImmutable gameImmutable, String nickname);

    void show_maxNumPlayersSet(int max);
    void show_createdGame(int gameID);
    void show_youJoinedGame(int gameID);
    void show_noAvailableGame();
    void show_addedNewPlayer(String pNickname);
    void show_areYouReady();

    void invalidMessage();
    void invalidMessage(String s);

    void myRunningTurnChooseObjective();
    void myRunningTurnPlaceStarting();
    void myRunningTurnDrawCard();
    void myRunningTurnPlaceCard();
    void notMyTurn();

    void displayChat(String s);

}
