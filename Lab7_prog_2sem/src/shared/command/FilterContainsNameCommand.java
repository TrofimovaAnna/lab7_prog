package shared.command;

import shared.model.Ticket;
import shared.network.Response;

import java.util.List;
import java.util.stream.Collectors;

public class FilterContainsNameCommand extends Command {
    private final String substring;

    public FilterContainsNameCommand(String substring) {
        super(CommandType.FILTER_CONTAINS_NAME, "фильтр по подстроке в name");
        this.substring = substring;
    }

    @Override
    public Response execute(CommandContext ctx) {
        String lower = substring.toLowerCase();

        List<String> result = ctx.getCollectionManager().getCollection().stream()
                .filter(t -> (t.getName() != null && t.getName().toLowerCase().contains(lower)) ||
                        (t.getVenue() != null && t.getVenue().getName() != null &&
                                t.getVenue().getName().toLowerCase().contains(lower)))
                .sorted()
                .map(Ticket::toString)
                .collect(Collectors.toList());

        return result.isEmpty()
                ? Response.error("Билеты не найдены.")
                : Response.success("Найдено: " + result.size(), result);
    }
}