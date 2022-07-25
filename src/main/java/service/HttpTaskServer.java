package main.java.service;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import main.java.model.Epic;
import main.java.model.Status;
import main.java.model.SubTask;
import main.java.model.Task;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson = new Gson();
    private static TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);

        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

    }

    public static class TaskHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            InputStream inputStream = httpExchange.getRequestBody();
            String path = httpExchange.getRequestURI().getPath();
            String[] pathSplit = path.split("/");
            int pathLength = pathSplit.length;
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            String method = httpExchange.getRequestMethod();
            switch (method) {
                case "GET":
                    if (pathLength == 4 && pathSplit[2].equals("task")) {
                        String id = pathSplit[3];
                        int idPars = Integer.parseInt(id);
                        String task = gson.toJson(taskManager.getTask(idPars));
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(task.getBytes());
                            break;
                        }
                    }
                    if (pathLength == 4 && pathSplit[2].equals("epic")) {
                        String id = pathSplit[3];
                        int idPars = Integer.parseInt(id);
                        Epic epic = taskManager.getEpicTask(idPars);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("id", epic.getId());
                        jsonObject.addProperty("name", epic.getName());
                        jsonObject.addProperty("description", epic.getDescription());
                        jsonObject.addProperty("status", String.valueOf(epic.getStatus()));
                        jsonObject.addProperty("durationMinutes", epic.getDuration());
                        jsonObject.addProperty("startTime", String.valueOf(epic.getEndTime()));
                        httpExchange.sendResponseHeaders(200, 0);
                        String epics = gson.toJson(taskManager.getEpicTask(idPars));
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(epics.getBytes());
                            break;
                        }
                    }
                    if (pathLength == 5 && pathSplit[2].equals("subTask")) {
                        String epicId = pathSplit[3];
                        String subTaskId = pathSplit[4];
                        int epicIdPars = Integer.parseInt(epicId);
                        int subTaskIdPars = Integer.parseInt(subTaskId);
                        String task = gson.toJson(taskManager.getSubTask(epicIdPars, subTaskIdPars));
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(task.getBytes());
                            break;
                        }
                    }
                    if (pathLength == 3 && pathSplit[2].equals("task")) {
                        String allTask = gson.toJson(taskManager.getAllTasks());
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(allTask.getBytes());
                            break;
                        }
                    }
                    if (pathLength == 3 && pathSplit[2].equals("epic")) {
                        String allTask = gson.toJson(taskManager.getAllEpicTasks());
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(allTask.getBytes());
                            break;
                        }
                    }
                    if (pathLength == 4 && pathSplit[2].equals("subTask")) {
                        String epicId = pathSplit[3];
                        int epicIdPars = Integer.parseInt(epicId);
                        String allTask = gson.toJson(taskManager.getAllSubTask(epicIdPars));
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(allTask.getBytes());
                            break;
                        }
                    }
                    if (pathLength == 3 && pathSplit[2].equals("history")) {
                        String history = gson.toJson(taskManager.history());
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(history.getBytes());
                            break;
                        }
                    }
                    httpExchange.sendResponseHeaders(404, 0);
                    break;
                case "POST":
                    if (pathLength == 3 && pathSplit[2].equals("task")) {
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (!jsonElement.isJsonObject()) {
                            System.out.println("Ответ от сервера не соответствует ожидаемому.");
                            return;
                        }
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        taskManager.createTask(jsonObject.get("name").getAsString(),
                                jsonObject.get("description").getAsString());
                        httpExchange.sendResponseHeaders(201, 0);
                        break;
                    }
                    if (pathLength == 3 && pathSplit[2].equals("epic")) {
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (!jsonElement.isJsonObject()) {
                            System.out.println("Ответ от сервера не соответствует ожидаемому.");
                            return;
                        }
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        taskManager.createEpicTask(jsonObject.get("name").getAsString(),
                                jsonObject.get("description").getAsString());
                        httpExchange.sendResponseHeaders(201, 0);
                        break;
                    }
                    if (pathLength == 4 && pathSplit[2].equals("sub-task")) {
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (!jsonElement.isJsonObject()) {
                            System.out.println("Ответ от сервера не соответствует ожидаемому.");
                            return;
                        }
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        String epicId = pathSplit[3];
                        int epicIdPars = Integer.parseInt(epicId);
                        Epic epic = taskManager.getEpicTask(epicIdPars);
                        taskManager.createSubTask(epic, jsonObject.get("name").getAsString(),
                                jsonObject.get("description").getAsString());
                        httpExchange.sendResponseHeaders(201, 0);
                        break;
                    }
                    if (pathLength == 4 && pathSplit[2].equals("task")) {
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (!jsonElement.isJsonObject()) {
                            System.out.println("Ответ от сервера не соответствует ожидаемому.");
                            return;
                        }
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        String id = pathSplit[3];
                        int idPars = Integer.parseInt(id);
                        Status status = Status.valueOf(jsonObject.get("Status").getAsString());
                        LocalDateTime localDateTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
                        Task task = new Task(idPars, jsonObject.get("name").getAsString(),
                                jsonObject.get("description").getAsString(), status,
                                localDateTime, jsonObject.get("durationMinutes").getAsInt());
                        taskManager.updateTask(task);
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    }
                    if (pathLength == 4 && pathSplit[2].equals("epic")) {
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (!jsonElement.isJsonObject()) {
                            System.out.println("Ответ от сервера не соответствует ожидаемому.");
                            return;
                        }
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        String id = pathSplit[3];
                        int idPars = Integer.parseInt(id);
                        Epic epic = new Epic(idPars, jsonObject.get("name").getAsString(),
                                jsonObject.get("description").getAsString());
                        taskManager.updateEpic(epic);
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    }
                    if (pathLength == 5 && pathSplit[2].equals("subTask")) {
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (!jsonElement.isJsonObject()) {
                            System.out.println("Ответ от сервера не соответствует ожидаемому.");
                            return;
                        }
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        String epicId = pathSplit[3];
                        String subTaskId = pathSplit[4];
                        int epicIdPars = Integer.parseInt(epicId);
                        int subTaskIdPars = Integer.parseInt(subTaskId);
                        Status status = Status.valueOf(jsonObject.get("Status").getAsString());
                        LocalDateTime localDateTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
                        Epic epic = taskManager.getEpicTask(epicIdPars);
                        SubTask subTask = new SubTask(subTaskIdPars, jsonObject.get("name").getAsString(),
                                jsonObject.get("description").getAsString(), status,
                                localDateTime, jsonObject.get("durationMinutes").getAsInt());
                        taskManager.updateSubTask(epic, subTask);
                    }
                case "DELETE":
                    if (pathLength == 4 && pathSplit[2].equals("task")) {
                        String id = pathSplit[3];
                        int idPars = Integer.parseInt(id);
                        taskManager.deleteTask(idPars);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    if (pathLength == 4 && pathSplit[2].equals("epic")) {
                        String id = pathSplit[3];
                        int idPars = Integer.parseInt(id);
                        taskManager.deleteEpicTask(idPars);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    if (pathLength == 5 && pathSplit[2].equals("subTask")) {
                        String epicId = pathSplit[3];
                        String subTaskId = pathSplit[4];
                        int epicIdPars = Integer.parseInt(epicId);
                        int subTaskIdPars = Integer.parseInt(subTaskId);
                        taskManager.deleteSubTask(epicIdPars, subTaskIdPars);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    if (pathLength == 3 && pathSplit[2].equals("task")) {
                        taskManager.deleteAllTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    if (pathLength == 3 && pathSplit[2].equals("epic")) {
                        taskManager.deleteAllEpicTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    if (pathLength == 4 && pathSplit[2].equals("subTask")) {
                        String epicId = pathSplit[3];
                        int idPars = Integer.parseInt(epicId);
                        taskManager.deleteAllSubTasks(idPars);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
            }
            httpExchange.close();
        }
    }
}
