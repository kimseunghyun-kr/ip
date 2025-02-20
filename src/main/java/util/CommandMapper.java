package util;

import java.util.HashMap;
import java.util.Map;

import entity.Actions;
import exceptions.UserFacingException;

/**
 * Utility class for mapping user input commands to {@link Actions}.
 */
public class CommandMapper {
    private static final Map<String, Actions> COMMAND_MAPPINGS = new HashMap<>();

    // Static block to initialize mappings
    static {
        configureMappings();
    }

    /**
     * Initializes predefined command mappings.
     */
    private static void configureMappings() {
        COMMAND_MAPPINGS.put("find", Actions.SEARCH);
        COMMAND_MAPPINGS.put("lookup", Actions.SEARCH);
        COMMAND_MAPPINGS.put("search", Actions.SEARCH);

        COMMAND_MAPPINGS.put("delete", Actions.DELETE);
        COMMAND_MAPPINGS.put("remove", Actions.DELETE);

        COMMAND_MAPPINGS.put("todo", Actions.ADD);
        COMMAND_MAPPINGS.put("deadline", Actions.ADD);
        COMMAND_MAPPINGS.put("event", Actions.ADD);

        COMMAND_MAPPINGS.put("mark", Actions.MARK);
        COMMAND_MAPPINGS.put("unmark", Actions.UNMARK);

        COMMAND_MAPPINGS.put("exit", Actions.TERMINATE);
        COMMAND_MAPPINGS.put("quit", Actions.TERMINATE);
        COMMAND_MAPPINGS.put("bye", Actions.TERMINATE);
    }

    /**
     * Maps a user-provided command string to an {@link Actions} enum.
     *
     * @param command The user input command.
     * @return The corresponding {@link Actions} enum.
     * @throws UserFacingException If the command is unknown or deprecated.
     */
    public static Actions mapCommandToAction(String command) throws UserFacingException {
        command = command.toLowerCase();

        // Direct match
        if (COMMAND_MAPPINGS.containsKey(command)) {
            return COMMAND_MAPPINGS.get(command);
        }

        // Handling deprecated commands
        if (command.equals("add")) {
            throw new UserFacingException("The 'add' action is deprecated. Use 'create' instead.");
        }

        // Suggest the closest valid command
        for (String validCommand : COMMAND_MAPPINGS.keySet()) {
            if (isSimilar(command, validCommand)) {
                throw new UserFacingException("Unknown command: '"
                        + command + "'. Did you mean '" + validCommand + "'?");
            }
        }
        try {
            return Actions.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserFacingException("Unknown command: '"
                    + command + "'. Type 'help' for a list of valid commands.");
        }
    }

    /**
     * Checks if two strings are similar based on Levenshtein distance.
     *
     * @param input        The user-provided command.
     * @param validCommand A known valid command.
     * @return True if the input is close to the valid command.
     */
    private static boolean isSimilar(String input, String validCommand) {
        return levenshteinDistance(input, validCommand) <= 2; // Allow up to 2 character differences
    }

    /**
     * Calculates Levenshtein Distance between two strings.
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return The number of single-character edits required to change s1 into s2.
     */
    private static int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(Math.min(
                                    dp[i - 1][j] + 1, // Deletion
                                    dp[i][j - 1] + 1), // Insertion
                            dp[i - 1][j - 1] + cost // Substitution
                    );
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }
}
