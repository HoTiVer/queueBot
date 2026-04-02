package botMain.commands.updateCommands;

import Service.QueueService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class SwapUsersCommand implements BotCommand {

    private final QueueService queueService;

    public SwapUsersCommand(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public String getCommandName() {
        return "swap";
    }

    @Override
    public String execute(CommandContext context) {
        return queueService.swapMembers(context.chatId(), context.text(), context.username());
    }
}
