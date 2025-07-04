package Service;


import Entity.Queue;
import botMain.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.*;

public class QueueNotificationService {

    private static volatile QueueNotificationService instance;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private Bot bot;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private QueueNotificationService() {

    }

    public static QueueNotificationService getInstance() {
        if (instance == null) {
            synchronized (QueueNotificationService.class) {
                if (instance == null) {
                    instance = new QueueNotificationService();
                }
            }
        }
        return instance;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public void scheduleQueueNotification(Queue queue) {
        LocalDateTime targetDateTime = LocalDateTime.of(queue.getStartDate(), queue.getStartTime());
        long delayMillis = Duration.between(LocalDateTime.now(), targetDateTime).toMillis();

        if (delayMillis <= 0) {
            bot.sendMessage(queue.getChatId(), "Queue \"" + queue.getQueueName() + "\" has already started!");
            return;
        }

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            bot.sendMessage(queue.getChatId(), "Queue \"" + queue.getQueueName() + "\" has started!");
            scheduledTasks.remove(queue.getId());
        }, delayMillis, TimeUnit.MILLISECONDS);

        scheduledTasks.put(queue.getId(), future);

        String msg = "Scheduled notification for \"" + queue.getQueueName() + "\" after " + delayMillis + " ms.";
        log.info(msg);
        System.out.println(msg);
    }

    public void cancelQueueNotification(Long queueId) {
        ScheduledFuture<?> future = scheduledTasks.get(queueId);
        if (future != null) {
            future.cancel(false);
            scheduledTasks.remove(queueId);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
