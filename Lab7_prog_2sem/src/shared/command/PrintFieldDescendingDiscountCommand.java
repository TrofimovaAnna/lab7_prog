package shared.command;

import shared.network.Response;

public class PrintFieldDescendingDiscountCommand extends Command {
    public PrintFieldDescendingDiscountCommand() {
        super(CommandType.PRINT_FIELD_DESCENDING_DISCOUNT, "discount по убыванию");
    }

    @Override
    public Response execute(CommandContext ctx) {
        var discounts = ctx.getCollectionManager().getCollection().stream()
                .map(t -> t.getDiscount())
                .sorted(java.util.Comparator.reverseOrder())
                .toList();

        if (discounts.isEmpty()) return Response.error("Коллекция пустая");
        return Response.success("Discount по убыванию:\n" +
                discounts.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining("\n")));
    }
}