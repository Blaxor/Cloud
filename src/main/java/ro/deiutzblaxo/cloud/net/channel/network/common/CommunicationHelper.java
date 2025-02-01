package ro.deiutzblaxo.cloud.net.channel.network.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;
import ro.deiutzblaxo.cloud.net.channel.network.exceptions.DataHandlerException;
import ro.deiutzblaxo.cloud.net.channel.network.exceptions.OutputConnectionException;
import ro.deiutzblaxo.cloud.net.channel.processing.PacketDataHandlers;
import ro.deiutzblaxo.cloud.net.channel.processing.handler.Handler;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class CommunicationHelper {

    private final static Logger logger = LogManager.getLogger(CommunicationHelper.class);


    /**
     * Sends a {@link PacketData} object over the given {@link SocketChannel}.
     *
     * @param clientChannel The {@link SocketChannel} to send the data through.
     * @param packetData    The {@link PacketData} to be transmitted.
     * @throws OutputConnectionException If an error occurs during the transmission process.
     */
    public static void send(SocketChannel clientChannel, PacketData packetData) throws OutputConnectionException {
        if (packetData == null) {
            return;
        }
        if (packetData.isEmpty()) {
            return;
        }
        try {
            clientChannel.write(packetData.writeObject(ByteBuffer.allocate(0)));
        } catch (Throwable t) {
            throw new OutputConnectionException(t);
        }
    }

    /**
     * Processes the incoming {@link PacketData} using the appropriate handler instance.
     *
     * @param clientChannel The {@link SocketChannel} from which the data is received.
     * @param packetData    The {@link PacketData} to be processed.
     * @return The processed {@link PacketData} object.
     * @throws DataHandlerException If no handler is found for the operation.
     */
    public static PacketData process(SocketChannel clientChannel, PacketData packetData) {

        Handler handlerInstance = PacketDataHandlers.getInstanceHandler(packetData.getHeader().getOperation());

        if (handlerInstance == null) {
            logger.warn("The handler " + packetData.getHeader().getOperation() + " is not known ");
            throw new DataHandlerException();
        }
        logger.debug("The handler is of type" + handlerInstance.getClass().getName());

        int callBackId = packetData.getHeader().getCallbackId();

        packetData = handlerInstance.process(packetData.getBody().content(), clientChannel);

        if (callBackId >= 0 && packetData != null && !packetData.isEmpty())
            packetData.getHeader().setCallbackId(callBackId);

        return packetData;

    }

    /**
     * Generates a random integer between 0 and 999,999,999 (inclusive).
     *
     * @return A randomly generated integer in the range [0, 999,999,999].
     */
    public static int generateCallBackID() {
        return (int) (Math.random() * 1000000000);
    }
}
