package main.java.service;

import main.java.model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epicTasks = new HashMap<>();
    protected final Identifier identifier = new Identifier();
    private static final InMemoryTaskManager INSTANCE = new InMemoryTaskManager();
    private final TreeSet<Task> treeSet;

    public InMemoryTaskManager() {
        Comparator<Task> comparator = (o1, o2) -> {
            if (o1.getStartTime() == null) {
                return 1;
            }
            if (o2.getStartTime() == null) {
                return -1;
            }
            return o1.getStartTime().compareTo(o2.getStartTime());
        };
        treeSet = new TreeSet<>(comparator);
    }

    @Override
    public Map<Integer, Task> getAllTasks() {
        return tasks;
    }

    @Override
    public Task createTask(String name, String description) {
        Task task = new Task(identifier.generate(), name, description);
        tasks.put(task.getId(), task);
        treeSet.add(task);
        return task;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.linkLast(task);
        }
        return task;
    }

    @Override
    public void deleteTask(int id) {
        historyManager.removeHistoryTask(id);
        tasks.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.removeHistoryTask(task.getId());
        }
        tasks.clear();
    }

    @Override
    public Task updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Map<Integer, Epic> getAllEpicTasks() {
        return epicTasks;
    }

    @Override
    public Epic createEpicTask(String name, String description) {
        Epic epic = new Epic(identifier.generate(), name, description);
        epicTasks.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic getEpicTask(int id) {
        Epic epic = epicTasks.get(id);
        if (epic != null) {
            historyManager.linkLast(epic);
        }
        return epic;
    }

    @Override
    public void deleteEpicTask(int id) {
        historyManager.removeHistoryTask(id);
        epicTasks.remove(id);
    }

    @Override
    public void deleteAllEpicTasks() {
        for (Epic epic : epicTasks.values()) {
            historyManager.removeHistoryTask(epic.getId());
            for (SubTask subTask : epic.getSubTasks().values()) {
                historyManager.removeHistoryTask(subTask.getId());
            }
            epic.getSubTasks().clear();
        }
        epicTasks.clear();
    }

    @Override
    public Epic updateEpic(Epic epic) {
        epicTasks.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(Epic epic, String name, String description) {
        SubTask subTask = new SubTask(identifier.generate(), name, description, Status.NEW);
        epic.getSubTasks().put(subTask.getId(), subTask);
        return subTask;
    }

    @Override
    public Map<Integer, SubTask> getAllSubTask(int epicId) {
        return epicTasks.get(epicId).getSubTasks();
    }

    @Override
    public SubTask getSubTask(int epicId, int subTaskId) {
        SubTask subTask = epicTasks.get(epicId).getSubTasks().get(subTaskId);
        if (subTask != null) {
            historyManager.linkLast(subTask);
        }
        return subTask;
    }

    @Override
    public SubTask updateSubTask(Epic epic, SubTask subTask) {
        epic.getSubTasks().put(subTask.getId(), subTask);
        return subTask;
    }

    @Override
    public void deleteAllSubTasks(int id) {
        epicTasks.get(id).getSubTasks().clear();
    }

    @Override
    public void deleteSubTask(int epicId, int subTaskId) {
        historyManager.removeHistoryTask(subTaskId);
        epicTasks.get(epicId).getSubTasks().remove(subTaskId);
    }

    private Status calculateStatus(Epic epic) {
        int countNew = 0;
        for (Map.Entry<Integer, SubTask> subTask : epic.getSubTasks().entrySet()) {
            if (subTask.getValue().getStatus().equals(Status.IN_PROGRESS)) {
                return Status.IN_PROGRESS;
            } else if (subTask.getValue().getStatus().equals(Status.NEW)) {
                countNew++;
            }
        }
        if (epic.getSubTasks().size() == countNew) {
            return Status.NEW;
        }
        if (countNew > 0) {
            return Status.IN_PROGRESS;
        }
        return Status.DONE;
    }

    public Status getEpicStatus(Epic epic) {
        return calculateStatus(epic);
    }

    public List<Task> history() {
        return historyManager.getHistory();
    }

    public static InMemoryTaskManager getInstance() {
        return INSTANCE;
    }

    public TreeSet getPrioritizedTasks() {
        return treeSet;
    }

    public void setMaxId(int id) {
        identifier.setId(id);
    }
}

