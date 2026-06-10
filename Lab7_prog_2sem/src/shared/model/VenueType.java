package shared.model;

public enum VenueType {
    OPEN_AREA,
    CINEMA,
    STADIUM;

    public static String names() {
        StringBuilder sb = new StringBuilder();
        for (VenueType type : values()) {
            sb.append(type.name()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}