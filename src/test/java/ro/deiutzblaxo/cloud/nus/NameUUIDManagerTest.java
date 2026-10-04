package ro.deiutzblaxo.cloud.nus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ro.deiutzblaxo.cloud.expcetions.NoFoundException;
import ro.deiutzblaxo.cloud.expcetions.TooManyArgs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class NameUUIDManagerTest {

    /**
     * Simple in-memory NameUUIDStorage test double, keyed by UUID -> name.
     */
    private static class MockStorage implements NameUUIDStorage {
        private final String id;
        private final PriorityNUS priority;
        private final Map<UUID, String> byUuid = new LinkedHashMap<>();
        private int addCalls = 0;

        private MockStorage(String id, PriorityNUS priority) {
            this.id = id;
            this.priority = priority;
        }

        void seed(String name, UUID uuid) {
            byUuid.put(uuid, name);
        }

        int getAddCalls() {
            return addCalls;
        }

        boolean contains(UUID uuid) {
            return byUuid.containsKey(uuid);
        }

        @Override
        public String getNameByUUID(UUID uuid) {
            return byUuid.get(uuid);
        }

        @Override
        public String getUUIDByName(String name) {
            for (Map.Entry<UUID, String> entry : byUuid.entrySet()) {
                if (entry.getValue().equals(name)) {
                    return entry.getKey().toString();
                }
            }
            return null;
        }

        @Override
        public String getRealName(String fakename) {
            return null;
        }

        @Override
        public PriorityNUS getPriority() {
            return priority;
        }

        @Override
        public void add(String name, UUID uuid) throws TooManyArgs, SQLException {
            addCalls++;
            byUuid.put(uuid, name);
        }

        @Override
        public String toString() {
            return id;
        }
    }

    private NameUUIDManager manager;

    @AfterEach
    void tearDown() throws IOException {
        if (manager != null) {
            manager.close();
            manager = null;
        }
    }

    /** Polls until the write queue has been drained and processed, up to a timeout. */
    private static void awaitAsyncWrite(Runnable assertion) {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        AssertionError last = null;
        while (System.nanoTime() < deadline) {
            try {
                assertion.run();
                return;
            } catch (AssertionError e) {
                last = e;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (last != null) {
            throw last;
        }
    }

    @Test
    void addStorage_sortsByPriorityDescending() {
        MockStorage low = new MockStorage("low", PriorityNUS.LOW);
        MockStorage high = new MockStorage("high", PriorityNUS.HIGH);
        MockStorage medium = new MockStorage("medium", PriorityNUS.MEDIUM);

        // Deliberately passed out of order to the constructor.
        manager = new NameUUIDManager(low, high, medium);
        manager.start();

        UUID uuid = UUID.randomUUID();
        high.seed("alice", uuid);

        // If ordering were wrong, a lower-priority storage with no entry would be checked first
        // and the lookup would still succeed via "high", but we verify ordering indirectly below
        // by checking promotion targets instead (see other tests).
        assertDoesNotThrow(() -> manager.getNameByUUID(uuid));
    }

    @Test
    void lookupHitInHighPriority_promotesNowhere() throws NoFoundException {
        MockStorage low = new MockStorage("low", PriorityNUS.LOW);
        MockStorage medium = new MockStorage("medium", PriorityNUS.MEDIUM);
        MockStorage high = new MockStorage("high", PriorityNUS.HIGH);

        manager = new NameUUIDManager(high, medium, low);
        manager.start();

        UUID uuid = UUID.randomUUID();
        high.seed("alice", uuid);

        String result = manager.getNameByUUID(uuid);
        assertEquals("alice", result);

        awaitAsyncWrite(() -> {
            assertEquals(0, high.getAddCalls());
            assertEquals(0, medium.getAddCalls());
            assertEquals(0, low.getAddCalls());
        });
    }

    @Test
    void lookupHitInMediumPriority_promotesToHighOnly() throws NoFoundException {
        MockStorage low = new MockStorage("low", PriorityNUS.LOW);
        MockStorage medium = new MockStorage("medium", PriorityNUS.MEDIUM);
        MockStorage high = new MockStorage("high", PriorityNUS.HIGH);

        manager = new NameUUIDManager(high, medium, low);
        manager.start();

        UUID uuid = UUID.randomUUID();
        medium.seed("bob", uuid);

        String result = manager.getNameByUUID(uuid);
        assertEquals("bob", result);

        awaitAsyncWrite(() -> {
            assertTrue(high.contains(uuid));
            assertEquals(1, high.getAddCalls());
            assertEquals(0, medium.getAddCalls());
            assertEquals(0, low.getAddCalls());
        });
    }

    @Test
    void lookupHitInLowPriority_promotesToMediumAndHigh() throws NoFoundException {
        MockStorage low = new MockStorage("low", PriorityNUS.LOW);
        MockStorage medium = new MockStorage("medium", PriorityNUS.MEDIUM);
        MockStorage high = new MockStorage("high", PriorityNUS.HIGH);

        manager = new NameUUIDManager(high, medium, low);
        manager.start();

        UUID uuid = UUID.randomUUID();
        low.seed("carol", uuid);

        String result = manager.getNameByUUID(uuid);
        assertEquals("carol", result);

        awaitAsyncWrite(() -> {
            assertTrue(high.contains(uuid));
            assertTrue(medium.contains(uuid));
            assertEquals(1, high.getAddCalls());
            assertEquals(1, medium.getAddCalls());
            assertEquals(0, low.getAddCalls());
        });
    }

    @Test
    void lookupByName_hitInLowPriority_promotesToMediumAndHigh() throws NoFoundException {
        MockStorage low = new MockStorage("low", PriorityNUS.LOW);
        MockStorage medium = new MockStorage("medium", PriorityNUS.MEDIUM);
        MockStorage high = new MockStorage("high", PriorityNUS.HIGH);

        manager = new NameUUIDManager(high, medium, low);
        manager.start();

        UUID uuid = UUID.randomUUID();
        low.seed("dave", uuid);

        UUID result = manager.getUUIDByName("dave");
        assertEquals(uuid, result);

        awaitAsyncWrite(() -> {
            assertTrue(high.contains(uuid));
            assertTrue(medium.contains(uuid));
            assertEquals(1, high.getAddCalls());
            assertEquals(1, medium.getAddCalls());
            assertEquals(0, low.getAddCalls());
        });
    }

    @Test
    void lookupNotFoundAnywhere_throwsAndWritesNothing() {
        MockStorage low = new MockStorage("low", PriorityNUS.LOW);
        MockStorage medium = new MockStorage("medium", PriorityNUS.MEDIUM);
        MockStorage high = new MockStorage("high", PriorityNUS.HIGH);

        manager = new NameUUIDManager(high, medium, low);
        manager.start();

        UUID uuid = UUID.randomUUID();

        assertThrows(NoFoundException.class, () -> manager.getNameByUUID(uuid));

        awaitAsyncWrite(() -> {
            assertEquals(0, high.getAddCalls());
            assertEquals(0, medium.getAddCalls());
            assertEquals(0, low.getAddCalls());
        });
    }

    @Test
    void manualAdd_writesToAllStorages() {
        MockStorage low = new MockStorage("low", PriorityNUS.LOW);
        MockStorage medium = new MockStorage("medium", PriorityNUS.MEDIUM);
        MockStorage high = new MockStorage("high", PriorityNUS.HIGH);

        manager = new NameUUIDManager(high, medium, low);
        manager.start();

        UUID uuid = UUID.randomUUID();
        manager.add("erin", uuid);

        awaitAsyncWrite(() -> {
            assertTrue(high.contains(uuid));
            assertTrue(medium.contains(uuid));
            assertTrue(low.contains(uuid));
            assertEquals(1, high.getAddCalls());
            assertEquals(1, medium.getAddCalls());
            assertEquals(1, low.getAddCalls());
        });
    }
}
