package weber.kaden.common.command;

import java.util.List;

import weber.kaden.common.Results;
import weber.kaden.common.model.Game;

public class PollGamesListCommand implements Command {

    private String username;
    private iCommandManager commandManager;

    public PollGamesListCommand(List<String> params, iCommandManager commandManager) {
        this.username = params.get(0);
        this.commandManager = commandManager;
    }

    @Override
    public Results execute() {
        List<Game> games = commandManager.gamesList();
        if (games == null) {
            return new Results(null, false, "incorrect polltype, please use: 'gamesList' or 'currentGame'");
        } else {
        	return new Results(games, true, "");
        }
    }
}