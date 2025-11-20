import Entity.Queue;
import Repository.QueueDao;
import Service.QueueNotificationService;
import botMain.Bot;
import io.github.cdimascio.dotenv.Dotenv;
import org.flywaydb.core.Flyway;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import java.util.List;


public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = null;
        String environment = null;
        try {
            dotenv = Dotenv.load();
            environment = dotenv.get("ENVIRONMENT");
        } catch (Exception e){
            environment = System.getenv("ENVIRONMENT");
        }
        if (environment == null){
            System.out.println("Enter value to ENVIRONMENT in env file.");
            return;
        }

        String url;
        String user;
        String pass;
        String botToken = null;

        if (environment.equals("DEBUG")){
            url = dotenv.get("DB_URL");
            user = dotenv.get("DB_USER");
            pass = dotenv.get("DB_PASS");

            System.setProperty("hibernate.connection.url", url);
            System.setProperty("hibernate.connection.username", user);
            System.setProperty("hibernate.connection.password", pass);
            Flyway flyway = Flyway.configure()
                    .dataSource(url, user, pass)
                    .load();
            flyway.migrate();
            botToken = dotenv.get("BOT_TOKEN");
        } else if (environment.equals("PROD")) {
            url = System.getenv("DB_URL");
            user = System.getenv("DB_USER");
            pass = System.getenv("DB_PASS");

            System.setProperty("hibernate.connection.url", url);
            System.setProperty("hibernate.connection.username", user);
            System.setProperty("hibernate.connection.password", pass);

            Flyway flyway = Flyway.configure()
                   .dataSource(url, user, pass)
                   .load();

            flyway.migrate();

            botToken = System.getenv("BOT_TOKEN");
        }

        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            Bot bot = new Bot(botToken);
            botsApplication.registerBot(botToken, bot);

            QueueNotificationService.getInstance().setBot(bot);

            QueueDao queueDao = new QueueDao();
            List<Queue> upcomingQueues = queueDao.getAllQueuesWithFutureStart();

            for (Queue queue : upcomingQueues) {
                QueueNotificationService.getInstance().scheduleQueueStartNotification(queue);
            }

            System.out.println("Bot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
