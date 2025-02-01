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

    private final static Logger logger =  LogManager.getLogger(CommunicationHelper.class);

    public static void send(SocketChannel clientChannel, PacketData packetData) throws OutputConnectionException {
        if(packetData == null){
            return;
        }
        if(packetData.isEmpty()){
            return;
        }
        try {
            logger.info("Sending PacketData to server");
            clientChannel.write(packetData.writeObject(ByteBuffer.allocate(0)));
        }catch (Throwable t){
            throw new OutputConnectionException(t);
        }
    }

    public static PacketData process(SocketChannel clientChannel, PacketData packetData) {

        logger.error("Process start");
            Handler handlerInstance = PacketDataHandlers.getInstanceHandler(packetData.getHeader().getOperation());

            if(handlerInstance == null)
                throw new DataHandlerException();
        logger.error("Process continue2");
            int callBackId = packetData.getHeader().getCallbackId();

            packetData = handlerInstance.process(packetData.getBody().content(), clientChannel);
        logger.error("Process continue3");
            if(callBackId >= 0 && packetData != null && !packetData.isEmpty())
                packetData.getHeader().setCallbackId(callBackId);
             logger.error("Process end");
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
