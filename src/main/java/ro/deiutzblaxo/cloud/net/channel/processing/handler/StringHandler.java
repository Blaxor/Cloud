package ro.deiutzblaxo.cloud.net.channel.processing.handler;

import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;

import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


/**
 * StringHandler Class
 * <p>
 * An abstract handler that converts incoming byte data to a UTF-16 string
 * and processes it.
 */
public abstract class StringHandler implements Handler {

    /**
     * Converts byte data to a UTF-16 string and sending to {@link StringHandler#process(String, SocketChannel)}.
     *
     * @param data          The data to be processed.
     * @param clientChannel The client channel from which the data was received.
     * @return The processed {@link PacketData}.
     */
    @Override
    public PacketData process(byte[] data, SocketChannel clientChannel) {
        return process(new String(data, StandardCharsets.UTF_16), clientChannel);
    }

    /**
     * Processes the given string data.
     *
     * @param data          The string data converted in {@link Handler#process(byte[], SocketChannel)}
     * @param clientChannel The client channel from which the data was received.
     * @return The processed {@link PacketData}.
     */
    public abstract PacketData process(String data, SocketChannel clientChannel);
}
