package ro.deiutzblaxo.cloud.net.channel.processing.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;

import java.nio.channels.SocketChannel;

public class NoneHandler implements Handler {
    private static final Logger logger = LogManager.getLogger(NoneHandler.class);

    public NoneHandler() {

    }

    @Override
    public PacketData process(byte[] data, SocketChannel clientChannel) {

        logger.debug("Got the following message in the NoneHandler: " + new String(data));

        return null;
    }
}
