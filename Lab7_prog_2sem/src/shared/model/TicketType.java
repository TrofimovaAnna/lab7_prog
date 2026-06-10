package shared.model;

public enum TicketType {
    VIP,
    USUAL,
    BUDGETARY,
    CHEAP;

    public static String names() {
        StringBuilder sb = new StringBuilder();
        for (TicketType type : values()) {
            sb.append(type.name()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}