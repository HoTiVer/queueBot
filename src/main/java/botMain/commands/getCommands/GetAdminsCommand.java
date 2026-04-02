package botMain.commands.getCommands;

import Service.AdminService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class GetAdminsCommand implements BotCommand {

    private final AdminService adminService;

    public GetAdminsCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String getCommandName() {
        return "admins";
    }

    @Override
    public String execute(CommandContext context) {
        return adminService.getChatAdmins(context.chatId());
    }
}
