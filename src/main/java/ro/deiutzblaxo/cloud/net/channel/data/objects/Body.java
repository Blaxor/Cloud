package ro.deiutzblaxo.cloud.net.channel.data.objects;

import ro.deiutzblaxo.cloud.net.channel.data.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;


public record Body(byte[] content) implements Serializable, Data {

    @Override
    public void writeObject(OutputStream outputStream) throws IOException {
        outputStream.write(content);
    }

    @Override
    public ByteBuffer writeObject(ByteBuffer buffer) {

        if(buffer.remaining() < content.length){
            int newSize = buffer.capacity()-buffer.remaining() + content.length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(newSize);
            byteBuffer.put(buffer.array());
            buffer = byteBuffer;
            buffer.put(content);

        }else{
            buffer.put(content);
        }
        return buffer;
    }

    @Override
    public boolean isEmpty() {
        return content.length==0;
    }


    //Read from any InputStream the header.
    static Body readBody(InputStream ois, int size) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        ois.read(buffer.array());
        return new Body(buffer.array());

    }

    static Body readBody(SocketChannel ois, int size) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        ois.read(buffer);
        buffer.position(0);
        return new Body(buffer.array());

    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof Body body){
            return Arrays.equals(content, body.content);
        }
        return false;
    }


    @Override
    public String toString() {
        return "Body{" +
                "content=" + new String(content)+
                '}';
    }
}
