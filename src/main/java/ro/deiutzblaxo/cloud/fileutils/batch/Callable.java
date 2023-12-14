package ro.deiutzblaxo.cloud.fileutils.batch;

import java.io.IOException;

public interface Callable {

    void processBytes(byte[] bytes) throws IOException;

}
