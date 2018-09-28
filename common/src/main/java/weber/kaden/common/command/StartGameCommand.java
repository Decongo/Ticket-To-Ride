package weber.kaden.common.command;


import main.java.weber.kaden.common.Results;

import java.util.List;

public class StartGameCommand implements Command {

    private String playerID = null;
    private String gameID = null;

    public StartGameCommand(List<String> params) {
        this.playerID = params.get(0);
        this.gameID = params.get(1);
    }

    @Override
    public Results execute() {
        return null;
    }
}
