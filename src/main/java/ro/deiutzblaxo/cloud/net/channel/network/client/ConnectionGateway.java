package ro.deiutzblaxo.cloud.net.channel.network.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;
import ro.deiutzblaxo.cloud.net.channel.network.common.CommunicationHelper;
import ro.deiutzblaxo.cloud.net.channel.network.exceptions.DataHandlerException;
import ro.deiutzblaxo.cloud.net.channel.network.exceptions.InputConnectionException;
import ro.deiutzblaxo.cloud.threads.interfaces.CallBack;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

import static ro.deiutzblaxo.cloud.net.channel.network.common.CommunicationHelper.*;


/**
 * Handles the connection between the client and the server.
 * This class runs in a separate thread, continuously listening for incoming {@link PacketData}
 * and processing requests accordingly.
 */
public class ConnectionGateway extends Thread {

    private static final Logger logger = LogManager.getLogger(ConnectionGateway.class);
    private final ConcurrentHashMap<Integer, CallBack<PacketData>> callBackConcurrentHashMap = new ConcurrentHashMap<>();
    private final SocketChannel socketChannel;

    /**
     * Constructs a {@link ConnectionGateway} with the given socket channel.
     *
     * @param socketChannel The socket channel used for communication with the server.
     */
    public ConnectionGateway(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }


    /**
     * Continuously listens for incoming {@link PacketData} from the server,
     * processes them, and sends appropriate responses.
     * If the received packet contains a callback ID, it is handled accordingly.
     */
    @Override
    public void run() {
        logger.info("Started the connection.");
        while (true) {
            PacketData received;
            try {
                received = PacketData.readPacketData(socketChannel);
            } catch (Throwable e) {
                throw new InputConnectionException(e);
            }

            if (received.isEmpty())
                return;

            int callBackId = received.getHeader().getCallbackId();
            if (callBackId >= -1) {
                if (callBackConcurrentHashMap.containsKey(callBackId)) {
                    CallBack<PacketData> packetDataCallBack = callBackConcurrentHashMap.remove(callBackId);
                    packetDataCallBack.finished(received);
                    return;
                }
            }
            try {
                PacketData response = process(socketChannel, received);
                CommunicationHelper.send(socketChannel, response);
            } catch (DataHandlerException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Sends a {@link PacketData} object to the connected server.
     *
     * @param packetData The complete data packet (header + body) to be sent.
     */
    public void makeRequest(PacketData packetData) {
        send(socketChannel, packetData);
    }

    /**
     * Sends a request to the server and associates it with a callback.
     * The callback is triggered when a response with a matching callback ID is received.
     *
     * @param packetData The packet data to be sent.
     * @param callBack   The callback function to be executed when a response is received.
     */
    public void makeRequestWithCallBack(PacketData packetData, CallBack<PacketData> callBack) {
        int callBackId = generateCallBackID();
        packetData.getHeader().setCallbackId(callBackId);
        callBackConcurrentHashMap.put(callBackId, callBack);
        makeRequest(packetData);
    }

    /**
     * Closes the socket connection to the server.
     */
    public void close() {
        try {
            socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
