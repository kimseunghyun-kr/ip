package runtime;

import java.util.Scanner;

import static util.ChatBotUtil.*;

public class BotRunTime {
    public void run(){
        Scanner scanner = new Scanner(System.in);
        linesep();
        introSequence();
        while(true) {
            linesep();
            String input = scanner.nextLine();
            linesep();
            if(input.equals("bye")) {
                break;
            }
            System.out.println(input);
        }
        exitSequence();
    }
}
