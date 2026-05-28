package botMain.commands.createCommands;

import Service.AdminService;
import botMain.commands.BaseAdminCommands.AdminBaseCommand;
import botMain.commands.BaseAdminCommands.SubCommand;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

public class RegisterFirstAdminCommand implements SubCommand {

    private final AdminService adminService;

    public RegisterFirstAdminCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String execute(CommandContext ctx, String[] args) {
        return adminService.registerAdmin(ctx.chatId(), ctx.username(), args[2]);
    }

    @Override
    public boolean isMainAdminOnly() {
        return false;
    }
}
