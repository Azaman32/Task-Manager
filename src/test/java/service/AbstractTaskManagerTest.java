package test.java.service;

import main.java.model.Epic;
import main.java.model.Status;
import main.java.model.SubTask;
import main.java.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.function.Executable;
import main.java.service.TaskManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class AbstractTaskManagerTest<T extends TaskManager> {

    T manager;

    @Test
    void createTaskTest() {
        Task task = manager.createTask("name", "description");
        assertEquals(task, manager.getTask(task.getId()));
    }

    @Test
    void getTaskTest() {
        Task task = manager.createTask("name", "description");
        assertEquals(task, manager.getTask(task.getId()));
    }

    @Test
    void getTaskIncorrectIdTest() {
        assertNull(manager.getTask(-1));
    }

    @Test
    void deleteTaskTest() {
        Task task = manager.createTask("task", "description");
        int id = task.getId();
        manager.deleteTask(id);
        assertNull(manager.getTask(id));
    }

    @Test
    void deleteAllTasksTest() {
        manager.createTask("task1", "description task1");
        manager.createTask("task2", "description task2");
        assertEquals(2, manager.getAllTasks().size());
        manager.deleteAllTasks();
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    void updateTaskTest() {
        Task taskUpdate = manager.createTask("task", "description task");
        taskUpdate = manager.updateTask(new Task
                (taskUpdate.getId(), "task1", "description task1", Status.IN_PROGRESS));
        assertEquals(taskUpdate.getStatus(), manager.getTask(taskUpdate.getId()).getStatus());
        assertEquals(taskUpdate.getName(), manager.getTask(taskUpdate.getId()).getName());
        assertEquals(taskUpdate.getDescription(), manager.getTask(taskUpdate.getId()).getDescription());
    }

    @Test
    void getEndTimeTest() {
        Task taskTime = manager.createTask("task", "description task");
        taskTime = manager.updateTask(new Task
                (taskTime.getId(), "task1", "description task1", Status.IN_PROGRESS,
                        LocalDateTime.of(2022, 05, 17, 19, 30), 240));
        assertEquals(taskTime.getEndTime(), LocalDateTime.of(2022, 05, 17, 23, 30));
    }

    @Test
    void createEpicTaskTest() {
        Epic epic = manager.createEpicTask("epic", "description");
        assertEquals(epic, manager.getEpicTask(epic.getId()));
    }

    @Test
    void getEpicTaskTest() {
        Epic epic = manager.createEpicTask("epic", "description");
        assertEquals(epic, manager.getEpicTask(epic.getId()));
    }

    @Test
    void deleteEpicTaskTest() {
        Epic epic = manager.createEpicTask("epic", "description");
        int id = epic.getId();
        manager.deleteEpicTask(id);
        assertNull(manager.getEpicTask(id));
    }

    @Test
    void deleteAllEpicTaskTest() {
        manager.createEpicTask("epic1", "description epic1");
        manager.createEpicTask("epic2", "description epic2");
        assertEquals(2, manager.getAllEpicTasks().size());
        manager.deleteAllEpicTasks();
        assertEquals(0, manager.getAllEpicTasks().size());
    }

    @Test
    void getTaskIncorrectIdEpicTest() {
        assertNull(manager.getEpicTask(-1));
    }


    @Test
    void updateEpicTaskTest() {
        Epic epicUpdate = manager.createEpicTask("epic", "description epic");
        epicUpdate = manager.updateEpic(new Epic
                (epicUpdate.getId(), "epic1", "description epic1"));
        assertEquals(epicUpdate.getName(), manager.getEpicTask(epicUpdate.getId()).getName());
        assertEquals(epicUpdate.getDescription(), manager.getEpicTask(epicUpdate.getId()).getDescription());
        assertEquals(epicUpdate.getStatus(), manager.getEpicTask(epicUpdate.getId()).getStatus());
    }

    @Test
    void calculateStatusNewTest() {
        Epic epicStatus = manager.createEpicTask("epic", "description epic");
        manager.createSubTask
                (epicStatus, "subTask", "description subTask");
        manager.createSubTask
                (epicStatus, "subTask", "description subTask");
        assertEquals(Status.NEW, manager.getEpicStatus(epicStatus));
    }

    @Test
    void calculateStatusNewAndDoneTest() {
        Epic epicStatus = manager.createEpicTask("epic", "description epic");
        SubTask subTaskStatus = manager.createSubTask
                (epicStatus, "subTask", "description subTask");
        SubTask subTaskStatus1 = manager.createSubTask
                (epicStatus, "subTask", "description subTask");
        manager.updateSubTask
                (epicStatus, new SubTask
                        (subTaskStatus.getId(), "subTask1", "description subTask1", Status.NEW));
        manager.updateSubTask
                (epicStatus, new SubTask
                        (subTaskStatus1.getId(), "subTask1", "description subTask1", Status.DONE));
        assertEquals(Status.IN_PROGRESS, manager.getEpicStatus(epicStatus));
    }

    @Test
    void calculateStatusDoneTest() {
        Epic epicStatusDone = manager.createEpicTask("epic", "description epic");
        SubTask subTaskStatus = manager.createSubTask
                (epicStatusDone, "subTask", "description subTask");
        SubTask subTaskStatus1 = manager.createSubTask
                (epicStatusDone, "subTask", "description subTask");
        manager.updateSubTask
                (epicStatusDone, new SubTask
                        (subTaskStatus.getId(), "subTask1", "description subTask1", Status.DONE));
        manager.updateSubTask
                (epicStatusDone, new SubTask
                        (subTaskStatus1.getId(), "subTask1", "description subTask1", Status.DONE));
        assertEquals(Status.DONE, manager.getEpicStatus(epicStatusDone));
    }

    @Test
    void calculateStatusInProgressTest() {
        Epic epicStatus = manager.createEpicTask("epic", "description epic");
        SubTask subTaskStatus = manager.createSubTask
                (epicStatus, "subTask", "description subTask");
        SubTask subTaskStatus1 = manager.createSubTask
                (epicStatus, "subTask", "description subTask");
        manager.updateSubTask
                (epicStatus, new SubTask
                        (subTaskStatus.getId(), "subTask1", "description subTask1", Status.IN_PROGRESS));
        manager.updateSubTask
                (epicStatus, new SubTask
                        (subTaskStatus1.getId(), "subTask1", "description subTask1", Status.IN_PROGRESS));
        assertEquals(Status.IN_PROGRESS, manager.getEpicStatus(epicStatus));
    }

    @Test
    void createSubTaskTest() {
        Epic epic = manager.createEpicTask("epic", "description epic");
        SubTask subTask = manager.createSubTask(epic, "subTask", "description");
        assertEquals(subTask, manager.getSubTask(epic.getId(), subTask.getId()));
    }

    @Test
    void getSubTaskTest() {
        Epic epic = manager.createEpicTask("epic", "description epic");
        SubTask subTask = manager.createSubTask(epic, "subTask", "description");
        assertEquals(subTask, manager.getSubTask(epic.getId(), subTask.getId()));
    }

    @Test
    void deleteSubTaskTest() {
        Epic epic = manager.createEpicTask("epic", "description epic");
        SubTask subTask = manager.createSubTask(epic, "subTask", "description");
        int id = subTask.getId();
        manager.deleteSubTask(epic.getId(), id);
        assertNull(manager.getSubTask(epic.getId(), id));
    }

    @Test
    void deleteAllSubTaskTest() {
        Epic epic = manager.createEpicTask("epic", "description epic");
        manager.createSubTask(epic, "subTask", "description");
        manager.createSubTask(epic, "epic1", "description epic1");
        manager.createSubTask(epic, "epic2", "description epic2");
        assertEquals(3, manager.getAllSubTask(epic.getId()).size());
        manager.deleteAllSubTasks(epic.getId());
        assertEquals(0, epic.getSubTasks().size());
    }

    @Test
    void getTaskIncorrectIdSubTaskTest() {
        NullPointerException exception = assertThrows(
                NullPointerException.class, new Executable() {
                    @Override
                    public void execute() {
                        manager.getSubTask(1, 1);
                    }
                });
        assertEquals(NullPointerException.class, exception.getClass());
    }

    @Test
    void updateSubTask() {
        Epic epic = manager.createEpicTask("epic", "description epic");
        SubTask subTaskUpdate = manager.createSubTask(epic, "subTask", "description subTask");
        subTaskUpdate = manager.updateSubTask(epic, new SubTask
                (subTaskUpdate.getId(), "subTask1", "description subTask1", Status.IN_PROGRESS));
        assertEquals(subTaskUpdate.getStatus(),
                manager.getSubTask(epic.getId(), subTaskUpdate.getId()).getStatus());
        assertEquals(subTaskUpdate.getName(),
                manager.getSubTask(epic.getId(), subTaskUpdate.getId()).getName());
        assertEquals(subTaskUpdate.getDescription(),
                manager.getSubTask(epic.getId(), subTaskUpdate.getId()).getDescription());
    }

    @AfterEach
    void clear() {
        manager.deleteAllTasks();
        manager.deleteAllEpicTasks();
    }
}
