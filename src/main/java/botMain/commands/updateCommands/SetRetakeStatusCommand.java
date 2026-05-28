package botMain.commands.updateCommands;

import Service.QueueService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;
import common.MemberStatus;

public class SetRetakeStatusCommand implements BotCommand {

    private final QueueService queueService;

    public SetRetakeStatusCommand(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public String getCommandName() {
        return "retake";
    }

    @Override
    public String execute(CommandContext context) {
        return queueService.updateMemberStatus(context.chatId(), context.text(),
                context.username(), MemberStatus.RETAKE);
    }
}
