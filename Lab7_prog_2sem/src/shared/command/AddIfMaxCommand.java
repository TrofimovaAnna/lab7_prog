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

public class AddIfMaxCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name; private final Coordinates coordinates; private final float price;
    private final long discount; private final TicketType ticketType;
    private final String venueName; private final Long venueCapacity; private final VenueType venueType;

    public AddIfMaxCommand(String name, Coordinates coordinates, float price,
                           long discount, TicketType ticketType,
                           String venueName, Long venueCapacity, VenueType venueType) {
        super(CommandType.ADD_IF_MAX, "добавить новый элемент, если его значение превышает значение наибольшего элемента");
        this.name = name; this.coordinates = coordinates; this.price = price;
        this.discount = discount; this.ticketType = ticketType;
        this.venueName = venueName; this.venueCapacity = venueCapacity; this.venueType = venueType;
    }

    @Override
    public Response execute(CommandContext ctx) {
        try {
            CollectionManager cm = ctx.getCollectionManager();
            String owner = ctx.getCurrentUser();
            Ticket maxTicket = cm.getMaxByPrice();

            if (maxTicket == null || this.price > maxTicket.getPrice()) {
                Venue venue = null;
                if (venueName != null && venueCapacity != null && venueType != null) {
                    venue = new Venue(1L, venueName, venueCapacity, venueType);
                }
                Ticket newTicket = new Ticket(
                        1L, name, coordinates, LocalDateTime.now(),
                        this.price, discount, ticketType, venue
                );

                cm.add(newTicket, owner);
                ctx.getHistoryManager().addCommand(getType().name());
                return Response.success("Билет успешно добавлен.");
            }
            float maxPrice = (maxTicket != null) ? maxTicket.getPrice() : 0;
            return Response.success("Билет не добавлен: " + this.price + " меньше или равно " + maxPrice);
        } catch (Exception e) {
            return Response.error("Ошибка: " + e.getMessage());
        }
    }
}