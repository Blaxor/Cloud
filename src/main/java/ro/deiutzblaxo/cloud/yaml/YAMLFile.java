package ro.deiutzblaxo.cloud.yaml;

import ro.deiutzblaxo.cloud.expcetions.PathNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @since Version 1.3.0
 * @author Deiutz
 */
public interface YAMLFile {
    /**
     * It is re-loading the YAMLFile using the last path known during current runtime.
     * @return YAMLFile resulted after loading the contents of the YAML.
     */
    YAMLFile load() throws FileNotFoundException;
    /**
     * It is loading the YAML file
     * @param path The YAML file path. Example: {@systemProperty C:/User/Application/Config/config.yaml}
     * @return YAMLFile resulted after loading the contents of the YAML.
     */
    YAMLFile load(String path) throws FileNotFoundException;

    /**
     * Save to the last known path during current runtime.
     * @return the saved YAMLFile
     */
    YAMLFile save() throws IOException;
    /**
     * Saving the YAMLFile to the path
     * @param path the path where to save the YAMLFile
     * @return the saved YAMLFile
     */
    YAMLFile save(String path);

    /**
     *
     * @param key the key to which have the value saved.
     * @param value the value itself.
     * @return the YAMLFile with the value saved.
     */
    YAMLFile put(String key, Object value);

    /**
     * @param key the object key which should be deleted
     * @return the YAMLFile with the value deleted.
     */
    YAMLFile delete(String key);

    /**
     *
     * @param key the key of the object
     * @param value the value of the object
     * @param save should save immediately
     * @return the YAMLFile saved.
     */
    YAMLFile put(String key, Object value, boolean save);

    /**
     *
     * @param key the object key which should be deleted
     * @param save should be saved immediately
     * @return the YAMLFile with the value deleted.
     */
    YAMLFile delete(String key, boolean save);

    /**
     *
     * @param key the object's key
     * @return the object attributed to that key
     */
    Object get(String key);

    /**
     *
     * @return the current used path
     */
    String getPath();
}
