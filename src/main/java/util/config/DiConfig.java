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
import repository.entitymanager.BabyEntityManager;
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

public class DiConfig {
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
        container.register(BabyEntityManager.class);

    }
}
