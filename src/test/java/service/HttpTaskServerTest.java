package test.java.service;

import com.google.gson.Gson;
import main.java.model.Epic;
import main.java.model.Status;
import main.java.model.SubTask;
import main.java.model.Task;
import main.java.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    TaskManager taskManager = Managers.getDefault();
    KVTaskClient kvTaskClient = new KVTaskClient("http://localhost:8078");
    Gson gson = new Gson();

    @BeforeAll
    public static void start() throws IOException {
        new KVServer().start();
        HttpTaskServer.main(new String[]{});
    }

    @Test
    void saveTaskTest() {
        taskManager.createTask("name", "description");
        assertEquals(gson.toJson(taskManager.getAllTasks()), kvTaskClient.load("task"));

    }

    @Test
    void deleteTaskTest() { // Для ревьювера: почему если назвать метод "deleteTaskTest" то
        Task task = taskManager.createTask("task", "description");
        int id = task.getId();
        taskManager.deleteTask(id);
        assertEquals("{}", kvTaskClient.load("task"));
    }

    @Test
    void saveUpdateTask() {
        Task taskUpdate = taskManager.createTask("task", "description");
        taskManager.updateTask(new Task
                (taskUpdate.getId(), "task1", "description task1", Status.IN_PROGRESS));
        assertEquals(gson.toJson(taskManager.getAllTasks()), kvTaskClient.load("task"));
    }

    @Test
    void saveEpicAndSubTaskTest() {
        Epic epic = taskManager.createEpicTask("epic", "description");
        taskManager.createSubTask(epic, "subTask", "description");
        assertEquals(gson.toJson(taskManager.getAllEpicTasks()), kvTaskClient.load("epic"));
    }


    @Test
    void saveUpdateEpicAndSubTaskTest() {
        Epic epicUpdate = taskManager.createEpicTask("epic", "description");
        SubTask subTaskUpdate = taskManager.createSubTask(epicUpdate, "subTask", "description");
        taskManager.updateEpic(new Epic(epicUpdate.getId(), "epic1", "description1"));
        taskManager.updateSubTask(epicUpdate, new SubTask
                (subTaskUpdate.getId(), "subTask1", "description subTask1", Status.IN_PROGRESS));
        assertEquals(gson.toJson(taskManager.getAllEpicTasks()), kvTaskClient.load("epic"));
    }

    @Test
    void deleteEpicAndSubTaskTest() {
        Epic epic = taskManager.createEpicTask("epic", "description");
        taskManager.createSubTask(epic, "subTask", "description");
        int id = epic.getId();
        taskManager.deleteEpicTask(id);
        assertEquals("{}", kvTaskClient.load("epic"));
    }

    @Test
    void saveHistory() {
        Task task1 = taskManager.createTask("task1", "description");
        Task task2 = taskManager.createTask("task2", "description");
        Task task3 = taskManager.createTask("task3", "description");
        taskManager.getTask(task3.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        assertEquals(gson.toJson(taskManager.history()), kvTaskClient.load("history"));
    }

    @Test
    void emptyHistory() {
        assertEquals("[]", kvTaskClient.load("history"));
    }

    @AfterEach
    void clear() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpicTasks();
    }
}
