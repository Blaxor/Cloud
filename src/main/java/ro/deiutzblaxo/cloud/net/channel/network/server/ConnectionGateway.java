package ro.deiutzblaxo.cloud.net.channel.network.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;
import ro.deiutzblaxo.cloud.net.channel.network.common.CommunicationHelper;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static ro.deiutzblaxo.cloud.net.channel.network.common.CommunicationHelper.process;

public class ConnectionGateway extends Thread{

    Selector selector;

    ServerSocketChannel serverSocketChannel;

    protected final static ExecutorService pool = Executors.newCachedThreadPool();


   private static final Logger logger = LogManager.getLogger(ConnectionGateway.class);

   private final int maxConnections;
   private int currentConnections;

    public ConnectionGateway(Selector selector, ServerSocketChannel serverSocketChannel,int maxConnections){
        this.selector=selector;
        this.serverSocketChannel=serverSocketChannel;
        this.setName("ConnectionGateway-Thread");
        this.maxConnections=maxConnections;
    }

    @Override
    public void run() {
        try {
            while (true) {

                selector.select(); // Wait for events
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();


                    if (key.isAcceptable()) acceptConnection(key);
                    else if (key.isReadable()) readData(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        currentConnections++;
        logger.info("New client connected: " + clientChannel.getRemoteAddress());
    }

    private void readData(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        try {
        AtomicReference<PacketData> packetData = new AtomicReference<>(PacketData.readPacketData(clientChannel));
        if(packetData.get().isEmpty()) {
            return;
        }
            pool.submit(() -> {

                packetData.set(process(clientChannel, packetData.get()));

            CommunicationHelper.send(clientChannel, packetData.get());
            });
        } catch (SocketException socketException){
            logger.warn(socketException.getMessage());
            clientChannel.close();


        }catch (Throwable e) {
            e.printStackTrace();
        }


    }
}
