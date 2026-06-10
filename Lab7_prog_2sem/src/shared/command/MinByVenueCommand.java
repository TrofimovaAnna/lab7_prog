package shared.command;

import shared.network.Response;

public class MinByVenueCommand extends Command {
    public MinByVenueCommand() {
        super(CommandType.MIN_BY_VENUE, "элемент с минимальным venue");
    }

    @Override
    public Response execute(CommandContext ctx) {
        var ticket = ctx.getCollectionManager().getMinByVenue();
        return (ticket != null)
                ? Response.success("Минимальный venue:\n" + ticket)
                : Response.error("В коллекции нет билетов с venue");
    }
}