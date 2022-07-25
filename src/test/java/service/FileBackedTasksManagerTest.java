package test.java.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.service.FileBackedTasksManager;
import main.java.service.ManagerSaveException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends AbstractTaskManagerTest<FileBackedTasksManager> {
    File file;

    @BeforeEach
    void init() {
        Path testFilePath = Paths.get("FileTestData.txt");
        file = new File(String.valueOf(testFilePath));
        manager = FileBackedTasksManager.loadFromFile(file);
    }

    @Test
    void saveTest() {
        manager.deleteAllTasks();
        manager.deleteAllEpicTasks();
        int id = manager.getCurrentId();
        manager.createTask("testSave", "description testSave");
        manager.createTask("1testSave", "description testSave1");
        manager.save();
        List<String> list1 = new ArrayList<>();
        list1.add(id + ",TASK,testSave,NEW,description testSave,null,0");
        list1.add((id + 1) + ",TASK,1testSave,NEW,description testSave1,null,0");
        List<String> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals("")) {
                    break;
                }
                list.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        assertEquals(list, list1);
    }
}
