package botMain.commands.deleteCommands;

import Service.QueueService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class RemoveUserCommand implements BotCommand {

    private final QueueService queueService;

    public RemoveUserCommand(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public String getCommandName() {
        return "remove";
    }

    @Override
    public String execute(CommandContext context) {
        return queueService.removeMember(context.chatId(), context.text());
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
