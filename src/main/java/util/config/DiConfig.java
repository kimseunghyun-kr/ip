package util.config;

import java.nio.file.Path;

import controller.ITaskController;
import controller.TaskController;
import dicontainer.DependencyInjectionContainer;
import dicontainer.aopinterfaces.ExceptionHandlerInterceptor;
import dicontainer.aopinterfaces.LoggingInterceptor;
import dicontainer.aopinterfaces.TransactionalInterceptor;
import dicontainer.aopinterfaces.annotationinterfaces.ExceptionHandler;
import dicontainer.aopinterfaces.annotationinterfaces.Log;
import dicontainer.aopinterfaces.annotationinterfaces.Transactional;
import dispatcher.CliDispatcher;
import dispatcher.GuiDispatcher;
import dispatcher.IDispatcher;
import entity.command.CommandFactory;
import gui.JavaFxLauncher;
import repository.FileBackedTaskRepository;
import repository.IFileBackedTaskRepository;
import repository.ITaskRepository;
import repository.entitymanager.TaskFlusher;
import repository.event.TaskEventLogger;
import runtime.IBotRunTime;
import service.ActionHandler;
import service.CommandExecutionService;
import service.ITaskService;
import service.TaskRepositoryCoordinatorService;
import service.interactiveexecutionservice.CliInteractiveExecutionService;
import service.interactiveexecutionservice.GuiInteractiveExecutionService;
import service.interactiveexecutionservice.InteractiveExecutionService;

/**
 * Configuration class for dependency injection setup.
 * <p>
 * This class registers components, interceptors, and services in the
 * {@link DependencyInjectionContainer}, allowing for dependency management
 * across the application.
 * </p>
 */
public class DiConfig {

    /**
     * Registers configurations and dependencies in the given dependency injection container.
     * <p>
     * This method sets up interceptors, repositories, services, and controllers.
     * Depending on the {@code cli} flag, it registers either CLI or GUI-specific components.
     * </p>
     *
     * @param container The dependency injection container where components are registered.
     * @param logPath   The path for logging events.
     * @param filePath  The path for file-backed task storage.
     * @param cli       A flag indicating whether the application runs in CLI mode.
     */
    public static void registerConfig(DependencyInjectionContainer container, Path logPath,
                                      Path filePath, boolean cli) {
        // Register interceptors
        container.registerInterceptor(Log.class, new LoggingInterceptor());
        container.registerInterceptor(Transactional.class, new TransactionalInterceptor());
        container.registerInterceptor(ExceptionHandler.class, new ExceptionHandlerInterceptor());

        // Register components
        container.register(TaskEventLogger.class, logPath);
        container.register(FileBackedTaskRepository.class, filePath);
        container.register(IFileBackedTaskRepository.class);
        container.register(ITaskService.class);
        container.register(ITaskRepository.class);
        container.register(ITaskController.class, TaskController.class);
        container.register(CommandExecutionService.class);
        if (cli) {
            container.register(IDispatcher.class, CliDispatcher.class);
            container.register(IBotRunTime.class); // Register BotRunTime
            container.register(InteractiveExecutionService.class, CliInteractiveExecutionService.class);
        } else {
            container.register(IDispatcher.class, GuiDispatcher.class);
            container.register(IBotRunTime.class);
            container.register(InteractiveExecutionService.class, GuiInteractiveExecutionService.class);
            container.register(FxmlStaticSetterInjectionConfig.class);
        }
        container.register(TaskRepositoryCoordinatorService.class);
        container.register(JavaFxLauncher.class);
        container.register(ActionHandler.class);
        container.register(CommandFactory.class);
        container.register(TaskFlusher.class);
    }
}
