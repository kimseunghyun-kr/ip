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
