package botMain.commands.updateCommands;

import Service.QueueService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;
import common.MemberStatus;

public class SetProcessStatusCommand implements BotCommand {

    private final QueueService queueService;

    public SetProcessStatusCommand(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public String getCommandName() {
        return "process";
    }

    @Override
    public String execute(CommandContext context) {
        return queueService.updateMemberStatus(context.chatId(), context.text(),
                context.username(), MemberStatus.IN_PROCESS);
    }
}
