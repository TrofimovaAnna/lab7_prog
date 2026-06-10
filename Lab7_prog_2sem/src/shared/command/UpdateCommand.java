package shared.command;

import shared.model.Coordinates;
import shared.model.Ticket;
import shared.model.TicketType;
import shared.model.Venue;
import shared.model.VenueType;
import shared.network.Response;
import server.CollectionManager;
import java.io.Serializable;

public class UpdateCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long id; private final String name; private final Coordinates coordinates;
    private final float price; private final long discount; private final TicketType ticketType;
    private final String venueName; private final Long venueCapacity; private final VenueType venueType;

    public UpdateCommand(long id, String name, Coordinates coordinates, float price, long discount, TicketType ticketType,
                         String venueName, Long venueCapacity, VenueType venueType) {
        super(CommandType.UPDATE, "обновить значение элемента коллекции, id которого равен заданному");
        this.id = id; this.name = name; this.coordinates = coordinates; this.price = price;
        this.discount = discount; this.ticketType = ticketType;
        this.venueName = venueName; this.venueCapacity = venueCapacity; this.venueType = venueType;
    }

    @Override
    public Response execute(CommandContext ctx) {
        try {
            CollectionManager cm = ctx.getCollectionManager();
            String owner = ctx.getCurrentUser();
            Ticket oldTicket = cm.getCollection().stream()
                    .filter(t -> t.getId() == id).findFirst().orElse(null);

            if (oldTicket == null) return Response.error("Элемент с ID " + id + " не найден в коллекции.");

            Venue venue = null;
            if (venueName != null && venueCapacity != null && venueType != null) {
            }

            Ticket updatedTicket = new Ticket(
                    id, name, coordinates, oldTicket.getCreationDate(),
                    price, discount, ticketType, venue
            );

            if (cm.update(id, updatedTicket, owner)) {
                ctx.getHistoryManager().addCommand(getType().name());
                return Response.success("Элемент с ID " + id + " успешно обновлён.");
            } else {
                return Response.error("Не удалось обновить элемент с ID " + id + " или нет прав.");
            }
        } catch (Exception e) {
            return Response.error("Ошибка: " + e.getMessage());
        }
    }
}
