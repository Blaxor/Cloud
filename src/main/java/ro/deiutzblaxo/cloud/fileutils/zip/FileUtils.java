package ro.deiutzblaxo.cloud.fileutils.zip;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class FileUtils {



    public static void saveFile(File file, byte[] bytes){
        try {
            file.createNewFile();
            FileOutputStream fileWriter = new FileOutputStream(file);
            fileWriter.write(bytes);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void delete(File root){

        File[] files = root.listFiles();
        if(files == null)
            if(root == null)
                    return;
                else {
                root.delete();
                return;
                }
        if(files.length <=0){
            root.delete();
        }else{
            for(File file : files){
                if(file.isDirectory()){
                    delete(file);
                }else{
                    file.delete();

                }
            }
        }
    }
    public static long lastTimeModificationTime(File file){
        long highestEpoch =0;
        if(file.isDirectory()){
            for(File files : file.listFiles()){
                long currentEpoch = lastTimeModificationTime(files);
                if(currentEpoch > highestEpoch)
                    highestEpoch= currentEpoch;
            }
        }else{
            if(file.lastModified() > highestEpoch)
                highestEpoch = file.lastModified();
        }
        return highestEpoch;

    }


}
