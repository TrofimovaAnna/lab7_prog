package server;

import shared.config.LabConfig;
import shared.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseManager {
    private final Connection conn;

    public DatabaseManager(String password) throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", LabConfig.DB_USER);
        props.setProperty("password", password);
        props.setProperty("currentSchema", LabConfig.SCHEMA);
        conn = DriverManager.getConnection(LabConfig.DB_URL, props);
        conn.setAutoCommit(true);
        initSchema();
    }

    private void initSchema() throws SQLException {
        String sql = """
            CREATE SEQUENCE IF NOT EXISTS ticket_id_seq START 1;
            CREATE SEQUENCE IF NOT EXISTS venue_id_seq START 1;
            CREATE TABLE IF NOT EXISTS users (username VARCHAR(50) PRIMARY KEY, password_hash VARCHAR(128) NOT NULL);
            CREATE TABLE IF NOT EXISTS venues (id BIGINT PRIMARY KEY DEFAULT nextval('venue_id_seq'), name VARCHAR(255) NOT NULL CHECK (name <> ''), capacity BIGINT NOT NULL CHECK (capacity > 0), type VARCHAR(50) NOT NULL);
            CREATE TABLE IF NOT EXISTS tickets (id BIGINT PRIMARY KEY DEFAULT nextval('ticket_id_seq'), name VARCHAR(255) NOT NULL CHECK (name <> ''), x DOUBLE PRECISION NOT NULL, y INTEGER NOT NULL, creation_date TIMESTAMP NOT NULL, price FLOAT NOT NULL CHECK (price > 0), discount BIGINT NOT NULL CHECK (discount BETWEEN 1 AND 100), type VARCHAR(50) NOT NULL, venue_id BIGINT REFERENCES venues(id) ON DELETE SET NULL, owner_username VARCHAR(50) REFERENCES users(username) ON DELETE CASCADE);
            """;
        try (Statement stmt = conn.createStatement()) { stmt.execute(sql); }
    }

    public long saveTicket(Ticket t, String owner) throws SQLException {
        Long venueId = null;
        if (t.getVenue() != null) {
            venueId = saveVenueToDb(t.getVenue());
        }

        String sql = "INSERT INTO tickets (name, x, y, creation_date, price, discount, type, venue_id, owner_username) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getName());
            ps.setDouble(2, t.getCoordinates().getX());
            ps.setInt(3, t.getCoordinates().getY());
            ps.setTimestamp(4, Timestamp.valueOf(t.getCreationDate()));
            ps.setFloat(5, t.getPrice());
            ps.setLong(6, t.getDiscount());
            ps.setString(7, t.getType().name());
            if (venueId != null) ps.setLong(8, venueId); else ps.setNull(8, Types.BIGINT);
            ps.setString(9, owner);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { return rs.next() ? rs.getLong(1) : -1; }
        }
    }

    private Long saveVenueToDb(Venue v) throws SQLException {
        String sql = "INSERT INTO venues (id, name, capacity, type) VALUES (nextval('venue_id_seq'), ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getName());
            ps.setLong(2, v.getCapacity());
            ps.setString(3, v.getType().name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : null;
            }
        }
    }

    public List<Ticket> loadAllTickets() {
        List<Ticket> list = new ArrayList<>();
        String sql = """
        SELECT t.id, t.name, t.x, t.y, t.creation_date, t.price, t.discount, t.type, 
               t.owner_username,
               v.id as v_id, v.name as v_name, v.capacity, v.type as v_type 
        FROM tickets t LEFT JOIN venues v ON t.venue_id = v.id
        """;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Coordinates coords = new Coordinates(rs.getDouble("x"), rs.getInt("y"));
                Venue venue = null;
                if (rs.getObject("v_id") != null) {
                    venue = new Venue(rs.getLong("v_id"), rs.getString("v_name"),
                            rs.getLong("capacity"), VenueType.valueOf(rs.getString("v_type")));
                }
                Ticket t = new Ticket(rs.getLong("id"), rs.getString("name"), coords,
                        rs.getTimestamp("creation_date").toLocalDateTime(),
                        rs.getFloat("price"), rs.getLong("discount"),
                        TicketType.valueOf(rs.getString("type")), venue);
                t.setOwnerUsername(rs.getString("owner_username"));
                list.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public String authenticate(String username, String passwordHash) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT password_hash FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString("password_hash").equals(passwordHash)) {
                    return username;
                }
            }
        } catch (SQLException ignored) {}
        return null;
    }

    public boolean isOwner(long ticketId, String username) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT owner_username FROM tickets WHERE id = ?")) {
            ps.setLong(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getString("owner_username").equals(username); }
        } catch (SQLException e) { return false; }
    }

    public boolean registerUser(String username, String passwordHash) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public void clearByOwner(String owner) {
        String sql = "DELETE FROM tickets WHERE owner_username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, owner);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateTicket(long id, Ticket t) throws SQLException {
        Long venueId = null;
        if (t.getVenue() != null) {
            venueId = saveVenueToDb(t.getVenue());
        }

        String sql = "UPDATE tickets SET name=?, x=?, y=?, creation_date=?, price=?, discount=?, type=?, venue_id=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setDouble(2, t.getCoordinates().getX());
            ps.setInt(3, t.getCoordinates().getY());
            ps.setTimestamp(4, Timestamp.valueOf(t.getCreationDate()));
            ps.setFloat(5, t.getPrice());
            ps.setLong(6, t.getDiscount());
            ps.setString(7, t.getType().name());
            if (venueId != null) ps.setLong(8, venueId);
            else ps.setNull(8, Types.BIGINT);
            ps.setLong(9, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean removeTicket(long id) throws SQLException {
        String sql = "DELETE FROM tickets WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}