package Repository;

import Entity.Queue;
import common.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.List;

public class QueueDao extends HibernateDao<Queue, Long>{

    public QueueDao(){
        super(Queue.class);
    }

    public List<Queue> getChatQueues(Long chatId){
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            String hql = "FROM Queue q WHERE q.chatId = :chatId";
            return session.createQuery(hql, Queue.class)
                    .setParameter("chatId", chatId)
                    .getResultList();
        }
    }

    public Queue getQueueByChatIdAndName(Long chatId, String queueName){
        String hql = """
            SELECT q FROM Queue q LEFT JOIN FETCH q.members
            WHERE q.chatId = :chatId AND q.queueName = :queueName
            """;

        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            return session.createQuery(hql, Queue.class)
                    .setParameter("chatId", chatId)
                    .setParameter("queueName", queueName)
                    .getSingleResult();
        }
    }

    public List<Queue> getAllQueuesWithFutureStart() {
        String hql = """
                FROM Queue q
                WHERE (q.startDate > :today)
                OR (q.startDate = :today AND q.startTime > :nowTime)
                """;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDateTime now = LocalDateTime.now();
            Query<Queue> query = session.createQuery(hql, Queue.class);

            query.setParameter("today", now.toLocalDate());
            query.setParameter("nowTime", now.toLocalTime());

            return query.list();
        }
    }

}
