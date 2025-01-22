import DIContainer.DIContainer;
import entity.Command.CommandFactory;
import runtime.ActionHandler;
import runtime.BotRunTime;
import repository.TaskRepository;
import service.TaskService;

public class Spring {
    public static void main(String[] args) {
        // Initialize the DI container
        DIContainer container = new DIContainer();

        // Register components
        container.register(TaskRepository.class);
        container.register(TaskService.class);
        container.register(CommandFactory.class);
        container.register(ActionHandler.class);


        // Pre-initialize all dependencies
        container.initialize();

        // Resolve the runtime components
        TaskService taskService = container.resolve(TaskService.class);
        ActionHandler actionHandler = container.resolve(ActionHandler.class);

        // Start the runtime
        BotRunTime botRunTime = new BotRunTime(taskService, actionHandler);
        botRunTime.run();
    }
}
