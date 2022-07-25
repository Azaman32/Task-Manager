package main.java.model;

import java.time.LocalDateTime;

public class SubTask extends Task {

    public SubTask(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public SubTask(int id, String name, String description, Status status, LocalDateTime startTime,int durationMinutes) {
        super(id, name, description, status,startTime,durationMinutes);
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}
