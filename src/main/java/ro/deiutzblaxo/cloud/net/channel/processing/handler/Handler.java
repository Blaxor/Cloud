package ro.deiutzblaxo.cloud.net.channel.processing.handler;

import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;

import java.nio.channels.SocketChannel;

/**
 * Handler Interface
 * <p>
 * Defines a contract for processing packet data received from a client channel.
 * Implementations are required to have an empty constructor.
 */
public interface Handler {
    /**
     * Processes the given data received from the client channel.
     *
     * @param data          The data to be processed.
     * @param clientChannel The client channel from which the data was received.
     * @return The processed {@link PacketData}.
     */
    PacketData process(byte[] data, SocketChannel clientChannel);
}
