package Repository;

import Entity.Member;
import common.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class MemberDao extends HibernateDao<Member, Long> {

    public MemberDao(){
        super(Member.class);
    }

    public void incrementPositionsFrom(Long queueId, int startPosition) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            String hql = "UPDATE Member m SET m.position = m.position + 1 " +
                    "WHERE m.queueId = :queueId AND m.position >= :startPos";

            Query<?> query = session.createQuery(hql);
            query.setParameter("queueId", queueId);
            query.setParameter("startPos", startPosition);

            query.executeUpdate();

            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
