package util;

/**
 * Utility class for chatbot functionalities such as displaying separators,
 * introduction, exit sequences, and logo generation.
 */
public class ChatBotUtil {

    /**
     * Prints a line separator for visual clarity.
     */
    public static void linesep() {
        String linesep = "______________________________________________\n";
        System.out.println(linesep);
    }

    /**
     * Displays the introduction sequence with a greeting and logo.
     */
    public static void introSequence() {
        String greet = "Hello from\n";
        String greet2 = "what can I do for you today \n";
        System.out.println(greet);
        linesep();
        System.out.println(logoGen());
        linesep();
        System.out.println(greet2);
    }

    /**
     * Displays the exit sequence message.
     */
    public static void exitSequence() {
        String exitSeq = "exiting";
        System.out.println(exitSeq);
        linesep();
    }

    /**
     * Generates a colorful logo string.
     *
     * @return a string representation of the logo with colors.
     */
    public static String logoGen() {
        String[] words = getBaseLogo();
        String[] colors = {
            "\033[31m", // Red
            "\033[38;2;255;165;0m", // Orange
            "\033[33m", // Yellow
            "\033[32m", // Green
            "\033[34m", // Blue
            "\033[35m" // Magenta (Purple)
        };
        String reset = "\033[0m";

        StringBuilder coloredLogo = new StringBuilder();
        int colorIndex = 0;
        for (String word : words) {
            if (!word.isEmpty()) {
                coloredLogo.append(colors[colorIndex % colors.length]).append(word);
                colorIndex++;
            }
        }
        coloredLogo.append(reset);
        return coloredLogo.toString();
    }

    /**
     * Retrieves the base logo as an array of strings.
     *
     * @return an array representing the logo in text format.
     */
    private static String[] getBaseLogo() {
        String logo = """
                .d8888.; d8888b.; d8888b.; d888888b; d8b   db;  d888b; \s
                88'  YP; 88  `8D; 88  `8D;   `88';   888o  88; 88' Y8b;\s
                `8bo.;   88oodD'; 88oobY';    88;    88V8o 88; 88;     \s
                  `Y8b.; 88~~~;   88`8b;      88;    88 V8o88; 88  ooo;\s
                db   8D; 88;      88 `88.;   .88.;   88  V888; 88. ~8~;\s
                `8888Y'; 88;      88   YD; Y888888P; VP   V8P;  Y888P; \s
                """;
        return logo.split(";");
    }
}
