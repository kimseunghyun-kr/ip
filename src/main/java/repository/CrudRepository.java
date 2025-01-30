package repository;

import DIContainer.Proxiable;

import java.util.List;
import java.util.Optional;

/**
 * A simplified version of a CRUD repository (similar to Spring Data).
 */
public interface CrudRepository<T, ID> {
    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    T deleteById(ID id);
}

