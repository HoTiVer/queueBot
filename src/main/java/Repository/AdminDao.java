package Repository;

import Entity.Admin;
import common.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class AdminDao extends HibernateDao<Admin, Long>{
    public AdminDao() {
        super(Admin.class);
    }

    public List<Admin> getChatAdmins(Long chatId) {
        List<Admin> admins = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            session.beginTransaction();

            String hql = "SELECT a FROM Admin a WHERE a.chatId = :chatId";
            Query<Admin> query = session.createQuery(hql, Admin.class);
            query.setParameter("chatId", chatId);
            admins = query.getResultList();

            session.getTransaction().commit();
        } catch (Exception e){
            System.out.println(e.toString());
        }
        return admins;
    }
}
