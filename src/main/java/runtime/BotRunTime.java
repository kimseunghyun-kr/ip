package runtime;

import DIContainer.Proxiable;
import entity.Actions;
import entity.Command.Command;
import entity.Command.TerminationCommand;
import service.TaskService;

import java.util.Scanner;

import static util.ChatBotUtil.*;

public class BotRunTime implements Proxiable {

    private final TaskService taskService;
    private final ActionHandler actionHandler;
    Scanner scanner = new Scanner(System.in);

    public BotRunTime(TaskService taskService, ActionHandler actionHandler) {
        this.taskService = taskService;
        this.actionHandler = actionHandler;
    }

    public void run(){
        linesep();
        introSequence();
        while(true) {
            linesep();
            String input = scanner.nextLine();
            linesep();
            Command command = actionHandler.resolveAction(input);
            if(command instanceof TerminationCommand) {
                break;
            }
        }
        exitSequence();
    }
}
