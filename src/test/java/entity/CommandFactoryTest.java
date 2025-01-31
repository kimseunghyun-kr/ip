package entity;

import entity.command.*;
import mocks.MockTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import service.ITaskService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CommandFactory} using JUnit 5.
 * <p>
 * Follows the GIVEN-WHEN-THEN format for readability.
 */
public class CommandFactoryTest {
    private ITaskService mockTaskService;
    private CommandFactory commandFactory;

    @BeforeEach
    void setUp() {
        mockTaskService = new MockTaskService();
        commandFactory = new CommandFactory(mockTaskService);
    }

    @Test
    @DisplayName("GIVEN a valid command string WHEN createCommand is called THEN the correct Command instance should be returned")
    void testCreateValidCommand() {
        // GIVEN
        String commandString = "ADD";

        // WHEN
        Command command = commandFactory.createCommand(Actions.valueOf(commandString.toUpperCase()));

        // THEN
        assertNotNull(command);
        assertTrue(command instanceof AddCommand);
    }

    @Test
    @DisplayName("GIVEN an invalid lowercase add command string WHEN createCommand is called THEN the correct exception instance should be returned")
    void testCreateAddCommandException() {
        // GIVEN
        String commandString = "add";

        // WHEN
        assertThrows(RuntimeException.class, ()->commandFactory.createCommand(Actions.valueOf(commandString)));

        // THEN


    }

    @Test
    @DisplayName("GIVEN an invalid command string WHEN createCommand is called THEN an exception instance should be returned")
    void testCreateInvalidCommand() {
        // GIVEN
        String commandString = "unknown";

        // WHEN
        assertThrows(RuntimeException.class, ()->commandFactory.createCommand(Actions.valueOf(commandString)));

    }

    @Test
    @DisplayName("GIVEN a delete command string WHEN createCommand is called THEN a DeleteCommand instance should be returned")
    void testCreateDeleteCommand() {
        // GIVEN
        String commandString = "delete";

        // WHEN
        Command command = commandFactory.createCommand(Actions.valueOf(commandString.toUpperCase()));

        // THEN
        assertNotNull(command);
        assertTrue(command instanceof DeleteCommand);
    }

    @Test
    @DisplayName("GIVEN a list command string WHEN createCommand is called THEN a ListCommand instance should be returned")
    void testCreateListCommand() {
        // GIVEN
        String commandString = "list";

        // WHEN
        Command command = commandFactory.createCommand(Actions.valueOf(commandString.toUpperCase()));

        // THEN
        assertNotNull(command);
        assertTrue(command instanceof ListCommand);
    }
}