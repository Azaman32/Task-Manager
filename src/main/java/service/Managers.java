package main.java.service;

public class Managers {

    public static TaskManager getDefault(){
        return new HTTPTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return InMemoryHistoryManager.getInstance();
    }
}
