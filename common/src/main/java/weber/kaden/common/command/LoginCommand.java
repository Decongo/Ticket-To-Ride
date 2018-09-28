package weber.kaden.common.command;


import main.java.weber.kaden.common.Results;

import java.util.List;

public class LoginCommand implements Command {

    private String username = null;
    private String password = null;

    public LoginCommand(List<String> params) {
        this.username = params.get(0);
        this.password = params.get(1);
    }

    @Override
    public Results execute() {
        return null;
    }
}
