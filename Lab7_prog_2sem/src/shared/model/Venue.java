package shared.model;

import java.io.Serializable;
import java.util.Objects;

public class Venue implements Comparable<Venue>, Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private final String name;
    private final long capacity;
    private final VenueType type;

    public Venue(Long id, String name, long capacity, VenueType type) {
        if (id == null || id <= 0) throw new IllegalArgumentException("ID > 0");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name не пустой");
        if (capacity <= 0) throw new IllegalArgumentException("Capacity > 0");
        if (type == null) throw new IllegalArgumentException("Type не null");
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.type = type;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public long getCapacity() { return capacity; }
    public VenueType getType() { return type; }

    @Override
    public int compareTo(Venue other) {
        if (other == null) return 1;
        return Long.compare(this.capacity, other.capacity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venue venue)) return false;
        return capacity == venue.capacity && Objects.equals(id, venue.id)
                && Objects.equals(name, venue.name) && type == venue.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, capacity, type);
    }

    @Override
    public String toString() {
        return "Venue{id=" + id + ", name='" + name + "', capacity=" + capacity + ", type=" + type + "}";
    }
}