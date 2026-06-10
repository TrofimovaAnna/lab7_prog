package shared.command;

import shared.network.Response;

public class HelpCommand extends Command {
    public HelpCommand() {
        super(CommandType.HELP, "вывести справку по доступным командам");
    }

    @Override
    public Response execute(CommandContext ctx) {
        String help = "Доступные команды:\n" + "help - справка по командам\n" +
                "info - информация о коллекции\n" +
                "show - показать все элементы\n" +
                "add - добавить элемент\n" +
                "update id - обновить элемент по ID\n" +
                "remove_by_id id - удалить по ID\n" +
                "clear - очистить коллекцию\n" +
                "remove_first - удалить первый элемент\n" +
                "add_if_max - добавить, если больше максимального\n" +
                "history - последние 13 команд\n" +
                "min_by_venue - элемент с минимальным venue\n" +
                "filter_contains_name name - фильтр по имени\n" +
                "print_field_descending_discount - discount по убыванию\n" +
                "exit - завершить клиент";
        return Response.success(help);
    }
}