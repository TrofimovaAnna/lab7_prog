package shared.command;

import shared.network.Response;

public class RemoveFirstCommand extends Command {
    public RemoveFirstCommand() {
        super(CommandType.REMOVE_FIRST, "удалить первый элемент");
    }

    @Override
    public Response execute(CommandContext ctx) {
        if (ctx.getCollectionManager().removeFirst(ctx.getCurrentUser())) {
            ctx.getHistoryManager().addCommand(getType().name());
            return Response.success("Первый ваш элемент удалён.");
        }
        return Response.error("Коллекция пуста или первый элемент не ваш.");
    }
}