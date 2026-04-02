import Entity.Queue;
import Repository.AdminDao;
import Repository.MemberDao;
import Repository.QueueDao;
import Service.AdminService;
import Service.QueueNotificationService;
import Service.QueueQueryService;
import Service.QueueService;
import botMain.*;
import botMain.commands.CommandDispatcher;
import botMain.commands.CommandInitializer;
import common.BotUtils;
import common.QueueUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.flywaydb.core.Flyway;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        String url = getEnvValue(dotenv, "DB_URL");
        String user = getEnvValue(dotenv, "DB_USER");
        String pass = getEnvValue(dotenv, "DB_PASS");
        String botToken = getEnvValue(dotenv, "BOT_TOKEN");

        System.setProperty("hibernate.connection.url", url);
        System.setProperty("hibernate.connection.username", user);
        System.setProperty("hibernate.connection.password", pass);

        Flyway flyway = Flyway.configure()
                .dataSource(url, user, pass)
                .load();

        flyway.migrate();

        try (TelegramBotsLongPollingApplication botsApplication =
                     new TelegramBotsLongPollingApplication()) {

            BotUtils botUtils = new BotUtils();
            QueueUtils queueUtils = new QueueUtils();

            AdminDao adminDao = new AdminDao();
            QueueDao queueDao = new QueueDao();
            MemberDao memberDao = new MemberDao();

            AdminService adminService = new AdminService(adminDao);
            QueueService queueService = new QueueService(queueDao, memberDao, queueUtils);
            QueueQueryService queueQueryService = new QueueQueryService(queueDao);

            CommandDispatcher commandDispatcher = new CommandDispatcher(
                    adminService,
                    CommandInitializer.initCommands(queueService, queueQueryService, adminService)
            );

            Bot bot = new Bot(botToken, queueService, botUtils, commandDispatcher);
            botsApplication.registerBot(botToken, bot);

            QueueNotificationService.getInstance().setBot(bot);

            List<Queue> upcomingQueues = queueDao.getAllQueuesWithFutureStart();
            for (Queue queue : upcomingQueues) {
                QueueNotificationService.getInstance().scheduleQueueStartNotification(queue);
            }

            System.out.println("Bot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String getEnvValue(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        return value != null ? value : System.getenv(key);
    }
}
