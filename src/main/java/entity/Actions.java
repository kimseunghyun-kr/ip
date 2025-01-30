package entity;

public enum Actions {
    TERMINATE,
    ADD,
    LIST,
    MARK,
    UNMARK,
    DELETE,
    SEARCH,
    INVALID;

    public static Actions fromString(String command) {
        try {
            if (command.equalsIgnoreCase("BYE")) {
                return Actions.TERMINATE;
            }
            if (command.equalsIgnoreCase("Todo") || command.equalsIgnoreCase("Event") ||
                    command.equalsIgnoreCase("DeadLine")) {
                return Actions.ADD;
            }
            return Actions.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            return INVALID; // Default to INVALID for unrecognized strings
        }
    }
}
