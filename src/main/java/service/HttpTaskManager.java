package main.java.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.java.model.Epic;
import main.java.model.SubTask;
import main.java.model.Task;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private KVTaskClient kvTaskClient;
    private Gson gson = new Gson();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    public HttpTaskManager(String url) {
        kvTaskClient = new KVTaskClient(url);
        load();
    }

    @Override
    public void save() {
        kvTaskClient.put("task", gson.toJson(tasks));
        kvTaskClient.put("epic", gson.toJson(epicTasks));
        kvTaskClient.put("history", gson.toJson(historyManager.getHistory()));

    }

    public void load() {
        String task = kvTaskClient.load("task");
        int maxId = 0;
        if (!task.equals("")) {
            Type typeTask = new TypeToken<Map<Integer, Task>>() {
            }.getType();
            Map<Integer, Task> readTask = gson.fromJson(task, typeTask);
            tasks.putAll(readTask);
            for (Task taskId : tasks.values()) {
                if (taskId.getId() > maxId) {
                    maxId = taskId.getId();
                }
            }
        }
        String epic = kvTaskClient.load("epic");
        if (!epic.equals("")) {
            Type typeEpic = new TypeToken<Map<Integer, Epic>>() {
            }.getType();
            Map<Integer, Epic> readEpic = gson.fromJson(epic, typeEpic);
            epicTasks.putAll(readEpic);
            for (Epic epicId : epicTasks.values()) {
                if (epicId.getId() > maxId) {
                    maxId = epicId.getId();
                }
                if (!epicId.getSubTasks().isEmpty()) {
                    for (SubTask subTaskId : epicId.getSubTasks().values()) {
                        if (subTaskId.getId() > maxId) {
                            maxId = epicId.getId();
                        }
                    }
                }

            }

        }
        String history = kvTaskClient.load("history");
        if (!history.equals("")) {
            Type typeHistory = new TypeToken<List<Task>>() {
            }.getType();
            List<Task> readHistory = gson.fromJson(history, typeHistory);
            for (Task tasks : readHistory) {
                historyManager.linkLast(tasks);
            }
        }
        setMaxId(maxId + 1);

    }
}
