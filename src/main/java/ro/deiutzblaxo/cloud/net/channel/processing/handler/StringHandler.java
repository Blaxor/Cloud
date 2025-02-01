package ro.deiutzblaxo.cloud.net.channel.processing.handler;

import ro.deiutzblaxo.cloud.net.channel.data.objects.PacketData;

import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public abstract class StringHandler implements Handler {

    @Override
    public PacketData process(byte[] data, SocketChannel clientChannel) {
        return process(new String(data, StandardCharsets.UTF_16), clientChannel);
    }

    public abstract PacketData process(String data, SocketChannel clientChannel);
}
