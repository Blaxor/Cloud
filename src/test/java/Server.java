import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;
import ro.deiutzblaxo.cloud.net.channel.network.server.CloudServer;
import ro.deiutzblaxo.cloud.net.channel.processing.PacketDataHandlers;
import ro.deiutzblaxo.cloud.net.channel.processing.handler.Handler;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Server {

    public static void main(String[] t) throws IOException {

        CloudServer cloudServer = new CloudServer(1234);
        PacketDataHandlers.addHandler(1,Handler1.class);


        cloudServer.start();
    }

    public static class Handler1 implements Handler{

        Logger logger = LogManager.getLogger(Handler.class);

        public Handler1(){

        }
        @Override
        public PacketData process(byte[] data, SocketChannel clientChannel) {

            logger.info("Got the data" + new String(data));
            return new PacketData(0,"Got response in handler1 for data ".getBytes(StandardCharsets.UTF_8));
        }
    }

}
