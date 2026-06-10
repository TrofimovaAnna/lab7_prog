package shared.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Ticket implements Comparable<Ticket>, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private final String name;
    private final Coordinates coordinates;
    private final LocalDateTime creationDate;
    private final float price;
    private final long discount;
    private final TicketType type;
    private final Venue venue;

    private String ownerUsername;

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Ticket(long id, String name, Coordinates coordinates,
                  LocalDateTime creationDate, float price, long discount,
                  TicketType type, Venue venue) {
        if (id <= 0) throw new IllegalArgumentException("ID > 0");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name не пустой");
        if (coordinates == null) throw new IllegalArgumentException("Coordinates не null");
        if (creationDate == null) throw new IllegalArgumentException("CreationDate не null");
        if (price <= 0) throw new IllegalArgumentException("Price > 0");
        if (discount <= 0 || discount > 100) throw new IllegalArgumentException("Discount: 1-100");
        if (type == null) throw new IllegalArgumentException("Type не null");

        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.discount = discount;
        this.type = type;
        this.venue = venue;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public float getPrice() { return price; }
    public long getDiscount() { return discount; }
    public TicketType getType() { return type; }
    public Venue getVenue() { return venue; }

    // сначала сравнивается цена, потом id
    @Override
    public int compareTo(Ticket other) {
        if (other == null) return 1;
        int cmp = Float.compare(this.price, other.price);
        return (cmp != 0) ? cmp : Long.compare(this.id, other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket ticket)) return false;
        return id == ticket.id && Float.compare(ticket.price, price) == 0
                && discount == ticket.discount && Objects.equals(name, ticket.name)
                && Objects.equals(coordinates, ticket.coordinates)
                && Objects.equals(creationDate, ticket.creationDate)
                && type == ticket.type && Objects.equals(venue, ticket.venue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, price, discount, type, venue);
    }

    @Override
    public String toString() {
        return "Ticket{id=" + id + ", name='" + name + "', price=" + price
                + ", discount=" + discount + ", type=" + type
                + ", venue=" + (venue != null ? venue : "null") + "}";
    }
}