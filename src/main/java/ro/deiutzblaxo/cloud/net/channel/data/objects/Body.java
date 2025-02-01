package ro.deiutzblaxo.cloud.net.channel.data.objects;

import ro.deiutzblaxo.cloud.net.channel.data.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Represents a data body that contains byte content.
 * Implements {@link Data} for serialization and deserialization operations.
 */
public record Body(byte[] content) implements Serializable, Data {


    /**
     * Reads a body from an {@link InputStream} with a specified size.
     *
     * @param ois  The input stream to read from.
     * @param size The size of the content to read.
     * @return A {@link Body} object containing the read bytes.
     * @throws IOException If an I/O error occurs while reading.
     */
    static Body readBody(InputStream ois, int size) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        ois.read(buffer.array());
        return new Body(buffer.array());

    }

    /**
     * Reads a body from a {@link SocketChannel} with a specified size.
     *
     * @param ois  The socket channel to read from.
     * @param size The size of the content to read.
     * @return A {@link Body} object containing the read bytes.
     * @throws IOException If an I/O error occurs while reading.
     */
    static Body readBody(SocketChannel ois, int size) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        ois.read(buffer);
        buffer.position(0);
        return new Body(buffer.array());

    }

    /**
     * Writes the content of this body to an {@link OutputStream}.
     *
     * @param outputStream The output stream to write the content to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void writeObject(OutputStream outputStream) throws IOException {
        outputStream.write(content);
    }

    /**
     * Writes the content of this body into a {@link ByteBuffer}.
     * If the buffer does not have enough remaining space, a new buffer is allocated.
     *
     * @param buffer The byte buffer where the content will be written.
     * @return A {@link ByteBuffer} containing the written data.
     */
    @Override
    public ByteBuffer writeObject(ByteBuffer buffer) {

        if (buffer.remaining() < content.length) {
            int newSize = buffer.capacity() - buffer.remaining() + content.length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(newSize);
            byteBuffer.put(buffer.array());
            buffer = byteBuffer;
            buffer.put(content);

        } else {
            buffer.put(content);
        }
        return buffer;
    }

    /**
     * Checks if the body content is empty.
     *
     * @return {@code true} if the content is empty, otherwise {@code false}.
     */
    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }


    /**
     * Compares this {@link Body} instance with another object for equality.
     * Two {@code Body} instances are considered equal if their byte contents are the same.
     *
     * @param o The object to compare.
     * @return {@code true} if the objects are equal, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Body body) {
            return Arrays.equals(content, body.content);
        }
        return false;
    }

    /**
     * Returns a string representation of this {@code Body} object.
     *
     * @return A string representation containing the byte content as a string.
     */
    @Override
    public String toString() {
        return "Body{" +
                "content=" + new String(content) +
                '}';
    }
}
