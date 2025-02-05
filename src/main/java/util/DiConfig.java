package util;

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
import service.ActionHandler;
import runtime.IBotRunTime;
import service.ITaskService;
import service.TaskRepositoryCoordinatorService;

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
        if (cli) {
            container.register(IDispatcher.class, CliDispatcher.class);
            container.register(IBotRunTime.class); // Register BotRunTime
        } else {
            container.register(IDispatcher.class, GuiDispatcher.class);
            container.register(IBotRunTime.class);
        }
        container.register(TaskRepositoryCoordinatorService.class);
        container.register(JavaFxLauncher.class);
        container.register(ActionHandler.class);
        container.register(CommandFactory.class);
        container.register(TaskFlusher.class);
        container.register(BabyEntityManager.class);

    }
}
