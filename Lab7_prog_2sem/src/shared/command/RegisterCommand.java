package shared.command;

import shared.network.Response;
import java.io.Serializable;

public class RegisterCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String username;
    private final String passwordHash;

    public RegisterCommand(String username, String passwordHash) {
        super(CommandType.REGISTER, "зарегистрировать нового пользователя");
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @Override
    public Response execute(CommandContext ctx) {
        return null;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
}