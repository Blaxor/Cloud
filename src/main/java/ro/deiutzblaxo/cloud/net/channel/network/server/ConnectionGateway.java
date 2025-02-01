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

/**
 * Manages client connections and data processing for the server.
 */
public class ConnectionGateway extends Thread {

    protected final static ExecutorService pool = Executors.newCachedThreadPool();
    private static final Logger logger = LogManager.getLogger(ConnectionGateway.class);
    private final int maxConnections;
    Selector selector;
    ServerSocketChannel serverSocketChannel;
    private int currentConnections;

    /**
     * Constructs a {@link ConnectionGateway} with the specified selector, server socket
     * channel, and maximum connections.
     *
     * @param selector            The {@link Selector} to monitor.
     * @param serverSocketChannel The {@link ServerSocketChannel} for client connections.
     * @param maxConnections      The maximum number of concurrent connections allowed.
     */
    public ConnectionGateway(Selector selector, ServerSocketChannel serverSocketChannel, int maxConnections) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
        this.setName("ConnectionGateway-Thread");
        this.maxConnections = maxConnections;
    }

    /**
     * Continuously monitors for events on the selector and processes client connections
     * and data.
     */
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

    /**
     * Accepts a new client connection.
     *
     * @param key The selection key associated with the server socket channel.
     * @throws IOException If an I/O error occurs.
     */
    private void acceptConnection(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        currentConnections++;
        logger.info("New client connected: " + clientChannel.getRemoteAddress());
    }

    /**
     * Reads data from a client channel and processes it.
     *
     * @param key The selection key associated with the client socket channel.
     * @throws IOException If an I/O error occurs.
     */
    private void readData(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        try {
            AtomicReference<PacketData> packetData = new AtomicReference<>(PacketData.readPacketData(clientChannel));
            if (packetData.get().isEmpty()) {
                return;
            }
            pool.submit(() -> {

                packetData.set(process(clientChannel, packetData.get()));

                CommunicationHelper.send(clientChannel, packetData.get());
            });
        } catch (SocketException socketException) {
            logger.warn(socketException.getMessage());
            clientChannel.close();


        } catch (Throwable e) {
            e.printStackTrace();
        }


    }
}
