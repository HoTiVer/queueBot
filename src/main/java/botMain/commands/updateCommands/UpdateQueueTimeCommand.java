package botMain.commands.updateCommands;

import Service.QueueService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class UpdateQueueTimeCommand implements BotCommand {

    private final QueueService queueService;

    public UpdateQueueTimeCommand(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public String getCommandName() {
        return "set-time";
    }

    @Override
    public String execute(CommandContext context) {
        return queueService.setNewQueueFastTime(context.chatId(), context.text());
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
