package ro.deiutzblaxo.cloud.net.channel.data.objects;

import ro.deiutzblaxo.cloud.net.channel.data.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


/**
 * Represents a data packet containing structured information consisting of a {@link Header} and {@link Body}.
 * This class facilitates serialization and deserialization of packet data to and from streams.
 * Instances of PacketData are immutable once initialized with a specific header and body.
 */
public class PacketData implements Serializable, Data {

    private final Header header;
    private final Body body;

    /**
     * Constructs a PacketData object with the specified header and body.
     *
     * @param header The header containing operation information.
     * @param body   The body containing actual data content.
     */
    public PacketData(Header header, Body body) {
        this.header = header;
        this.body = body;

    }

    /**
     * Constructs a PacketData object with the specified operation and body content.
     *
     * @param operation The operation code associated with this packet.
     * @param content   The body content as byte array.
     */
    public PacketData(int operation, byte[] content) {
        this(new Header(operation, content.length, -1), new Body(content));
    }

    public PacketData(int operation, byte[] content, int callbackId) {
        this(new Header(operation, content.length, callbackId), new Body(content));
    }

    public PacketData(int operation, String content) {
        this(new Header(operation, content.getBytes(StandardCharsets.UTF_16).length, -1),
                new Body(content.getBytes(StandardCharsets.UTF_16)));
    }

    public PacketData(int operation, String content, int callbackId) {
        this(new Header(operation, content.getBytes(StandardCharsets.UTF_16).length, callbackId),
                new Body(content.getBytes(StandardCharsets.UTF_16)));
    }

    /**
     * Deserialize from the {@link InputStream} the object {@link PacketData}.
     *
     * @param ois The target {@link InputStream}.
     * @return The deserialized object.
     */
    public static PacketData readPacketData(InputStream ois) throws IOException {
        Header header1 = Header.readHeader(ois);
        Body body1 = Body.readBody(ois, header1.bodySize);
        return new PacketData(header1, body1);

    }

    public static PacketData readPacketData(SocketChannel ois) throws IOException {

        Header header1 = Header.readHeader(ois);
        Body body1 = Body.readBody(ois, header1.bodySize);
        return new PacketData(header1, body1);

    }

    /**
     * Writes the PacketData object to an output stream.
     *
     * @param outputStream The output stream to which the object will be written.
     * @throws IOException If an I/O error occurs while writing to the stream.
     */
    public void writeObject(OutputStream outputStream)
            throws IOException {
        header.writeObject(outputStream);
        body.writeObject(outputStream);
        outputStream.flush();
    }

    @Override
    public ByteBuffer writeObject(ByteBuffer buffer) {
        buffer = header.writeObject(buffer);
        buffer = body.writeObject(buffer);
        buffer.position(0);
        return buffer;
    }

    @Override
    public boolean isEmpty() {
        return body.isEmpty() && header.isEmpty();
    }

    public Header getHeader() {
        return header;
    }

    public Body getBody() {
        return body;
    }

    /**
     * Checks whether this PacketData object is equal to another object.
     *
     * @param obj The object to compare for equality.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PacketData packetData) {
            return packetData.body.equals(body) && packetData.header.equals(header);
        }
        return false;
    }

    @Override
    public String toString() {
        return "PacketData{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
