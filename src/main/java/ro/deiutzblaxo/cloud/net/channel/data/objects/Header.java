package ro.deiutzblaxo.cloud.net.channel.data.objects;


import ro.deiutzblaxo.cloud.net.channel.data.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * This should be the first part of any communication.
 */

public class Header implements Serializable, Data {

    int operation = 0;
    int bodySize =0;
    int callbackId = -1;
    private static final int MAX_HEADER_SIZE = 12;

    public Header(int operation, int bodySize, int callbackId) {
        this.operation = operation;
        this.bodySize = bodySize;
        this.callbackId = callbackId;
    }

    public Header(int operation, int bodySize) {
        this.operation = operation;
        this.bodySize = bodySize;
    }

    public void writeObject(OutputStream outputStream)
            throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_HEADER_SIZE);
        buffer.putInt(this.operation);
        buffer.putInt(this.bodySize);
        buffer.putInt(this.callbackId);
        outputStream.write(buffer.array());
    }

    @Override
    public ByteBuffer writeObject(ByteBuffer buffer) {
        if(buffer.remaining() <= MAX_HEADER_SIZE){
            int newSize = buffer.capacity()-buffer.remaining() + MAX_HEADER_SIZE;
            ByteBuffer byteBuffer = ByteBuffer.allocate(newSize);
            byteBuffer.put(buffer.array());
            buffer = byteBuffer;
            buffer.putInt(this.operation);
            buffer.putInt(this.bodySize);
            buffer.putInt(this.callbackId);

        }else{
            buffer.putInt(this.operation);
            buffer.putInt(this.bodySize);
            buffer.putInt(this.callbackId);
        }
        return buffer;
    }

    @Override
    public boolean isEmpty() {
        return bodySize==0 && operation == 0 && this.callbackId <= 0;
    }


    //Read from any InputStream the header.
     static Header readHeader(InputStream ois) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(MAX_HEADER_SIZE);
        buffer.put(ois.readNBytes(MAX_HEADER_SIZE));
        buffer.position(0);
        return new Header(buffer.getInt(),buffer.getInt(),buffer.getInt());
    }

    static Header readHeader(SocketChannel ois) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(MAX_HEADER_SIZE);
        ois.read(buffer);
        buffer.position(0);
        return new Header(buffer.getInt(),buffer.getInt(),buffer.getInt());
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Header header){
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

    @Override
    public String toString() {
        return "Header{" +
                "operation=" + operation +
                ", bodySize=" + bodySize +
                ", callbackId=" + callbackId +
                '}';
    }
}
