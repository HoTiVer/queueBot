package botMain.commands.getCommands;


import Service.QueueQueryService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class GetQueueListCommand implements BotCommand {

    private final QueueQueryService queueQueryService;

    public GetQueueListCommand(QueueQueryService queueQueryService) {
        this.queueQueryService = queueQueryService;
    }

    @Override
    public String getCommandName() {
        return "list";
    }

    @Override
    public String execute(CommandContext context) {
        return queueQueryService.getChatQueuesNamesAndMiniInfo(context.chatId());
    }
}
