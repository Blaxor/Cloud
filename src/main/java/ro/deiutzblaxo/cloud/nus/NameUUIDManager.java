package ro.deiutzblaxo.cloud.nus;

import ro.deiutzblaxo.cloud.expcetions.NoFoundException;
import ro.deiutzblaxo.cloud.datastructure.OrderType;
import ro.deiutzblaxo.cloud.datastructure.QuickSortReflectByMethodReturn;
import ro.deiutzblaxo.cloud.utils.CloudLogger;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class NameUUIDManager implements Closeable {

    private static class WriteRequest {
        private final UUID uuid;
        private final String name;
        private final NameUUIDStorage foundIn;

        private WriteRequest(UUID uuid, String name, NameUUIDStorage foundIn) {
            this.uuid = uuid;
            this.name = name;
            this.foundIn = foundIn;
        }
    }

    private ArrayList<NameUUIDStorage> storages = new ArrayList<>();
    private LinkedBlockingQueue<WriteRequest> queue = new LinkedBlockingQueue<>();
    private boolean running = false;
    private final Thread thread =  new Thread(() -> {
        while (running) {
            try {
                    WriteRequest request = queue.take();
                    List<NameUUIDStorage> targets = request.foundIn == null
                            ? storages
                            : storages.subList(0, storages.indexOf(request.foundIn));
                    targets.forEach(nameUUIDStorage -> {
                        try {
                            nameUUIDStorage.add(request.name, request.uuid);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

            }catch (InterruptedException ignore) {
                //IGNORED
            }catch (RuntimeException e) {
                CloudLogger.getLogger().severe("Exception catch in queue for adding to the NameUUIDStorage, clearing the queue...");
                CloudLogger.getLogger().severe(e.getMessage());
                queue.clear();
            }
        }

    });

    public NameUUIDManager(NameUUIDStorage... storage) {
        storages.addAll(Arrays.asList(storage));
        try {
            QuickSortReflectByMethodReturn.sort(storages, 0, storages.size() - 1, "getPriority", OrderType.DESCENDING);
        } catch (NoSuchFieldException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        running = true;
        thread.start();
    }

    public String getNameByUUID(UUID uuid) throws NoFoundException {
        String value = null;
        NameUUIDStorage foundIn = null;
        for (NameUUIDStorage storage : storages) {
            value = storage.getNameByUUID(uuid);
            if (value != null) {
                foundIn = storage;
                break;
            }
        }
        if (value == null)
            throw new NoFoundException("Name not found by UUID: " + uuid);
        add(value, uuid, foundIn);
        return value;
    }

    public UUID getUUIDByName(String name) throws NoFoundException {
        String value = null;
        NameUUIDStorage foundIn = null;
        for (NameUUIDStorage storage : storages) {
            value = storage.getUUIDByName(name);
            if (value != null) {
                foundIn = storage;
                break;
            }
        }
        if (value == null)
            throw new NoFoundException("UUID not found by name: " + name);
        add(name, UUID.fromString(value), foundIn);
        return UUID.fromString(value);
    }

    /**
     * getUUIDByName should be used so it is also saving to the cache, this is
     * @param name
     * @return
     */
    @Deprecated(forRemoval = true, since = "1.3.2.6")
    public String getRealName(String name) {
        String value;
        for (NameUUIDStorage storage : storages) {
            value = storage.getUUIDByName(name);
            if (value != null)
                return value;
        }
        return null;
    }

    public void addStorage(NameUUIDStorage storage) {
        storages.add(storage);
        try {
            QuickSortReflectByMethodReturn.sort(storages, 0, storages.size() - 1, "getPriority", OrderType.DESCENDING);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void add(String name, UUID uuid) {
        queue.add(new WriteRequest(uuid, name, null));
    }

    private void add(String name, UUID uuid, NameUUIDStorage foundIn) {
        queue.add(new WriteRequest(uuid, name, foundIn));
    }


    @Override
    public void close() throws IOException {
        if(!running){
            CloudLogger.getLogger().warning("NameUUIDManager was not running, nothing to be closed.");
            return;
        }
        running = false;
        thread.interrupt();
    }
}
