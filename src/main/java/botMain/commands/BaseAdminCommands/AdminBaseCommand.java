package botMain.commands.BaseAdminCommands;

import Service.AdminService;
import botMain.commands.BotCommand;
import botMain.commands.CommandContext;

import java.util.HashMap;
import java.util.Map;

public class AdminBaseCommand implements BotCommand {
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final AdminService adminService;

    public AdminBaseCommand(AdminService adminService) {
        this.adminService = adminService;
        subCommands.put("reg", new RegisterAdminSub(adminService));
        subCommands.put("del", new DeleteAdminSub(adminService));
        subCommands.put("raise", new RaiseAdminSub(adminService));
    }

    @Override
    public String getCommandName() { return "admin"; }

    @Override
    public String execute(CommandContext ctx) {
        String[] parts = ctx.text().split("\\s+");
        if (parts.length < 2) return "Using: admin [reg|del|raise] @username";

        String subName = parts[1].toLowerCase();
        SubCommand sub = subCommands.get(subName);

        if (sub == null) return "Unknown sub-command.";

        if (sub.isMainAdminOnly() && !adminService.validateMainQueueAdmin(ctx.chatId(), ctx.username())) {
            return "Only the main admin can do this.";
        }

        return sub.execute(ctx, parts);
    }
}