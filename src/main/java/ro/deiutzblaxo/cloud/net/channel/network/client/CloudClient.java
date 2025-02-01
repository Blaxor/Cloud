package ro.deiutzblaxo.cloud.net.channel.network.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class CloudClient {
    private final String hostname;
    private final int port;
    ConnectionGateway gatewayThread;

    /**
     * Constructs a {@link CloudClient} with the specified hostname and port.
     *
     * @param hostname The hostname or IP address of the server to connect to.
     * @param port     The port number on which the server is listening.
     */
    public CloudClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Represents a client that connects to a remote cloud server via a socket.
     * This class initializes a connection to the specified hostname and port
     * and manages communication through a {@link ConnectionGateway}.
     */
    public void start() throws IOException {
        SocketChannel socket = SocketChannel.open();
        socket.configureBlocking(true);
        socket.connect(new InetSocketAddress(hostname, port));
        gatewayThread = new ConnectionGateway(socket);
        gatewayThread.start();
    }

    /**
     * Retrieves the {@link ConnectionGateway} instance responsible for managing
     * communication between the client and the server.
     *
     * @return The {@link ConnectionGateway} handling the connection.
     */
    public ConnectionGateway getGateway() {
        return gatewayThread;
    }
}
