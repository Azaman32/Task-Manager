package main.java.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.model.Epic;
import main.java.model.Status;
import main.java.model.SubTask;
import main.java.model.Task;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class TaskHandler implements HttpHandler {
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson = new Gson();
    private final TaskManager taskManager = Managers.getDefault();
    private final int NAME_TASK = 2;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String path = httpExchange.getRequestURI().getPath();
        String[] pathSplit = path.split("/");
        int pathLength = pathSplit.length;
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        String method = httpExchange.getRequestMethod();
        JsonElement jsonElement = JsonParser.parseString(body);
        switch (method) {
            case "GET":
                processingGet(httpExchange, pathSplit, pathLength);
                break;
            case "POST":
                processingPost(httpExchange, pathSplit, pathLength, jsonElement);
                break;
            case "DELETE":
                processingDelete(httpExchange, pathSplit, pathLength);
                break;
        }
        httpExchange.close();
    }

    public void processingGet(HttpExchange httpExchange, String[] pathSplit, int pathLength) throws IOException {
        if (pathLength == 4 && pathSplit[NAME_TASK].equals("task")) {
            String id = pathSplit[3];
            int idPars = Integer.parseInt(id);
            String task = gson.toJson(taskManager.getTask(idPars));
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(task.getBytes());
            }
            return;
        }
        if (pathLength == 4 && pathSplit[NAME_TASK].equals("epic")) {
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
            }
            return;
        }
        if (pathLength == 5 && pathSplit[NAME_TASK].equals("subTask")) {
            String epicId = pathSplit[3];
            String subTaskId = pathSplit[4];
            int epicIdPars = Integer.parseInt(epicId);
            int subTaskIdPars = Integer.parseInt(subTaskId);
            String task = gson.toJson(taskManager.getSubTask(epicIdPars, subTaskIdPars));
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(task.getBytes());
            }
            return;
        }
        if (pathLength == 3 && pathSplit[NAME_TASK].equals("task")) {
            String allTask = gson.toJson(taskManager.getAllTasks());
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(allTask.getBytes());
            }
            return;
        }
        if (pathLength == 3 && pathSplit[NAME_TASK].equals("epic")) {
            String allTask = gson.toJson(taskManager.getAllEpicTasks());
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(allTask.getBytes());
            }
            return;
        }
        if (pathLength == 4 && pathSplit[NAME_TASK].equals("subTask")) {
            String epicId = pathSplit[3];
            int epicIdPars = Integer.parseInt(epicId);
            String allTask = gson.toJson(taskManager.getAllSubTask(epicIdPars));
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(allTask.getBytes());
            }
            return;
        }
        if (pathLength == 3 && pathSplit[NAME_TASK].equals("history")) {
            String history = gson.toJson(taskManager.history());
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(history.getBytes());
            }
            return;
        }
        httpExchange.sendResponseHeaders(404, 0);
    }

    public void processingPost(HttpExchange httpExchange, String[] pathSplit, int pathLength, JsonElement jsonElement)
            throws IOException {
        if (!jsonElement.isJsonObject()) {
            System.out.println("Ответ от сервера не соответствует ожидаемому.");
            return;
        }
        if (pathLength == 3 && pathSplit[NAME_TASK].equals("task")) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            taskManager.createTask(jsonObject.get("name").getAsString(),
                    jsonObject.get("description").getAsString());
            httpExchange.sendResponseHeaders(201, 0);
            return;
        }
        if (pathLength == 3 && pathSplit[NAME_TASK].equals("epic")) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            taskManager.createEpicTask(jsonObject.get("name").getAsString(),
                    jsonObject.get("description").getAsString());
            httpExchange.sendResponseHeaders(201, 0);
            return;
        }
        if (pathLength == 4 && pathSplit[NAME_TASK].equals("sub-task")) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String epicId = pathSplit[3];
            int epicIdPars = Integer.parseInt(epicId);
            Epic epic = taskManager.getEpicTask(epicIdPars);
            taskManager.createSubTask(epic, jsonObject.get("name").getAsString(),
                    jsonObject.get("description").getAsString());
            httpExchange.sendResponseHeaders(201, 0);
            return;
        }
        if (pathLength == 4 && pathSplit[NAME_TASK].equals("task")) {
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
            return;
        }
        if (pathLength == 4 && pathSplit[NAME_TASK].equals("epic")) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String id = pathSplit[3];
            int idPars = Integer.parseInt(id);
            Epic epic = new Epic(idPars, jsonObject.get("name").getAsString(),
                    jsonObject.get("description").getAsString());
            taskManager.updateEpic(epic);
            httpExchange.sendResponseHeaders(200, 0);
            return;
        }
        if (pathLength == 5 && pathSplit[NAME_TASK].equals("subTask")) {
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
    }

    public void processingDelete(HttpExchange httpExchange, String[] pathSplit, int pathLength) throws IOException {
        if (pathLength == 4 && pathSplit[NAME_TASK].equals("task")) {
            String id = pathSplit[3];
            int idPars = Integer.parseInt(id);
            taskManager.deleteTask(idPars);
            httpExchange.sendResponseHeaders(200, 0);
        }
        if (pathLength == 4 && pathSplit[NAME_TASK].equals("epic")) {
            String id = pathSplit[3];
            int idPars = Integer.parseInt(id);
            taskManager.deleteEpicTask(idPars);
            httpExchange.sendResponseHeaders(200, 0);
        }
        if (pathLength == 5 && pathSplit[NAME_TASK].equals("subTask")) {
            String epicId = pathSplit[3];
            String subTaskId = pathSplit[4];
            int epicIdPars = Integer.parseInt(epicId);
            int subTaskIdPars = Integer.parseInt(subTaskId);
            taskManager.deleteSubTask(epicIdPars, subTaskIdPars);
            httpExchange.sendResponseHeaders(200, 0);
        }
        if (pathLength == 3 && pathSplit[NAME_TASK].equals("task")) {
            taskManager.deleteAllTasks();
            httpExchange.sendResponseHeaders(200, 0);
        }
        if (pathLength == 3 && pathSplit[NAME_TASK].equals("epic")) {
            taskManager.deleteAllEpicTasks();
            httpExchange.sendResponseHeaders(200, 0);
        }
        if (pathLength == 4 && pathSplit[NAME_TASK].equals("subTask")) {
            String epicId = pathSplit[3];
            int idPars = Integer.parseInt(epicId);
            taskManager.deleteAllSubTasks(idPars);
            httpExchange.sendResponseHeaders(200, 0);
        }
    }
}
