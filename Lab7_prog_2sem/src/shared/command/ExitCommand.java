package shared.command;

import shared.network.Response;

public class ExitCommand extends Command {
    public ExitCommand() {
        super(CommandType.EXIT, "завершить программу");
    }

    @Override
    public Response execute(CommandContext ctx) {
        ctx.getCollectionManager().clear(ctx.getCurrentUser());
        ctx.getHistoryManager().addCommand(getType().name());
        return Response.success("Выход осуществлен");
    }
}