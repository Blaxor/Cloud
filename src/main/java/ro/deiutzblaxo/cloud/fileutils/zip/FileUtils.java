package ro.deiutzblaxo.cloud.fileutils.zip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {



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

    public static class ArchiveHandler {

        public static void zip(String src, String dst) throws ZipException, FileNotFoundException {
            ZipFile zipFile = new ZipFile(dst);
            File srcFile = new File(src);
            if(srcFile.exists())
                for( File file: srcFile.listFiles()){
                    System.out.println("zipping file: "+file.getPath());
                    if (file.isDirectory())
                    zipFile.addFolder(file);
                    else zipFile.addFile(file);
                }
            else{
                throw new FileNotFoundException();
            }

        }

        public static void unzip(String zipFilePath, String dst) throws ZipException {
            ZipFile zipFile = new ZipFile(zipFilePath);
            zipFile.extractAll(dst);
        }

    }
}
