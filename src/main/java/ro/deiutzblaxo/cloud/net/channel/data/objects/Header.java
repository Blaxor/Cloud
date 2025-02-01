package ro.deiutzblaxo.cloud.net.channel.data.objects;


import ro.deiutzblaxo.cloud.net.channel.data.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Represents the header of a communication message.
 * This should be the first part of any communication, containing metadata about the message.
 */

public class Header implements Serializable, Data {

    private static final int MAX_HEADER_SIZE = 12;
    int operation;
    int bodySize;
    int callbackId = -1;


    /**
     * Constructs a Header with an operation, body size, and callback ID.
     *
     * @param operation  The operation code for the communication.
     * @param bodySize   The size of the body in bytes.
     * @param callbackId The callback identifier, used for request-response handling.
     */
    public Header(int operation, int bodySize, int callbackId) {
        this.operation = operation;
        this.bodySize = bodySize;
        this.callbackId = callbackId;
    }

    /**
     * Constructs a Header with an operation and body size.
     * The callback ID is set to -1 by default.
     *
     * @param operation The operation code for the communication.
     * @param bodySize  The size of the body in bytes.
     */
    public Header(int operation, int bodySize) {
        this.operation = operation;
        this.bodySize = bodySize;
    }

    /**
     * Reads a header from an {@link InputStream}.
     *
     * @param ois The input stream to read from.
     * @return A {@link Header} object containing the read values.
     * @throws IOException If an I/O error occurs while reading.
     */
    static Header readHeader(InputStream ois) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(MAX_HEADER_SIZE);
        buffer.put(ois.readNBytes(MAX_HEADER_SIZE));
        buffer.position(0);
        return new Header(buffer.getInt(), buffer.getInt(), buffer.getInt());
    }

    /**
     * Reads a header from a {@link SocketChannel}.
     *
     * @param ois The socket channel to read from.
     * @return A {@link Header} object containing the read values.
     * @throws IOException If an I/O error occurs while reading.
     */
    static Header readHeader(SocketChannel ois) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(MAX_HEADER_SIZE);
        ois.read(buffer);
        buffer.position(0);
        return new Header(buffer.getInt(), buffer.getInt(), buffer.getInt());
    }

    /**
     * Writes the header data into an {@link OutputStream}.
     *
     * @param outputStream The output stream to write to.
     * @throws IOException If an I/O error occurs.
     */
    public void writeObject(OutputStream outputStream)
            throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_HEADER_SIZE);
        buffer.putInt(this.operation);
        buffer.putInt(this.bodySize);
        buffer.putInt(this.callbackId);
        outputStream.write(buffer.array());
    }

    /**
     * Writes the header data into a {@link ByteBuffer}.
     * If the buffer does not have enough remaining space, a new buffer is allocated.
     *
     * @param buffer The byte buffer to write to.
     * @return A {@link ByteBuffer} containing the written header.
     */
    @Override
    public ByteBuffer writeObject(ByteBuffer buffer) {
        if (buffer.remaining() <= MAX_HEADER_SIZE) {
            int newSize = buffer.capacity() - buffer.remaining() + MAX_HEADER_SIZE;
            ByteBuffer byteBuffer = ByteBuffer.allocate(newSize);
            byteBuffer.put(buffer.array());
            buffer = byteBuffer;
            buffer.putInt(this.operation);
            buffer.putInt(this.bodySize);
            buffer.putInt(this.callbackId);

        } else {
            buffer.putInt(this.operation);
            buffer.putInt(this.bodySize);
            buffer.putInt(this.callbackId);
        }
        return buffer;
    }

    /**
     * Checks if the header is empty.
     *
     * @return {@code true} if the header has default values, otherwise {@code false}.
     */
    @Override
    public boolean isEmpty() {
        return bodySize == 0 && operation == 0 && this.callbackId <= 0;
    }

    /**
     * Compares this {@link Header} instance with another object for equality.
     * Two headers are equal if their operation, body size, and callback ID match.
     *
     * @param o The object to compare.
     * @return {@code true} if the objects are equal, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Header header) {
            return header.operation == this.operation && header.bodySize == this.bodySize && header.callbackId == this.callbackId;
        }
        return false;
    }

    public int getBodySize() {
        return bodySize;
    }

    public int getOperation() {
        return operation;
    }

    public int getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(int callbackId) {
        this.callbackId = callbackId;
    }

    /**
     * Returns a string representation of the header.
     *
     * @return A string containing the operation, body size, and callback ID.
     */
    @Override
    public String toString() {
        return "Header{" +
                "operation=" + operation +
                ", bodySize=" + bodySize +
                ", callbackId=" + callbackId +
                '}';
    }
}
