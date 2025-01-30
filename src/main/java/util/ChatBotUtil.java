package util;

public class ChatBotUtil {
    public static void linesep() {
        String linesep = "______________________________________________\n";
        System.out.println(linesep);
    }

    public static void introSequence(){
        String greet = "Hello from\n";
        String greet2 = "what can I do for you today \n";
        System.out.println(greet);
        linesep();
        System.out.println(logoGen());
        linesep();
        System.out.println(greet2);
    }

    public static void exitSequence() {
        String exitSeq = "Bye Hope to see you again soon!";
        System.out.println(exitSeq);
        linesep();
    }


    public static String logoGen() {
        String[] words = getBaseLogo();
        String[] colors = {
                "\033[31m", // Red
                "\033[38;2;255;165;0m", // Orange
                "\033[33m", // Yellow
                "\033[32m", // Green
                "\033[34m", // Blue
                "\033[35m"  // Magenta (Purple)
        };
        String reset = "\033[0m";


        StringBuilder coloredLogo = new StringBuilder();

        int colorIndex = 0;
        for(String word : words) {
            if(!word.isEmpty()) {
                coloredLogo.append(colors[colorIndex % colors.length]).append(word);
                colorIndex++;
            }
        }
        coloredLogo.append(reset);
        return coloredLogo.toString();
    }

    private static String[] getBaseLogo() {
        String logo = ".d8888.; d8888b.; d8888b.; d888888b; d8b   db;  d888b;  \n" +
                "88'  YP; 88  `8D; 88  `8D;   `88';   888o  88; 88' Y8b; \n" +
                "`8bo.;   88oodD'; 88oobY';    88;    88V8o 88; 88;      \n" +
                "  `Y8b.; 88~~~;   88`8b;      88;    88 V8o88; 88  ooo; \n" +
                "db   8D; 88;      88 `88.;   .88.;   88  V888; 88. ~8~; \n" +
                "`8888Y'; 88;      88   YD; Y888888P; VP   V8P;  Y888P;  \n";

        return logo.split(";");
    }

}
