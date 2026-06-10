package shared.model;

import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Comparable<Coordinates>, Serializable {

    private final Double x;
    private final int y;

    public Coordinates(Double x, int y) {
        if (x == null) throw new IllegalArgumentException("X не может быть null");
        this.x = x;
        this.y = y;
    }

    public Double getX() { return x; }
    public int getY() { return y; }

    @Override
    public int compareTo(Coordinates other) {
        if (other == null) return 1;
        int cmp = Double.compare(this.x, other.x);
        return (cmp != 0) ? cmp : Integer.compare(this.y, other.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates that)) return false;
        return y == that.y && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + "}";
    }
}