package repository;

import java.util.List;
import java.util.Optional;

/**
 * A simplified generic CRUD repository interface, similar to Spring Data repositories.
 * Provides basic operations for managing entities of type {@code T} identified by {@code ID}.
 *
 * @param <T>  The type of entity to be managed.
 * @param <ID> The type of the entity's unique identifier.
 */
public interface CrudRepository<T, ID> {

    /**
     * Saves the given entity. If the entity already exists, it may be updated.
     *
     * @param entity The entity to save.
     * @return The saved entity.
     */
    T save(T entity);

    /**
     * Retrieves an entity by its ID.
     *
     * @param id The unique identifier of the entity.
     * @return An {@link Optional} containing the found entity, or empty if not found.
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all entities managed by the repository.
     *
     * @return A list of all entities.
     */
    List<T> findAll();

    /**
     * Deletes an entity by its ID.
     *
     * @param id The unique identifier of the entity to delete.
     * @return The deleted entity, or {@code null} if not found.
     */
    T deleteById(ID id);
}
