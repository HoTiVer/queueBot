package botMain.commands.BaseAdminCommands;

import Service.AdminService;
import botMain.commands.CommandContext;

public class DeleteAdminSub implements SubCommand {
    private final AdminService adminService;
    public DeleteAdminSub(AdminService adminService) { this.adminService = adminService; }

    @Override
    public boolean isMainAdminOnly() { return true; }

    @Override
    public String execute(CommandContext ctx, String[] args) {
        if (args.length < 3) return "Write @username";
        return adminService.deleteAdmin(ctx.chatId(), ctx.username(), args[2]);
    }
}