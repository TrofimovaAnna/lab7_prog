package shared.command;

import shared.network.Response;
import java.io.Serializable;
import java.util.Objects;

public abstract class Command implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final CommandType type;
    protected final String description;

    public Command(CommandType type, String description) {
        this.type = type;
        this.description = description;
    }

    public CommandType getType() { return type; }
    public String getDescription() { return description; }

    public abstract Response execute(CommandContext context);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Command command)) return false;
        return type == command.type && Objects.equals(description, command.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, description);
    }
}