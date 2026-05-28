package botMain.commands;

import Service.AdminService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDispatcher {

    private final Map<String, BotCommand> commands = new HashMap<>();
    private final AdminService adminService;

    public CommandDispatcher(AdminService adminService, List<BotCommand> commandList) {
        this.adminService = adminService;
        commandList.forEach(cmd -> commands.put(cmd.getCommandName(), cmd));
    }

    public String dispatch(String command, CommandContext context) {
        BotCommand botCommand = commands.get(command);

        if (botCommand == null) return null;

        if (botCommand.isMainAdminOnly()) {
            if (!adminService.validateMainQueueAdmin(context.chatId(), context.username())) {
                return "This operation is available only to the CHIEF administrator.";
            }
        }
        else if (botCommand.isAdminOnly() &&
                !adminService.validateQueueAdmin(context.chatId(),  context.username())) {
            return "Dont have permission to use this command!";
        }

        return botCommand.execute(context);
    }

}
