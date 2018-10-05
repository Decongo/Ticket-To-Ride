package weber.kaden.common.command;



import java.util.List;

import weber.kaden.common.Results;
import weber.kaden.common.model.Model;

public class StartGameCommand implements Command {

    private String playerID = null;
    private String gameID = null;

    public StartGameCommand(List<String> params) {
        this.playerID = params.get(0);
        this.gameID = params.get(1);
    }

    @Override
    public Results execute() {
        return Model.getInstance().getGame(gameID).start();
    }
}
