package shared.command;

import shared.network.Response;
import java.io.Serializable;

public class ClearCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;

    public ClearCommand() {
        super(CommandType.CLEAR, "удалить ваши объекты из коллекции");
    }

    @Override
    public Response execute(CommandContext ctx) {
        String owner = ctx.getCurrentUser();
        ctx.getCollectionManager().clear(owner);
        ctx.getHistoryManager().addCommand(getType().name());
        return Response.success("Ваши объекты удалены из коллекции.");
    }
}