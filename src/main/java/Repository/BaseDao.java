package Repository;

import java.util.List;
import java.util.Optional;

public interface BaseDao<E, K> {
    E save(E entity);

    void delete(K id);

    E update(E entity);

    Optional<E> findById(K id);

    List<E> findAll();
}
