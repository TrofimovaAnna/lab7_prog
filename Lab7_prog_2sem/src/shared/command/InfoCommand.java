package shared.command;

import shared.network.Response;

public class InfoCommand extends Command {
    public InfoCommand() {
        super(CommandType.INFO, "информация о коллекции");
    }

    @Override
    public Response execute(CommandContext ctx) {
        return Response.success(ctx.getCollectionManager().getInfo());
    }
}