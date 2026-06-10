package shared.command;

import shared.network.Response;

public class ServerSaveCommand extends Command {
    public ServerSaveCommand() {
        super(CommandType.SERVER_SAVE, "сохранить коллекцию (данные автоматически синхронизированы с БД)");
    }

    @Override
    public Response execute(CommandContext ctx) {
        return Response.success("Коллекция автоматически сохранена в базе данных. Ручное сохранение не требуется.");
    }
}