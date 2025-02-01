package ro.deiutzblaxo.cloud.net.channel.processing.handler;

import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;

import java.nio.channels.SocketChannel;


/**
 * REQUIRED TO HAVE A EMPTY CONSTRUCTOR.
 */


public interface Handler {

    PacketData process(byte[] data, SocketChannel clientChannel);
}
