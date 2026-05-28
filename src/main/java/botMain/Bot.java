package botMain;

import Service.QueueService;
import botMain.commands.CommandContext;
import botMain.commands.CommandDispatcher;
import common.BotUtils;
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
    private final CommandDispatcher commandDispatcher;
    private final QueueService queueService;
    private final BotUtils botUtils;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Bot(String botToken, QueueService queueService,
               BotUtils botUtils, CommandDispatcher commandDispatcher) {
        telegramClient = new OkHttpTelegramClient(botToken);
        this.queueService = queueService;
        this.botUtils = botUtils;
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String text = update.getMessage().getText().trim();
        Long chatId = update.getMessage().getChatId();

        if (chatId > 0) return;

        String userName = botUtils.getUserDisplayName(update.getMessage().getFrom());
        String firstWord = botUtils.getFirstWord(text);

        if (botUtils.isNumber(text)) {
            sendMessage(chatId, queueService.fastQueueJoin(chatId, text, userName));
            return;
        }

        CommandContext ctx = new CommandContext(chatId, text, userName);
        String response = commandDispatcher.dispatch(firstWord, ctx);

        if (response != null) {
            sendMessage(chatId, response);
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
