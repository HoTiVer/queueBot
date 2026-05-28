package botMain.commands.createCommands;

import Service.QueueService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class CreateQueueCommand implements BotCommand {

    private final QueueService queueService;

    public CreateQueueCommand(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public String getCommandName() {
        return "create";
    }

    @Override
    public String execute(CommandContext context) {
        return queueService.createQueue(context.chatId(), context.text());
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
