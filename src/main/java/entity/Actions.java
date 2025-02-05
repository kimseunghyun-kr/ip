package entity;

/**
 * Enum representing various actions that can be performed.
 */
public enum Actions {
    TERMINATE,
    ADD,
    LIST,
    MARK,
    UNMARK,
    DELETE,
    SEARCH,
    INVALID,
    CLEAR,
    UPDATE;

    /**
     * Converts a string command to an Actions enum.
     *
     * @param command the command string to be converted
     * @return the corresponding Actions enum, or INVALID if unrecognized
     */
    public static Actions fromString(String command) {
        try {
            if (command.equalsIgnoreCase("BYE")) {
                return Actions.TERMINATE;
            }
            if (command.equalsIgnoreCase("Todo") || command.equalsIgnoreCase("Event")
                    || command.equalsIgnoreCase("DeadLine")) {
                return Actions.ADD;
            }
            return Actions.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            return INVALID; // Default to INVALID for unrecognized strings
        }
    }
}
