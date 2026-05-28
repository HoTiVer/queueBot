package botMain.commands;

public interface BotCommand {

    String getCommandName();
    String execute(CommandContext context);

    default boolean isAdminOnly() { return false; }
    default boolean isMainAdminOnly() { return false; }
}
