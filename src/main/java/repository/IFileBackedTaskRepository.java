package repository;

import dicontainer.aopinterfaces.annotationinterfaces.ProxyEnabled;
import dicontainer.Proxiable;

import java.util.UUID;

/**
 * A file-backed repository interface for managing persistent storage operations.
 * exists mostly only to be picked up as a JDK proxy
 */
@ProxyEnabled(implementation = FileBackedTaskRepository.class)
public interface IFileBackedTaskRepository extends Proxiable {

    /**
     * Writes all buffered changes to disk.
     */
    void flush();

    /**
     * Marks an entity as modified, scheduling it for persistence.
     *
     * @param id The unique identifier of the entity.
     * @return The same {@link UUID} of the marked entity.
     */
    UUID markDirty(UUID id);
}
