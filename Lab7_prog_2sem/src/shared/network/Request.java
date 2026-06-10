package shared.network;

import shared.command.Command;
import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Command command;
    private final String username;
    private final String passwordHash;

    public Request(Command command, String username, String passwordHash) {
        this.command = command;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public Command getCommand() {
        return command;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}