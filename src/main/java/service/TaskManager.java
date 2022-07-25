package main.java.service;

import main.java.model.*;

import java.util.List;
import java.util.Map;

public interface TaskManager{

    Map<Integer, Task> getAllTasks();

    Task createTask(String name, String description);

    Task getTask(int id);

    void deleteTask(int id);

    void deleteAllTasks();

    Task updateTask(Task task);

    Map<Integer, Epic> getAllEpicTasks();

    Epic createEpicTask(String name, String description);

    Epic getEpicTask(int id);

    void deleteEpicTask(int id);

    void deleteAllEpicTasks();

    Epic updateEpic(Epic epic);

    SubTask createSubTask(Epic epic, String name, String description);

    Map<Integer, SubTask> getAllSubTask(int epicId);

    SubTask getSubTask(int epicId, int subTaskId);

    SubTask updateSubTask(Epic epic, SubTask subTask);

    void deleteAllSubTasks(int id);

    void deleteSubTask(int epicId, int subTaskId);

    List<Task> history();

    Status getEpicStatus(Epic epicStatus);

    void setMaxId(int id);

}
