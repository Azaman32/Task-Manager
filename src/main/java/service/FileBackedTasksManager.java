package main.java.service;

import main.java.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;


public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;
    private static final FileBackedTasksManager INSTANCE = new FileBackedTasksManager();

    public static FileBackedTasksManager loadFromFile(File file) {
        INSTANCE.file = file;
        boolean isHistory = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (isHistory) {
                    INSTANCE.historyManager.fromStringInHistory(line, INSTANCE);
                } else {
                    INSTANCE.fromString(line);
                }
                if (line.equals("")) {
                    isHistory = true;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        return INSTANCE;
    }

    @Override
    public Task createTask(String name, String description) {
        Task task = super.createTask(name, description);
        save();
        return task;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task updateTask(Task task) {
        Task updateTask = super.updateTask(task);
        save();
        return updateTask;
    }

    @Override
    public Epic createEpicTask(String name, String description) {
        Epic epic = super.createEpicTask(name, description);
        save();
        return epic;
    }

    @Override
    public void deleteEpicTask(int id) {
        super.deleteEpicTask(id);
        save();
    }

    @Override
    public void deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
        save();
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updateEpic = super.updateEpic(epic);
        save();
        return updateEpic;
    }

    @Override
    public Epic getEpicTask(int id) {
        Epic epic = super.getEpicTask(id);
        save();
        return epic;
    }

    @Override
    public SubTask createSubTask(Epic epic, String name, String description) {
        SubTask subTask = super.createSubTask(epic, name, description);
        save();
        return subTask;
    }

    @Override
    public void deleteSubTask(int epicId, int subTaskId) {
        super.deleteSubTask(epicId, subTaskId);
        save();
    }

    @Override
    public void deleteAllSubTasks(int id) {
        super.deleteAllSubTasks(id);
        save();
    }

    @Override
    public SubTask updateSubTask(Epic epic, SubTask subTask) {
        SubTask updateSubTask = super.updateSubTask(epic, subTask);
        save();
        return updateSubTask;
    }

    @Override
    public SubTask getSubTask(int epicId, int subTaskId) {
        SubTask subTask = super.getSubTask(epicId, subTaskId);
        save();
        return subTask;
    }

    public int getCurrentId() {
        return super.identifier.getId();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toString(), StandardCharsets.UTF_8))) {
            for (Map.Entry<Integer, Task> task : tasks.entrySet()) {
                writer.write(toLine(task.getValue()) + "\n");
            }
            for (Map.Entry<Integer, Epic> task : epicTasks.entrySet()) {
                writer.write(toLineEpic(task.getValue()) + "\n");
            }
            for (Map.Entry<Integer, Epic> task : epicTasks.entrySet()) {
                Epic epic = task.getValue();
                for (Map.Entry<Integer, SubTask> subTask : this.getAllSubTask(epic.getId()).entrySet()) {
                    writer.write(toLine(subTask.getValue(), epic) + "\n");
                }
            }
            writer.write("\n" + historyManager.historyInString());
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public String toLine(Task task) {
        return task.getId() + ","
                + task.getType() + ","
                + task.getName() + ","
                + task.getStatus() + ","
                + task.getDescription() + ","
                + task.getStartTime() + ","
                + task.getDuration();
    }

    public String toLineEpic(Task task) {
        return task.getId() + ","
                + task.getType() + ","
                + task.getName() + ","
                + task.getStatus() + ","
                + task.getDescription();
    }

    public String toLine(Task task, Epic epic) {
        return task.getId() + ","
                + task.getType() + ","
                + task.getName() + ","
                + task.getStatus() + ","
                + task.getDescription() + ","
                + epic.getId() + ","
                + task.getStartTime() + ","
                + task.getDuration();
    }

    public void fromString(String fileName) {
        String[] split = fileName.split(",");
        if (split.length > 6 || split.length < 5) {
            return;
        }
        if (split[1].equals(TaskType.TASK.toString())) {
            tasks.put(Integer.parseInt(split[0]),
                    new Task(Integer.parseInt(split[0]), split[2], split[4], Status.valueOf(split[3]),
                            LocalDateTime.parse(split[5]), Integer.parseInt(split[6])));
        } else if (split[1].equals(TaskType.EPIC.toString())) {
            epicTasks.put(Integer.parseInt(split[0]),
                    new Epic(Integer.parseInt(split[0]), split[2], split[4]));
        } else {
            Epic epic = epicTasks.get(Integer.parseInt(split[5]));
            epic.getSubTasks().put(Integer.parseInt(split[0]),
                    new SubTask(Integer.parseInt(split[0]), split[2], split[4], Status.valueOf(split[3]),
                            LocalDateTime.parse(split[5]), Integer.parseInt(split[6])));
        }
    }

    public static FileBackedTasksManager getInstance() {
        return INSTANCE;
    }
}
