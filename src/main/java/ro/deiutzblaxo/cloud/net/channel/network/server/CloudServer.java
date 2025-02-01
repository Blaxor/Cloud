package ro.deiutzblaxo.cloud.net.channel.network.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class CloudServer {

    private static final Logger _logger = LogManager.getLogger(CloudServer.class);

    private final int port;

    private static  ServerSocketChannel serverSocketChannel;


    public CloudServer(int port){
        this.port=port;
    }

    public void start() throws IOException {
        Selector selector = Selector.open();

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(),port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);

        ConnectionGateway connectionGateway = new ConnectionGateway(selector,serverSocketChannel,500);
        connectionGateway.start();
        _logger.info("The Cloud Server started on port: " + port);
    }



}
