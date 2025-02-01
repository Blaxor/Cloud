package ro.deiutzblaxo.cloud.net.channel.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public interface Data {


    /**
     * Write to the {@link OutputStream} the encapsulated values, this method should not flush or do any other operation on the {@link OutputStream}
     * @param outputStream The stream from which to write
     */
    void writeObject(OutputStream outputStream) throws IOException;

    /**
     * Write to the {@link ByteBuffer} the encapsulated values, this method should not flush or do any other operation on the {@link ByteBuffer}
     * @param buffer The stream from which to write
     */
    ByteBuffer writeObject(ByteBuffer buffer);

    boolean isEmpty();

}
