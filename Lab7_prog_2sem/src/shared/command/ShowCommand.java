package shared.command;

import shared.model.Ticket;
import shared.network.Response;

import java.util.List;

public class ShowCommand extends Command {
    public ShowCommand() {
        super(CommandType.SHOW, "вывести все элементы коллекции");
    }

    @Override
    public Response execute(CommandContext ctx) {
        List<String> tickets = ctx.getCollectionManager().getCollection().stream()
                .sorted()
                .map(Ticket::toString)
                .toList();

        return tickets.isEmpty()
                ? Response.success("Коллекция пуста.")
                : Response.success("Элементы коллекции:", tickets);
    }
}