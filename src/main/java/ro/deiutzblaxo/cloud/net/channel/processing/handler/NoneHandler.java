package ro.deiutzblaxo.cloud.net.channel.processing.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;

import java.nio.channels.SocketChannel;

/**
 * A handler that processes incoming data and logs a debug message.
 */
public class NoneHandler implements Handler {
    private static final Logger logger = LogManager.getLogger(NoneHandler.class);

    /**
     * The required Constructor.
     */
    public NoneHandler() {

    }

    /**
     * Logs the data as string.
     *
     * @param data          The data to be processed.
     * @param clientChannel The client channel from which the data was received.
     * @return Always returns null.
     */
    @Override
    public PacketData process(byte[] data, SocketChannel clientChannel) {

        logger.debug("Got the following message in the NoneHandler: " + new String(data));

        return null;
    }
}
