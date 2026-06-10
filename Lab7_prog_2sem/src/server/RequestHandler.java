package server;
import shared.command.*;
import shared.network.Request;
import shared.network.Response;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private final Map<CommandType, Command> commandExecutors = new HashMap<>();
    private final CommandContext context;
    private final DatabaseManager db;

    public RequestHandler(CommandContext ctx, DatabaseManager db) {
        this.context = ctx;
        this.db = db;
        registerCommands();
    }

    private void registerCommands() {
        commandExecutors.put(CommandType.HELP, new HelpCommand());
        commandExecutors.put(CommandType.INFO, new InfoCommand());
        commandExecutors.put(CommandType.SHOW, new ShowCommand());
        commandExecutors.put(CommandType.EXIT, new ExitCommand());
        commandExecutors.put(CommandType.REMOVE_FIRST, new RemoveFirstCommand());
        commandExecutors.put(CommandType.HISTORY, new HistoryCommand());
        commandExecutors.put(CommandType.MIN_BY_VENUE, new MinByVenueCommand());
        commandExecutors.put(CommandType.PRINT_FIELD_DESCENDING_DISCOUNT, new PrintFieldDescendingDiscountCommand());
        commandExecutors.put(CommandType.SERVER_SAVE, new ServerSaveCommand());
        commandExecutors.put(CommandType.ADD, null);
        commandExecutors.put(CommandType.UPDATE, null);
        commandExecutors.put(CommandType.REMOVE_BY_ID, null);
        commandExecutors.put(CommandType.ADD_IF_MAX, null);
        commandExecutors.put(CommandType.FILTER_CONTAINS_NAME, null);
        commandExecutors.put(CommandType.CLEAR, new ClearCommand());
    }

    public Response handle(Request request) {
        Command receivedCommand = request.getCommand();
        if (receivedCommand == null) {
            return Response.error("Ошибка: команда не десериализовалась");
        }

        CommandType type = receivedCommand.getType();

        // обработка регистрации
        if (type == CommandType.REGISTER && receivedCommand instanceof RegisterCommand regCmd) {
            if (db.registerUser(regCmd.getUsername(), regCmd.getPasswordHash())) {
                return Response.success("Пользователь '" + regCmd.getUsername() + "' успешно зарегистрирован. Теперь войдите.");
            }
            return Response.error("Ошибка регистрации (возможно, такой пользователь уже существует).");
        }

        // проверка авторизации
        String user = db.authenticate(request.getUsername(), request.getPasswordHash());
        if (user == null) {
            return Response.error("Неверный логин или пароль");
        }
        context.setCurrentUser(user);

        if (type == CommandType.ADD || type == CommandType.UPDATE ||
                type == CommandType.REMOVE_BY_ID || type == CommandType.ADD_IF_MAX ||
                type == CommandType.FILTER_CONTAINS_NAME) {
            return receivedCommand.execute(context);
        }

        Command executor = commandExecutors.get(type);
        if (executor == null) {
            return Response.error("Неизвестная команда: " + type);
        }
        return executor.execute(context);
    }
}