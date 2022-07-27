package main.java.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }
    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getStartTime() {
        LocalDateTime startTime = null;
        for (Map.Entry<Integer, SubTask> subTask : getSubTasks().entrySet()){
            if(startTime == null || startTime.isAfter(subTask.getValue().getStartTime())) {
                startTime = subTask.getValue().getStartTime();
            }
        }
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime endTime = null;
        for (Map.Entry<Integer, SubTask> subTask : getSubTasks().entrySet()){
            if(endTime == null || endTime.isBefore(subTask.getValue().getStartTime())) {
                endTime = subTask.getValue().getStartTime();
            }
        }
        return endTime;
    }

    @Override
    public long getDuration() {
        long duration = 0;
        for (Map.Entry<Integer, SubTask> subTask : getSubTasks().entrySet()){
            duration = subTask.getValue().getDuration() + duration;
        }
        return duration;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", id=" + getId() +
                ", taskName='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}