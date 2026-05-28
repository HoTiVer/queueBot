package botMain.commands.BaseAdminCommands;

import Service.AdminService;
import botMain.commands.CommandContext;

public class RaiseAdminSub implements SubCommand{

    private final AdminService adminService;

    public RaiseAdminSub(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String execute(CommandContext ctx, String[] args) {
        return adminService.raiseMember(ctx.chatId(), ctx.username(), args[2]);
    }

    @Override
    public boolean isMainAdminOnly() {
        return true;
    }
}
