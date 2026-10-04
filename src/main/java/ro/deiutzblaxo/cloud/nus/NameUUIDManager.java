package ro.deiutzblaxo.cloud.nus;

import ro.deiutzblaxo.cloud.expcetions.NoFoundException;
import ro.deiutzblaxo.cloud.datastructure.OrderType;
import ro.deiutzblaxo.cloud.datastructure.QuickSortReflectByMethodReturn;
import ro.deiutzblaxo.cloud.utils.CloudLogger;
import ro.deiutzblaxo.cloud.utils.objects.Pair;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class NameUUIDManager implements Closeable {

    private ArrayList<NameUUIDStorage> storages = new ArrayList<>();
    private LinkedBlockingQueue<Pair<UUID, String>> queue = new LinkedBlockingQueue<>();
    private boolean running = false;
    private final Thread thread =  new Thread(() -> {
        while (running) {
            try {
                    Pair<UUID, String> value = queue.take();
                    storages.forEach(nameUUIDStorage -> {
                        try {
                            nameUUIDStorage.add(value.getLast(), value.getFirst());
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
        for (NameUUIDStorage storage : storages)
            value = storage.getNameByUUID(uuid);
        if (value == null)
            throw new NoFoundException("Name not found by UUID: " + uuid);
        add(value, uuid);
        return value;
    }

    public UUID getUUIDByName(String name) throws NoFoundException {
        String value = null;
        for (NameUUIDStorage storage : storages) {
            value = storage.getUUIDByName(name);
            if (value != null)
                break;
        }
        if (value == null)
            throw new NoFoundException("UUID not found by name: " + name);
        add(name, UUID.fromString(value));
        return UUID.fromString(value);
    }

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
        queue.add(new Pair<>(uuid, name));
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
