package botMain.commands;

import Service.AdminService;
import Service.QueueQueryService;
import Service.QueueService;
import botMain.commands.BaseAdminCommands.AdminBaseCommand;
import botMain.commands.createCommands.CreateQueueCommand;
import botMain.commands.deleteCommands.DeleteQueueCommand;
import botMain.commands.deleteCommands.LeaveQueueCommand;
import botMain.commands.deleteCommands.RemoveUserCommand;
import botMain.commands.getCommands.GetAdminsCommand;
import botMain.commands.getCommands.GetQueueListCommand;
import botMain.commands.getCommands.GetQueueInfoCommand;
import botMain.commands.updateCommands.*;

import java.util.List;

public class CommandInitializer {

    public static List<BotCommand> initCommands(QueueService queueService,
                                                QueueQueryService queueQueryService,
                                                AdminService adminService) {
        return List.of(
                new CreateQueueCommand(queueService),
                new GetQueueListCommand(queueQueryService),
                new GetQueueInfoCommand(queueQueryService),
                new JoinQueueCommand(queueService),
                new LeaveQueueCommand(queueService),
                new DeleteQueueCommand(queueService),
                new InsertUserInQueueCommand(queueService),
                new SwapUsersCommand(queueService),
                new RebuildQueueCommand(queueService),
                new UpdateQueueTimeCommand(queueService),
                new SetCompleteStatusCommand(queueService),
                new SetRetakeStatusCommand(queueService),
                new SetProcessStatusCommand(queueService),
                new RemoveUserCommand(queueService),
                new GetAdminsCommand(adminService),
                new AdminBaseCommand(adminService)
        );
    }
}
