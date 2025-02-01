package ro.deiutzblaxo.cloud.net.channel.network.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;
import ro.deiutzblaxo.cloud.net.channel.network.common.CommunicationHelper;
import ro.deiutzblaxo.cloud.net.channel.network.exceptions.InputConnectionException;
import ro.deiutzblaxo.cloud.net.channel.processing.PacketDataHandlers;
import ro.deiutzblaxo.cloud.net.channel.processing.handler.Handler;
import ro.deiutzblaxo.cloud.threads.interfaces.CallBack;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

import static ro.deiutzblaxo.cloud.net.channel.network.common.CommunicationHelper.*;

public class ConnectionGateway extends Thread{

    private SocketChannel socketChannel;

    Logger logger = LogManager.getLogger(ConnectionGateway.class);

    private final ConcurrentHashMap<Integer, CallBack<PacketData>> callBackConcurrentHashMap = new ConcurrentHashMap<>();



    public ConnectionGateway(SocketChannel socketChannel){
        this.socketChannel=socketChannel;
    }

    @Override
    public void run() {
        while (true){
            PacketData received;
            try {
                received = PacketData.readPacketData(socketChannel);
            } catch (Throwable e) {
                throw new InputConnectionException(e);
            }

            if(received.isEmpty())
                return;

            int callBackId = received.getHeader().getCallbackId();
            if(callBackId >= -1){
                if(callBackConcurrentHashMap.containsKey(callBackId)){
                    CallBack<PacketData> packetDataCallBack = callBackConcurrentHashMap.remove(callBackId);
                    packetDataCallBack.finished(received);
                    logger.info("Runned callback.");
                return;
                }
            }

            Class<? extends Handler> handler = PacketDataHandlers.getHandler(received.getHeader().getOperation());
            if(handler == null){
                logger.warn("Handler not found for operation " + received.getHeader().getOperation());
                return;
            }
                PacketData response = process(socketChannel, received);

            CommunicationHelper.send(socketChannel,response);
        }

    }

    public void makeRequest(PacketData packetData){
            send(socketChannel,packetData);
    }
    public void makeRequestWithCallBack(PacketData packetData, CallBack<PacketData> callBack){
        int callBackId = generateCallBackID();
        packetData.getHeader().setCallbackId(callBackId);
        callBackConcurrentHashMap.put(callBackId,callBack);

        makeRequest(packetData);

    }

    public void close(){
        try {
            socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ;
    }
}
