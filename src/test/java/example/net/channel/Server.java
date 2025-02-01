package example.net.channel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;
import ro.deiutzblaxo.cloud.net.channel.network.server.CloudServer;
import ro.deiutzblaxo.cloud.net.channel.processing.PacketDataHandlers;
import ro.deiutzblaxo.cloud.net.channel.processing.handler.Handler;
import ro.deiutzblaxo.cloud.net.channel.processing.handler.StringHandler;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class Server {

    public static void main(String[] t) throws IOException {

        CloudServer cloudServer = new CloudServer(1234);
        PacketDataHandlers.registerHandler(1, Handler1.class);


        cloudServer.start();
    }

    public static class Handler1 extends StringHandler {

        Logger logger = LogManager.getLogger(Handler.class);

        public Handler1() {

        }

        @Override
        public PacketData process(String data, SocketChannel clientChannel) {
            logger.error("We got the following data: " + data);

            return new PacketData(0, "This is a imaginary json response :D");
        }
    }

}
