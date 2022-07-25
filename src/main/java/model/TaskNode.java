package main.java.model;

public class TaskNode {
    private Task value;
    private TaskNode next;
    private TaskNode prev;

    public TaskNode(TaskNode prev, Task value, TaskNode next) {
        this.prev = prev;
        this.value = value;
        this.next = next;
    }

    public TaskNode getNext() {
        return next;
    }

    public Task getValue() {
        return value;
    }

    public TaskNode getPrev() {
        return prev;
    }

    public void setNext(TaskNode next) {
        this.next = next;
    }

    public void setPrev(TaskNode prev) {
        this.prev = prev;
    }
}
