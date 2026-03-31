package botMain;

import Service.AdminService;
import Service.QueueService;
import common.BotUtils;
import common.MemberStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


public class Bot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final AdminService adminService;
    private final QueueService queueService;
    private final BotUtils botUtils;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Bot(String botToken, AdminService adminService,
               QueueService queueService, BotUtils botUtils) {
        telegramClient = new OkHttpTelegramClient(botToken);
        this.adminService = adminService;
        this.queueService = queueService;
        this.botUtils = botUtils;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText().trim();
            Long chatId = update.getMessage().getChatId();

            if (chatId > 0) {
                return;
            }

            var from = update.getMessage().getFrom();
            String userName = botUtils.getUserDisplayName(from);


            if (botUtils.isNumber(text)){
                String response = queueService.fastQueueJoin(chatId, text, userName);
                System.out.println(response);
                sendMessage(chatId, response);
            }

            String firstWord = botUtils.getFirstWord(text);

            String response;
            switch (firstWord) {
                case "join":
                    response = queueService.joinQueue(chatId, userName, text);
                    sendMessage(chatId, response);
                    break;
                case "leave":
                    response = queueService.leaveQueue(chatId, userName, text);
                    sendMessage(chatId, response);
                    break;
                case "create":
                    if (adminService.validateQueueAdmin(chatId, userName)){
                        response = queueService.createQueue(chatId, text);
                        sendMessage(chatId, response);
                    }
                    else {
                        sendMessage(chatId, "not admin");
                    }
                    break;
                case  "delete":
                    if (adminService.validateQueueAdmin(chatId, userName)){
                        queueService.deleteQueue(chatId, text);
                    }
                    else {
                        sendMessage(chatId, "not admin");
                    }
                    break;
                case "list":
                    response = queueService.getChatQueuesNamesAndMiniInfo(chatId);
                    sendMessage(chatId, response);
                    break;
                case "info":
                    response = queueService.getQueueInfo(chatId, text);
                    sendMessage(chatId, response);
                    break;
                case "insert":
                    if (adminService.validateQueueAdmin(chatId, userName)) {
                        response = queueService.insertMember(chatId, text);
                        sendMessage(chatId, response);
                    }
                    else {
                        sendMessage(chatId, "not admin");
                    }
                    break;
                case "swap":
                    response = queueService.swapMembers(chatId, text, userName);
                    sendMessage(chatId, response);
                    break;
                case "rebuild":
                    if (adminService.validateQueueAdmin(chatId, userName)) {
                        response = queueService.rebuildQueue(chatId, text);
                        sendMessage(chatId, response);
                    }
                    else {
                        sendMessage(chatId, "not admin");
                    }
                    break;
                case "settime":
                    if (adminService.validateQueueAdmin(chatId, userName)) {
                        response = queueService.setNewQueueFastTime(chatId, text);
                        sendMessage(chatId, response);
                    }
                    else {
                        sendMessage(chatId, "not admin");
                    }
                    break;
                case "complete":
                    response = queueService.updateMemberStatus(chatId, text,
                            userName, MemberStatus.COMPLETE);
                    sendMessage(chatId, response);
                    break;
                case "retake":
                    response = queueService.updateMemberStatus(chatId, text,
                            userName, MemberStatus.RETAKE);
                    sendMessage(chatId, response);
                    break;
                case "process":
                    response = queueService.updateMemberStatus(chatId, text,
                            userName, MemberStatus.IN_PROCESS);
                    sendMessage(chatId, response);
                    break;
                case "remove":
                    if (adminService.validateQueueAdmin(chatId, userName)){
                        response = queueService.removeMember(chatId, text);
                        sendMessage(chatId, response);
                    }
                    else {
                        sendMessage(chatId, "not admin");
                    }
                    break;
                case "admins":
                    response = adminService.getChatAdmins(chatId);
                    sendMessage(chatId, response);
                    break;
                case "admin":
                    response = "Unknown admin command. Use 'reg', 'raise' or 'del'.";
                    String[] parts = text.trim().split("\\s+");
                    if (parts.length < 2) {
                        sendMessage(chatId, "Usage: admin reg <args> or admin del <args>");
                        break;
                    }
                    String subCommand = parts[1].toLowerCase();
                    String userToEdit = null;
                    try {
                        userToEdit = parts[2];
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    if (subCommand.equals("reg")){
                        response = adminService.registerAdmin(chatId, userName, userToEdit);
                    }
                    if (subCommand.equals("del") && adminService
                            .validateMainQueueAdmin(chatId, userName)){
                        response = adminService.deleteAdmin(chatId, userName, userToEdit);
                    }
                    if (subCommand.equals("raise") && adminService
                            .validateMainQueueAdmin(chatId, userName)){
                        response = adminService.raiseMember(chatId, userName, userToEdit);
                    }
                    sendMessage(chatId, response);
                    break;
            }
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        msg.setChatId(chatId);
        msg.setText(text);
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.toString());
        }
    }
}
