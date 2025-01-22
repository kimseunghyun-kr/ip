import DIContainer.AOPInterfaces.AnnotationInterfaces.ExceptionHandler;
import DIContainer.AOPInterfaces.AnnotationInterfaces.Log;
import DIContainer.AOPInterfaces.AnnotationInterfaces.Transactional;
import DIContainer.AOPInterfaces.ExceptionHandlerInterceptor;
import DIContainer.AOPInterfaces.LoggingInterceptor;
import DIContainer.AOPInterfaces.TransactionalInterceptor;
import DIContainer.DIContainer;
import entity.Command.CommandFactory;
import repository.ITaskRepository;
import repository.TaskRepository;
import runtime.ActionHandler;
import runtime.BotRunTime;
import service.TaskService;

public class Spring {
    public static void main(String[] args) {
        // Initialize the DI container
        DIContainer container = new DIContainer();


        // Register interceptors
        container.registerInterceptor(Log.class, new LoggingInterceptor());
        container.registerInterceptor(Transactional.class, new TransactionalInterceptor());
        container.registerInterceptor(ExceptionHandler.class, new ExceptionHandlerInterceptor());

        // Register components
        container.register(ITaskRepository.class);
        container.register(TaskService.class);
        container.register(ActionHandler.class);
        container.register(CommandFactory.class);
        container.register(BotRunTime.class); // Register BotRunTime





        // Pre-initialize all dependencies
        container.initialize();

        // Start the runtime
        BotRunTime botRunTime = container.resolve(BotRunTime.class);
        botRunTime.run();
    }
}
