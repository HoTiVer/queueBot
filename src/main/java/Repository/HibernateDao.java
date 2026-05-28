package Repository;

import common.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;


public abstract class HibernateDao<E, K> implements BaseDao<E, K> {

    private final HibernateUtil hibernateUtil = new HibernateUtil();
    private final Class<E> entityClass;
    private final SessionFactory sessionFactory;

    public HibernateDao(Class<E> entityClass) {
        this.entityClass = entityClass;
        sessionFactory = hibernateUtil.getSessionFactory();
    }

    @Override
    public E save(E entity) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();
        }
        return entity;
    }

    @Override
    public void delete(K id) {
        try (Session session = sessionFactory.openSession()){
            Optional<E> entity = findById(id);
            if (entity.isPresent()){
                session.beginTransaction();
                session.remove(entity.get());
            }
            session.getTransaction().commit();
        }
    }

    @Override
    public E update(E entity) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.merge(entity);
            session.getTransaction().commit();
        }
        return entity;
    }

    @Override
    public Optional<E> findById(K id) {
        try (Session session = sessionFactory.openSession()){
            return Optional.ofNullable(session.find(entityClass, id));
        }
    }

    @Override
    public List<E> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from " + entityClass.getSimpleName(),
                    entityClass).list();
        }
    }
}
