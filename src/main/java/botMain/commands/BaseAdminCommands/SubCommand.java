package botMain.commands.BaseAdminCommands;

import botMain.commands.CommandContext;

public interface SubCommand {
    String execute(CommandContext ctx, String[] args);
    boolean isMainAdminOnly();
}
