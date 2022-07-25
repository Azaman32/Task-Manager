package main.java.service;

import main.java.model.*;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 10;
    private TaskNode head;
    private TaskNode lastNode;
    private static final InMemoryHistoryManager INSTANCE = new InMemoryHistoryManager();

    private InMemoryHistoryManager() {
    }

    public final HashMap<Integer, TaskNode> history = new HashMap<>();

    @Override
    public void linkLast(Task task) {
        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
            history.remove(task.getId());
        }
        TaskNode last = lastNode;
        TaskNode newNode = new TaskNode(lastNode, task, null);
        lastNode = newNode;

        if (last == null) {
            head = newNode;
            history.put(task.getId(), head);
        } else {
            last.setNext(newNode);
            history.put(task.getId(), newNode);
        }

    }

    private void removeNode(TaskNode node) {
        TaskNode next = node.getNext();
        TaskNode prev = node.getPrev();
        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
        }
        if (next == null) {
            lastNode = prev;
        } else {
            next.setPrev(prev);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> nodeList = new ArrayList<>();
        TaskNode currentNode = lastNode;
        while (nodeList.size() < MAX_HISTORY_SIZE && currentNode != null) {
            nodeList.add(currentNode.getValue());
            currentNode = currentNode.getPrev();
        }
        return nodeList;
    }

    @Override
    public void removeHistoryTask(int taskId) {
        TaskNode toDelete = history.get(taskId);
        if (history.containsKey(taskId)) {
            removeNode(toDelete);
            history.remove(taskId);
        }
    }

    public void deleteAllHistory() {
        history.clear();
        head = null;
        lastNode = null;
    }

    public static InMemoryHistoryManager getInstance() {
        return INSTANCE;
    }

    public String historyInString() {
        StringBuilder builder = new StringBuilder();
        List<Task> historyList = getHistory();
        int count = historyList.size();
        for (int i = 0; i < count; i++) {
            if (historyList.size() == 1) {
                builder.append(historyList.get(i).getId());
            } else if (i == historyList.size() - 1) {
                builder.append(historyList.get(i).getId());
            } else {
                builder.append(historyList.get(i).getId()).append(",");
            }
        }
        return String.valueOf(builder);
    }

    public void fromStringInHistory(String stringHistory, TaskManager taskManager) {
        String[] split = stringHistory.split(",");
        for (String id : split) {
            int taskId = Integer.parseInt(id);
            if (taskManager.getAllTasks().containsKey(taskId)) {
                taskManager.getTask(taskId);
            } else if (taskManager.getAllEpicTasks().containsKey(taskId)) {
                taskManager.getEpicTask(taskId);
            } else {
                for (Epic epic : taskManager.getAllEpicTasks().values()) {
                    if (epic.getSubTasks().containsKey(taskId)) {
                        taskManager.getSubTask(epic.getId(), taskId);
                    }
                }
            }
        }
    }
}



