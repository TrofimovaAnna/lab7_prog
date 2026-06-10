package client;

import shared.command.*;
import shared.model.*;
import java.util.Scanner;
import shared.utility.PasswordUtil;

public class CommandReader {
    private final Scanner scanner;
    private final InputValidator validator;

    public CommandReader(Scanner scanner) {
        this.scanner = scanner;
        this.validator = new InputValidator(scanner);
    }

    public Command parseCommand(String input) {
        String[] parts = input.trim().split("\\s+", 2); // 1 - команда, 2 - аргументы
        String cmd = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1].trim() : null;

        return switch (cmd) {
            case "help" -> new HelpCommand();
            case "info" -> new InfoCommand();
            case "show" -> new ShowCommand();
            case "clear" -> new ClearCommand();
            case "exit" -> new ExitCommand();
            case "remove_first" -> new RemoveFirstCommand();
            case "history" -> new HistoryCommand();
            case "min_by_venue" -> new MinByVenueCommand();
            case "print_field_descending_discount" -> new PrintFieldDescendingDiscountCommand();
            case "add" -> parseAdd(args);
            case "update" -> parseUpdate(args);
            case "remove_by_id" -> parseRemoveById(args);
            case "add_if_max" -> parseAddIfMax(args);
            case "register" -> parseRegister(args);
            case "filter_contains_name" -> parseFilter(args);
            default -> {
                System.out.println("Неизвестная команда. Введите 'help' для справки команд");
                yield null; // command = null
            }
        };
    }

    private boolean readYesNo(String yesNoString) {
        while (true) {
            System.out.print(yesNoString);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes")) {
                return true;
            } else if (input.equals("no")) {
                return false;
            } else {
                System.out.println("Введите 'yes' или 'no'");
            }
        }
    }

    private Command parseAdd(String args) {
        if (args != null && !args.trim().isEmpty()) {
            try {
                String[] p = args.trim().split("\\s+", 7);
                if (p.length < 6) return null;
                String name = p[0].replace("\"", "");
                Double x = p[1].equalsIgnoreCase("null") ? null : Double.parseDouble(p[1]);
                int y = Integer.parseInt(p[2]);
                Coordinates coords = new Coordinates(x, y);
                float price = Float.parseFloat(p[3]);
                long discount = Long.parseLong(p[4]);
                TicketType type = TicketType.valueOf(p[5].toUpperCase());
                return new AddCommand(name, coords, price, discount, type, null, null, null);
            } catch (Exception e) { return null; }
        }
        System.out.println("Ввод билета");
        String name = validator.readString("Name", false, true);
        Double x = validator.readDouble("X", true);
        int y = validator.readInt("Y");
        Coordinates coords = new Coordinates(x, y);
        float price = validator.readFloat("Price", true, 0);
        long discount = validator.readLongRange("Discount", 1, 100);
        TicketType type = validator.readEnum("Type", TicketType.class);
        boolean hasVenue = readYesNo("Добавить venue? (yes/no): ");
        String venueName = null; Long venueCapacity = null; VenueType venueType = null;
        if (hasVenue) {
            venueName = validator.readString("Venue name", false, true);
            venueCapacity = validator.readLongRange("Capacity", 1, Long.MAX_VALUE);
            venueType = validator.readEnum("Venue type", VenueType.class);
        }
        return new AddCommand(name, coords, price, discount, type, venueName, venueCapacity, venueType);
    }
    private Command parseAddIfMax(String args) {
        if (args != null && !args.trim().isEmpty()) {
            try {
                String[] p = args.trim().split("\\s+", 7);
                if (p.length < 6) return null;
                String name = p[0].replace("\"", "");
                Double x = p[1].equalsIgnoreCase("null") ? null : Double.parseDouble(p[1]);
                int y = Integer.parseInt(p[2]);
                Coordinates coords = new Coordinates(x, y);
                float price = Float.parseFloat(p[3]);
                long discount = Long.parseLong(p[4]);
                TicketType type = TicketType.valueOf(p[5].toUpperCase());
                return new AddIfMaxCommand(name, coords, price, discount, type, null, null, null);
            } catch (Exception e) { return null; }
        }
        System.out.println("Ввод билета add_if_max");
        String name = validator.readString("Name", false, true);
        Double x = validator.readDouble("X", true);
        int y = validator.readInt("Y");
        Coordinates coords = new Coordinates(x, y);
        float price = validator.readFloat("Price", true, 0);
        long discount = validator.readLongRange("Discount", 1, 100);
        TicketType type = validator.readEnum("Type", TicketType.class);
        boolean hasVenue = readYesNo("Хотите добавить venue? (yes/no): ");
        String venueName = null; Long venueCapacity = null; VenueType venueType = null;
        if (hasVenue) {
            venueName = validator.readString("Venue name", false, true);
            venueCapacity = validator.readLongRange("Capacity", 1, Long.MAX_VALUE);
            venueType = validator.readEnum("Venue type", VenueType.class);
        }
        return new AddIfMaxCommand(name, coords, price, discount, type, venueName, venueCapacity, venueType);
    }
    private Command parseUpdate(String args) {
        if (args != null && !args.trim().isEmpty()) {
            try {
                String[] p = args.trim().split("\\s+", 8);
                if (p.length < 7) return null;
                long id = Long.parseLong(p[0]);
                if (id <= 0) return null;
                String name = p[1].replace("\"", "");
                Double x = p[2].equalsIgnoreCase("null") ? null : Double.parseDouble(p[2]);
                int y = Integer.parseInt(p[3]);
                Coordinates coords = new Coordinates(x, y);
                float price = Float.parseFloat(p[4]);
                long discount = Long.parseLong(p[5]);
                TicketType type = TicketType.valueOf(p[6].toUpperCase());
                return new UpdateCommand(id, name, coords, price, discount, type, null, null, null);
            } catch (Exception e) { return null; }
        }
        System.out.print("Введите ID для обновления: ");
        Long id = parseIdArg(null);
        if (id == null) return null;
        System.out.println("Ввод новых данных для билета " + id);
        String name = validator.readString("Name", false, true);
        Double x = validator.readDouble("X", true);
        int y = validator.readInt("Y");
        Coordinates coords = new Coordinates(x, y);
        float price = validator.readFloat("Price", true, 0);
        long discount = validator.readLongRange("Discount", 1, 100);
        TicketType type = validator.readEnum("Type", TicketType.class);
        boolean hasVenue = readYesNo("Хотите добавить venue? (yes/no): ");
        String venueName = null; Long venueCapacity = null; VenueType venueType = null;
        if (hasVenue) {
            venueName = validator.readString("Venue name", false, true);
            venueCapacity = validator.readLongRange("Capacity", 1, Long.MAX_VALUE);
            venueType = validator.readEnum("Venue type", VenueType.class);
        }
        return new UpdateCommand(id, name, coords, price, discount, type, venueName, venueCapacity, venueType);
    }

    private Command parseRemoveById(String args) {
        Long id = parseIdArg(args);
        return id != null ? new RemoveByIdCommand(id) : null;
    }

    private Command parseFilter(String args) {
        String substring = (args != null && !args.isEmpty()) ? args :
                validator.readString("подстроку", false, true);
        return new FilterContainsNameCommand(substring);
    }

    private Long parseIdArg(String args) {
        if (args == null || args.isEmpty()) {
            System.out.print("Введите ID: ");
            args = scanner.nextLine().trim();
        }
        try {
            long id = Long.parseLong(args);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            System.out.println("Некорректный ID (id > 0)");
            return null;
        }
    }

    private Command parseRegister(String args) {
        System.out.print("Введите логин для регистрации: ");
        String login = scanner.nextLine().trim();
        System.out.print("Введите пароль: ");
        String pass = scanner.nextLine().trim();

        if (login.isEmpty() || pass.isEmpty()) {
            System.out.println("Логин и пароль не могут быть пустыми");
            return null;
        }
        // Хэшируем на клиенте перед отправкой
        return new RegisterCommand(login, PasswordUtil.hash(pass));
    }
}