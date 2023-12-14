package ro.deiutzblaxo.cloud.fileutils.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class ReadFileInBatch {

    public static void ReadFileBatch(File file,int batchSize, Callable callable) throws IOException {
        byte[] buffer = new byte[batchSize];
        FileInputStream in = new FileInputStream(file);
        int rc = in.read(buffer);
        while(rc != -1)
        {
            if(rc < batchSize)
                buffer = new byte[rc];

            callable.processBytes(buffer);

            // next read
            rc = in.read(buffer);
        }
        System.out.println("rc = " + rc);
    }





}
