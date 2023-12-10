package ro.deiutzblaxo.cloud.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;


/**
 * @see ro.deiutzblaxo.cloud.yaml.YAMLFile
 * @apiNote Please note that it is using SnakeYAML Library.
 *
 */
public class YAMLFileImpl  implements  YAMLFile{
    private String _currentPath;

    private final Yaml yaml;

    private Map<String,Object> objects;

    public YAMLFileImpl(){
        this.yaml=new Yaml();
    }
    public YAMLFileImpl(String path){
        this();
        this._currentPath=path;

    }

    @Override
    public YAMLFile load() throws FileNotFoundException {
        objects = yaml.load(new FileReader(_currentPath));
        return this;
    }

    @Override
    public YAMLFile load(String path) throws FileNotFoundException {
    _currentPath = path;

    return load();
    }

    @Override
    public YAMLFile save() {
        try(FileWriter fileWriter = new FileWriter(_currentPath)){
            yaml.dump(objects,fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            return this;
        }
    }

    @Override
    public YAMLFile save(String path) {
        _currentPath = path;
        return save();
    }

    @Override
    public YAMLFile put(String key, Object value) {
        objects.put(key,value);
        return this;
    }

    @Override
    public YAMLFile delete(String key) {
        objects.remove(key);
        return this;
    }

    @Override
    public YAMLFile put(String key, Object value, boolean save) {
        put(key,value);
        if(save)
            save();
        return this;
    }

    @Override
    public YAMLFile delete(String key, boolean save) {
        delete(key);
        if(save)
            save();
        return this;
    }

    @Override
    public Object get(String key) {
        return objects.get(key);
    }

    @Override
    public String getPath() {
        return _currentPath;
    }
}
