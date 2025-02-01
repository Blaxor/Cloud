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

public class ConnectionGateway extends Thread {

    private final ConcurrentHashMap<Integer, CallBack<PacketData>> callBackConcurrentHashMap = new ConcurrentHashMap<>();
    private final SocketChannel socketChannel;
    Logger logger = LogManager.getLogger(ConnectionGateway.class);


    public ConnectionGateway(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

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

    public void makeRequest(PacketData packetData) {
        send(socketChannel, packetData);
    }

    public void makeRequestWithCallBack(PacketData packetData, CallBack<PacketData> callBack) {
        int callBackId = generateCallBackID();
        packetData.getHeader().setCallbackId(callBackId);
        callBackConcurrentHashMap.put(callBackId, callBack);

        makeRequest(packetData);

    }

    public void close() {
        try {
            socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
