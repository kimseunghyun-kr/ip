package runtime;

import repository.ItineraryRepository;

import java.util.Scanner;

import static util.ChatBotUtil.*;

public class BotRunTime {
    ItineraryRepository itineraryRepository = new ItineraryRepository();
    Scanner scanner = new Scanner(System.in);
    public void run(){
        linesep();
        introSequence();
        while(true) {
            linesep();
            String input = scanner.nextLine();
            linesep();
            String response;
            if(input.equals("bye")) {
                break;
            } if(input.equals("list")) {
                response = itineraryRepository.getAll();
            } else {
                response = itineraryRepository.store(input);
            }
            System.out.println(response);
        }
        exitSequence();
    }
}
