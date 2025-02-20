package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import controller.ITaskController;
import entity.MockTaskController;
import entity.tasks.Task;
import mocks.MockTaskCoordinatorRepositoryService;
import mocks.MockTaskRepository;

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
        List<Task> tasks = taskService.getAllTasks();

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
        List<Task> results = taskService.searchByKeyword("Important");

        // THEN
        assertTrue(results.contains(task1));
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
        List<Task> tasks = taskService.getAllTasks();

        // THEN
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
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