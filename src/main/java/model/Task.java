package main.java.model;

import java.time.LocalDateTime;

public class Task {
    private final int id;
    private final String name;
    private final String description;
    private final Status status;
    private int durationMinutes;
    private LocalDateTime startTime;

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description, Status status, LocalDateTime startTime,int durationMinutes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public long getDuration() {
        return durationMinutes;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(durationMinutes);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", durationMinutes=" + durationMinutes +
                ", startTime=" + startTime +
                '}';
    }
}

