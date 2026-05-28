package botMain.commands.updateCommands;

import Service.QueueService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class InsertUserInQueueCommand implements BotCommand {

    private final QueueService queueService;

    public InsertUserInQueueCommand(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public String getCommandName() {
        return "insert";
    }

    @Override
    public String execute(CommandContext context) {
        return queueService.insertMember(context.chatId(), context.text());
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
