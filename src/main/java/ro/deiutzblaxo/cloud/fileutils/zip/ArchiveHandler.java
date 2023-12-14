package ro.deiutzblaxo.cloud.fileutils.zip;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileNotFoundException;

public abstract class ArchiveHandler {

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