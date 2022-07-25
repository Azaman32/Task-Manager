package test.java.service;

import main.java.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends AbstractTaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void init() {
        manager = new InMemoryTaskManager();
    }
}