package botMain.commands.updateCommands;

import Service.QueueService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class RebuildQueueCommand implements BotCommand {

    public QueueService queueService;

    public RebuildQueueCommand(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public String getCommandName() {
        return "rebuild";
    }

    @Override
    public String execute(CommandContext context) {
        return queueService.rebuildQueue(context.chatId(), context.text());
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
