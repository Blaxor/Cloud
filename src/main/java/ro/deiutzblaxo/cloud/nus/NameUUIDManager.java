package ro.deiutzblaxo.cloud.nus;

import ro.deiutzblaxo.cloud.expcetions.NoFoundException;
import ro.deiutzblaxo.cloud.datastructure.OrderType;
import ro.deiutzblaxo.cloud.datastructure.QuickSortReflectByMethodReturn;
import ro.deiutzblaxo.cloud.expcetions.TooManyArgs;
import ro.deiutzblaxo.cloud.utils.objects.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NameUUIDManager {


    private ArrayList<NameUUIDStorage> storages = new ArrayList<>();
    private ConcurrentLinkedQueue<Pair<UUID, String>> q = new ConcurrentLinkedQueue<>();


    public NameUUIDManager(NameUUIDStorage... storage) {
        for (int i = 0; i < storage.length; i++) {
            storages.add(storage[i]);
        }
        try {
            QuickSortReflectByMethodReturn.sort(storages, 0, storages.size() - 1, "getPriority", OrderType.DESCENDING);
        } catch (NoSuchFieldException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            while (true) {
                if (!q.isEmpty()) {
                    Pair<UUID, String> value = q.remove();
                    storages.forEach(nameUUIDStorage -> {
                        try {
                            nameUUIDStorage.add(value.getLast(), value.getFirst());
                        } catch (TooManyArgs e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

        }).start();

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
        q.add(new Pair<>(uuid, name));
    }


}
