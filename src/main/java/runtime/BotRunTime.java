package runtime;

import DIContainer.Proxiable;
import entity.Actions;
import entity.Command.Command;
import entity.Command.TerminationCommand;
import exceptions.UserFacingException;
import repository.entityManager.TaskFlusher;
import service.ITaskService;

import java.util.Scanner;

import static util.ChatBotUtil.*;

public class BotRunTime implements IBotRunTime {

    private final ActionHandler actionHandler;
    private final TaskFlusher taskFlusher;
    Scanner scanner = new Scanner(System.in);

    public BotRunTime( ActionHandler actionHandler, TaskFlusher taskFlusher) {
        this.actionHandler = actionHandler;
        this.taskFlusher = taskFlusher;
    }

    public void run(){
        taskFlusher.start();
        linesep();
        introSequence();
        while(true) {
            try {
                linesep();
                String input = scanner.nextLine();
                linesep();
                Command command = actionHandler.resolveAction(input);
                if (command instanceof TerminationCommand) {
                    break;
                }
            } catch(UserFacingException e) {
                System.out.println("outermost loop catch :: " + e.getMessage());
            }
        }
        taskFlusher.stop();
        exitSequence();
    }
}
