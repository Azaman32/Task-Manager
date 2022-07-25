package main.java.service;

import main.java.model.*;

import java.util.HashMap;
import java.util.List;

public interface HistoryManager {

    void linkLast(Task task);

    List<Task> getHistory();

    void removeHistoryTask (int taskId);

    public String historyInString();

    public void fromStringInHistory(String stringHistory,TaskManager taskManager);

}
