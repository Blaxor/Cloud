package ro.deiutzblaxo.cloud.net.channel.network.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * Represents a server that listens for incoming connections on a specified port.
 */
public class CloudServer {

    private static final Logger _logger = LogManager.getLogger(CloudServer.class);
    private static ServerSocketChannel serverSocketChannel;
    private final int port;


    /**
     * Constructs a {@link CloudServer} with the specified port.
     *
     * @param port The port number on which the server will listen for incoming connections.
     */
    public CloudServer(int port) {
        this.port = port;
    }

    /**
     * Starts the server by opening a selector and server socket channel, configuring
     * it to be non-blocking, and registering it for accepting connections.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void start() throws IOException {
        Selector selector = Selector.open();

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        ConnectionGateway connectionGateway = new ConnectionGateway(selector, serverSocketChannel, 500);
        connectionGateway.start();
        _logger.info("The Cloud Server started on port: " + port);
    }


}
