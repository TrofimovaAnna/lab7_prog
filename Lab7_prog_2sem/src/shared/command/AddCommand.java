package shared.command;

import shared.model.Coordinates;
import shared.model.Ticket;
import shared.model.TicketType;
import shared.model.Venue;
import shared.model.VenueType;
import shared.network.Response;
import server.CollectionManager;
import java.io.Serializable;
import java.time.LocalDateTime;

public class AddCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Coordinates coordinates;
    private final float price;
    private final long discount;
    private final TicketType ticketType;
    private final String venueName;
    private final Long venueCapacity;
    private final VenueType venueType;

    public AddCommand(String name, Coordinates coordinates, float price,
                      long discount, TicketType ticketType,
                      String venueName, Long venueCapacity, VenueType venueType) {
        super(CommandType.ADD, "добавить новый элемент в коллекцию");
        this.name = name; this.coordinates = coordinates; this.price = price;
        this.discount = discount; this.ticketType = ticketType;
        this.venueName = venueName; this.venueCapacity = venueCapacity; this.venueType = venueType;
    }

    public AddCommand() {
        super(CommandType.ADD, "добавить новый элемент в коллекцию");
        this.name = null; this.coordinates = null; this.price = 0;
        this.discount = 0; this.ticketType = null;
        this.venueName = null; this.venueCapacity = null; this.venueType = null;
    }

    @Override
    public Response execute(CommandContext ctx) {
        try {
            CollectionManager cm = ctx.getCollectionManager();
            String owner = ctx.getCurrentUser();

            Venue venue = null;
            if (venueName != null && venueType != null) {
                // ID генерируется БД, передаём временный 1L
                venue = new Venue(1L, venueName, venueCapacity != null ? venueCapacity : 1L, venueType);
            }

            // ID генерируется БД, передаём временный 1L
            Ticket newTicket = new Ticket(
                    1L, name, coordinates,
                    LocalDateTime.now(), price, discount, ticketType, venue
            );

            if (cm.add(newTicket, owner)) {
                ctx.getHistoryManager().addCommand(getType().name());
                return Response.success("Билет успешно добавлен.");
            }
            return Response.error("Не удалось добавить билет.");
        } catch (Exception e) {
            return Response.error("Ошибка: " + e.getMessage());
        }
    }
}