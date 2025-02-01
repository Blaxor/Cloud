package example.net.channel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;
import ro.deiutzblaxo.cloud.net.channel.network.client.CloudClient;

import java.io.IOException;
import java.net.InetAddress;


public class Client {

    public static final Logger logger = LogManager.getLogger(Client.class);

    public static void main(String[] t) throws IOException {

        CloudClient cloudClient = new CloudClient(InetAddress.getLocalHost().getHostName(), 1234);

        cloudClient.start();

        logger.info("sending normal request");
        cloudClient.getGateway().makeRequest(new PacketData(1, "This is a normal request.".getBytes()));

        logger.info("sending normal callback");
        cloudClient.getGateway().makeRequestWithCallBack(new PacketData(1, "This is a request with callback.".getBytes()),
                value -> logger.info("Running the callback."));

    }


}
