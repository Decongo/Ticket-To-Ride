package weber.kaden.myapplication.ui.gameLobby;

import java.util.List;

import weber.kaden.common.model.Player;

public interface GameLobbyViewInterface {
    void setupGame();
    void updatePlayersList(List<Player> players);
}
