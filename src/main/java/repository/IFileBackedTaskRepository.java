package repository;


import DIContainer.AOPInterfaces.AnnotationInterfaces.ProxyEnabled;
import DIContainer.Proxiable;

import java.util.UUID;

@ProxyEnabled(implementation = FileBackedTaskRepository.class)
public interface IFileBackedTaskRepository extends Proxiable {
    public void flush();
    public UUID markDirty(UUID id);
}
