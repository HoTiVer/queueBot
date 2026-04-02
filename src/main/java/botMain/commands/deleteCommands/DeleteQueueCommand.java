package botMain.commands.deleteCommands;

import Service.QueueService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class DeleteQueueCommand implements BotCommand {

    private final QueueService queueService;

    public DeleteQueueCommand(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public String getCommandName() {
        return "delete";
    }

    @Override
    public String execute(CommandContext context) {
        return queueService.deleteQueue(context.chatId(), context.text());
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
