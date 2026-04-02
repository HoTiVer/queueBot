package botMain.commands.updateCommands;

import Service.QueueService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class JoinQueueCommand implements BotCommand {

    private final QueueService queueService;

    public JoinQueueCommand(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public String getCommandName() {
        return "join";
    }

    @Override
    public String execute(CommandContext context) {
        return queueService.joinQueue(context.chatId(), context.username(), context.text());
    }
}
