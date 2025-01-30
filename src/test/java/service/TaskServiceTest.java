package service;

import entity.tasks.Task;
import mocks.MockTaskCoordinatorRepositoryService;
import mocks.MockTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {

    private TaskService taskService;
    private MockTaskRepository mockTaskRepository;
    private MockTaskCoordinatorRepositoryService mockTaskCoordinatorRepositoryService;

    @BeforeEach
    void setUp() {
        mockTaskRepository = new MockTaskRepository();
        mockTaskCoordinatorRepositoryService = new MockTaskCoordinatorRepositoryService(mockTaskRepository);
        taskService = new TaskService(mockTaskCoordinatorRepositoryService, mockTaskRepository);
    }

    @Test
    @DisplayName("GIVEN a new task WHEN added THEN it should be retrievable")
    void testAddTask() {
        // GIVEN
        Task newTask = new Task("Task");

        // WHEN
        taskService.addTask(new ArrayList<>(List.of("TODO", "Task")));
        String tasks = taskService.getAllTasks();

        // THEN
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
        assertEquals(newTask.getName(), mockTaskRepository.findAll().get(0).getName());
    }

    @Test
    @DisplayName("GIVEN a task WHEN deleted THEN it should no longer exist")
    void testDeleteTask() {
        // GIVEN
        taskService.addTask(new ArrayList<>(List.of("TODO", "Task")));
        String tasks = taskService.getAllTasks();

        // WHEN
        taskService.deleteTask(1);

        // THEN
        assertTrue(taskService.getAllTasks().isEmpty());
    }

    @Test
    @DisplayName("GIVEN a list of tasks WHEN searched THEN matching tasks should be returned")
    void testSearchTask() {
        // GIVEN
        Task task1 = new Task("Important Task");
        Task task2 = new Task("Casual Task");
        mockTaskRepository.save(task1);
        mockTaskRepository.save(task2);

        // WHEN
        String results = taskService.SearchByKeyword("Important");

        // THEN
        assertTrue(results.contains(task1.toString()));
    }

    @Test
    @DisplayName("GIVEN a task repository WHEN retrieving all tasks THEN all should be returned")
    void testGetAllTasks() {
        // GIVEN
        Task task1 = new Task("Task A");
        Task task2 = new Task("Task B");
        mockTaskRepository.save(task1);
        mockTaskRepository.save(task2);

        // WHEN
        String tasks = taskService.getAllTasks();

        // THEN
        assertTrue(tasks.contains(task1.toString()));
        assertTrue(tasks.contains(task2.toString()));
    }

    @Test
    @DisplayName("GIVEN a task WHEN marked as completed THEN it should be updated")
    void testMarkTaskAsCompleted() {
        // GIVEN
        Task task = new Task("Incomplete Task");
        mockTaskRepository.save(task);

        // WHEN
        taskService.markDone(1);

        // THEN
        assertTrue(task.getCompleted());
    }
}