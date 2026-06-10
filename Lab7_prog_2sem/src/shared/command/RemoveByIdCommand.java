package shared.command;

import shared.network.Response;

public class RemoveByIdCommand extends Command {
    private final long id;

    public RemoveByIdCommand(long id) {
        super(CommandType.REMOVE_BY_ID, "удалить элемент по ID");
        this.id = id;
    }

    public long getId() { return id; }

    @Override
    public Response execute(CommandContext ctx) {
        if (ctx.getCollectionManager().removeById(id, ctx.getCurrentUser())) {
            ctx.getHistoryManager().addCommand(getType().name());
            return Response.success("Элемент с ID " + id + " удалён.");
        }
        return Response.error("Элемент не найден или нет прав на удаление.");
    }
}