package ro.deiutzblaxo.cloud.nus.prefab;

import lombok.Data;
import lombok.NonNull;
import org.yaml.snakeyaml.Yaml;
import ro.deiutzblaxo.cloud.nus.NameUUIDStorage;
import ro.deiutzblaxo.cloud.nus.PriorityNUS;
import ro.deiutzblaxo.cloud.utils.CloudLogger;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

@Data
public class NameUUIDStorageYaml implements NameUUIDStorage {

    private TreeMap<String, String> cache;
    private final File file;
    private Timer timer = new Timer();
    private PriorityNUS priority;

    public NameUUIDStorageYaml(File diskCacheFolder, long saveInterval, PriorityNUS priority) {


        file = new File(diskCacheFolder, "cachePlayer.yml");
        this.priority = priority;
        try {
            loadSave();
        } catch (FileNotFoundException e) {
            CloudLogger.getLogger().log(Level.WARNING, "cachePlayer.yml not found! Recreating file...");
            try {
                file.createNewFile();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                CloudLogger.getLogger().log(Level.SEVERE, "File can`t be created!");
            }
        }
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    updateSave();
                } catch (FileNotFoundException e) {
                    try {
                        CloudLogger.getLogger().log(Level.WARNING, "cachePlayer.yml not found! Recreating file...");

                        file.createNewFile();

                    } catch (IOException ioException) {
                        ioException.printStackTrace();

                    }
                }
            }
        }, 0, saveInterval);
    }

    public void updateSave() throws FileNotFoundException {

        CloudLogger.getLogger().log(Level.INFO, "Auto-Save cache name-uuid in local file cachePlayer.yml");
        PrintWriter writer = new PrintWriter(file);
        Yaml yaml = new Yaml();
        yaml.dump(cache, writer);
        writer.close();
    }

    public void loadSave() throws FileNotFoundException {
        cache = new TreeMap<>();
        FileInputStream stream = new FileInputStream(file);
        Yaml yaml = new Yaml();
        HashMap<String, Object> a = yaml.load(stream);
        if (a != null)
            if (!a.isEmpty())
                a.forEach((s, o) -> cache.put(s, (String) o));
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNameByUUID(UUID uuid) {
        return cache.get(uuid.toString());
    }

    @Override
    public String getUUIDByName(String name) {

        return cache.get(name);
    }

    @Override
    public String getRealName(String fakename) {
        return null;
    }

    @Override
    public @NonNull PriorityNUS getPriority() {
        return priority;
    }

    public void add(String name, UUID uuid) {
        getCache().put(name, uuid.toString());
        getCache().put(uuid.toString(), name);
    }


}
