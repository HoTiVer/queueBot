package botMain.commands.getCommands;

import Service.QueueQueryService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class GetQueueInfoCommand implements BotCommand {

    private final QueueQueryService queueQueryService;

    public GetQueueInfoCommand(QueueQueryService queueQueryService) {
        this.queueQueryService = queueQueryService;
    }

    @Override
    public String getCommandName() {
        return "info";
    }

    @Override
    public String execute(CommandContext context) {
        return queueQueryService.getQueueInfo(context.chatId(), context.text());
    }
}
