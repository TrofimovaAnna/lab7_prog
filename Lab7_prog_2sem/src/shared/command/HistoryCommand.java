package shared.command;

import shared.network.Response;

public class HistoryCommand extends Command {
    public HistoryCommand() {
        super(CommandType.HISTORY, "вывести последние 13 команд");
    }

    @Override
    public Response execute(CommandContext ctx) {
        var history = ctx.getHistoryManager().getHistory();
        if (history.isEmpty()) return Response.success("История пустая");
        return Response.success("История команд:\n" + String.join("\n", history));
    }
}