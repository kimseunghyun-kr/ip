import DIContainer.AOPInterfaces.AnnotationInterfaces.ExceptionHandler;
import DIContainer.AOPInterfaces.AnnotationInterfaces.Log;
import DIContainer.AOPInterfaces.AnnotationInterfaces.Transactional;
import DIContainer.AOPInterfaces.ExceptionHandlerInterceptor;
import DIContainer.AOPInterfaces.LoggingInterceptor;
import DIContainer.AOPInterfaces.TransactionalInterceptor;
import DIContainer.DIContainer;
import entity.Command.CommandFactory;
import repository.FileBackedTaskRepository;
import repository.IFileBackedTaskRepository;
import repository.ITaskRepository;
import repository.entityManager.BabyEntityManager;
import repository.entityManager.TaskFlusher;
import runtime.ActionHandler;
import runtime.IBotRunTime;
import service.ITaskService;
import service.TaskRepositoryCoordinatorService;
import util.DirectoryInitializeUtils;

import java.nio.file.Path;

public class Spring {
    public static void main(String[] args) {
        // Initialize the DI container
        DIContainer container = new DIContainer();
        Path filePath = DirectoryInitializeUtils.initializeDirectory();

        // Register interceptors
        container.registerInterceptor(Log.class, new LoggingInterceptor());
        container.registerInterceptor(Transactional.class, new TransactionalInterceptor());
        container.registerInterceptor(ExceptionHandler.class, new ExceptionHandlerInterceptor());

        // Register components
        container.register(FileBackedTaskRepository.class, filePath);
        container.register(IFileBackedTaskRepository.class);
        container.register(ITaskRepository.class);
        container.register(ITaskService.class);
        container.register(TaskRepositoryCoordinatorService.class);
        container.register(ActionHandler.class);
        container.register(CommandFactory.class);
        container.register(TaskFlusher.class);
        container.register(BabyEntityManager.class);
        container.register(IBotRunTime.class); // Register BotRunTime

        // Pre-initialize all dependencies
        container.initialize();

        // Start the runtime
        IBotRunTime botRunTime = container.resolve(IBotRunTime.class);
        botRunTime.run();
    }
}
