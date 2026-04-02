package botMain.commands.BaseAdminCommands;

import Service.AdminService;
import botMain.commands.CommandContext;

public class RegisterAdminSub implements SubCommand {

    private final AdminService adminService;

    public RegisterAdminSub(AdminService adminService) { this.adminService = adminService; }

    @Override
    public String execute(CommandContext ctx, String[] args) {
        if (args.length < 3) return "Write @username";
        return adminService.registerAdmin(ctx.chatId(), ctx.username(), args[2]);
    }

    @Override
    public boolean isMainAdminOnly() {
        return true;
    }
}
