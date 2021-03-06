package weber.kaden.ticketToRide.ui.gameList;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import weber.kaden.common.results.Results;
import weber.kaden.common.model.Game;
import weber.kaden.common.model.Model;
import weber.kaden.ticketToRide.model.ClientFacade;

public class GameListPresenter implements Observer {
    private GameListViewInterface activity;
    private ClientFacade client;
    private Model model = Model.getInstance();
    public GameListPresenter(GameListActivity activity, ClientFacade client) {
        this.activity = activity;
        this.client = client;
        model.addObserver(this);
    }

    public List<Game> displayGames() {
        return model.getGames();
    }

    public Game createGame(String username, String gameName) throws Exception {
        Results results = client.createGame(username, gameName);

        return (Game) results.getData();
    }

    public void joinGame(String username, String gameID) throws Exception {
        client.joinGame(username, gameID);
    }


    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof ArrayList<?> && ((ArrayList<Game>)arg).size() > 0 && ((ArrayList<Game>)arg).get(0) instanceof Game) {
            List<Game> games = (ArrayList<Game>) arg;
            activity.updateGamesList(games);
        }
    }
}
