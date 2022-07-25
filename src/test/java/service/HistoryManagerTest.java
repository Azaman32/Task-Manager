package test.java.service;

import main.java.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.service.InMemoryHistoryManager;
import main.java.service.InMemoryTaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {

     InMemoryTaskManager inMemoryTaskManager;
     InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    void init() {
        inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryHistoryManager = InMemoryHistoryManager.getInstance();
        inMemoryHistoryManager.deleteAllHistory();
    }

    @Test
    void linkLastTest() {
        Task task1 = inMemoryTaskManager.createTask("task1", "description task1");
        Task task2 = inMemoryTaskManager.createTask("task2", "description task2");
        Task task3 = inMemoryTaskManager.createTask("task3", "description task3");
        inMemoryHistoryManager.linkLast(task3);
        inMemoryHistoryManager.linkLast(task1);
        inMemoryHistoryManager.linkLast(task2);
        List<Task> nodeList = new ArrayList<>();
        nodeList.add(task2);
        nodeList.add(task1);
        nodeList.add(task3);
        System.out.println(inMemoryHistoryManager.getHistory());
        System.out.println(nodeList);
        assertEquals(nodeList, inMemoryHistoryManager.getHistory());
    }

    @Test
    void removeMiddleHistoryTaskTest() {
        Task task1 = inMemoryTaskManager.createTask("task1", "description task1");
        Task task2 = inMemoryTaskManager.createTask("task2", "description task2");
        Task task3 = inMemoryTaskManager.createTask("task3", "description task3");
        inMemoryHistoryManager.linkLast(task3);
        inMemoryHistoryManager.linkLast(task1);
        inMemoryHistoryManager.linkLast(task2);
        inMemoryHistoryManager.removeHistoryTask(task1.getId());
        List<Task> nodeList = new ArrayList<>();
        nodeList.add(task2);
        nodeList.add(task3);
        assertEquals(inMemoryHistoryManager.getHistory(), nodeList);
    }

    @Test
    void removeFirstHistoryTaskTest() {
        Task task1 = inMemoryTaskManager.createTask("task1", "description task1");
        Task task2 = inMemoryTaskManager.createTask("task2", "description task2");
        Task task3 = inMemoryTaskManager.createTask("task3", "description task3");
        inMemoryHistoryManager.linkLast(task3);
        inMemoryHistoryManager.linkLast(task1);
        inMemoryHistoryManager.linkLast(task2);
        inMemoryHistoryManager.removeHistoryTask(task2.getId());
        List<Task> nodeList = new ArrayList<>();
        nodeList.add(task1);
        nodeList.add(task3);
        assertEquals(inMemoryHistoryManager.getHistory(), nodeList);
    }

    @Test
    void removeLastHistoryTaskTest() {
        Task task1 = inMemoryTaskManager.createTask("task1", "description task1");
        Task task2 = inMemoryTaskManager.createTask("task2", "description task2");
        Task task3 = inMemoryTaskManager.createTask("task3", "description task3");
        inMemoryHistoryManager.linkLast(task3);
        inMemoryHistoryManager.linkLast(task1);
        inMemoryHistoryManager.linkLast(task2);
        inMemoryHistoryManager.removeHistoryTask(task3.getId());
        List<Task> nodeList = new ArrayList<>();
        nodeList.add(task2);
        nodeList.add(task1);
        assertEquals(inMemoryHistoryManager.getHistory(), nodeList);
    }

    @Test
    void getEmptyHistoryTest() {
        Task task1 = inMemoryTaskManager.createTask("task1", "description task1");
        Task task2 = inMemoryTaskManager.createTask("task2", "description task2");
        Task task3 = inMemoryTaskManager.createTask("task3", "description task3");
        inMemoryHistoryManager.removeHistoryTask(task3.getId());
        inMemoryHistoryManager.removeHistoryTask(task1.getId());
        inMemoryHistoryManager.removeHistoryTask(task2.getId());
        assertEquals(inMemoryHistoryManager.getHistory().size(), 0);
    }
}
