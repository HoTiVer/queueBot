package botMain.commands.deleteCommands;

import Service.QueueService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class LeaveQueueCommand implements BotCommand {

    private final QueueService queueService;

    public LeaveQueueCommand(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public String getCommandName() {
        return "leave";
    }

    @Override
    public String execute(CommandContext context) {
        return queueService.leaveQueue(context.chatId(), context.username(), context.text());
    }
}
