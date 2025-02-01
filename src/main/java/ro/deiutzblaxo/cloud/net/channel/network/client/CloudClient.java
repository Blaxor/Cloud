package ro.deiutzblaxo.cloud.net.channel.network.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class CloudClient {

    private final String hostname;
    private final int port;

    ConnectionGateway gatewayThread;




    public CloudClient(String hostname, int port){
        this.hostname=hostname;
        this.port=port;

    }

    public void start() throws IOException {
        SocketChannel socket = SocketChannel.open();
        socket.configureBlocking(true);
        socket.connect(new InetSocketAddress(hostname,port));
        gatewayThread = new ConnectionGateway(socket);

        gatewayThread.start();


    }

    public ConnectionGateway getGateway() {
        return gatewayThread;
    }
}
