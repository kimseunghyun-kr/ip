import java.nio.file.Path;

import dicontainer.DependencyInjectionContainer;
import dicontainer.aopinterfaces.ExceptionHandlerInterceptor;
import dicontainer.aopinterfaces.LoggingInterceptor;
import dicontainer.aopinterfaces.TransactionalInterceptor;
import dicontainer.aopinterfaces.annotationinterfaces.ExceptionHandler;
import dicontainer.aopinterfaces.annotationinterfaces.Log;
import dicontainer.aopinterfaces.annotationinterfaces.Transactional;
import entity.command.CommandFactory;
import repository.FileBackedTaskRepository;
import repository.IFileBackedTaskRepository;
import repository.ITaskRepository;
import repository.entityManager.BabyEntityManager;
import repository.entityManager.TaskFlusher;
import repository.event.TaskEventLogger;
import runtime.ActionHandler;
import runtime.IBotRunTime;
import service.ITaskService;
import service.TaskRepositoryCoordinatorService;
import util.DirectoryInitializeUtils;



public class Spring {
    public static void main(String[] args) {
        // Initialize the DI container
        DependencyInjectionContainer container = new DependencyInjectionContainer();
        Path filePath = DirectoryInitializeUtils.initializeDataDirectory();
        Path logPath = DirectoryInitializeUtils.initializeLogDirectory();

        // Register interceptors
        container.registerInterceptor(Log.class, new LoggingInterceptor());
        container.registerInterceptor(Transactional.class, new TransactionalInterceptor());
        container.registerInterceptor(ExceptionHandler.class, new ExceptionHandlerInterceptor());

        // Register components
        container.register(TaskEventLogger.class, logPath);
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
