package ro.deiutzblaxo.cloud.net.udp;

import ro.deiutzblaxo.cloud.net.Listener;
import ro.deiutzblaxo.cloud.net.Message;

import java.util.ArrayList;
import java.util.List;

public class ObjectArrayListenerUDP implements Listener<Object> {

    protected List<Message<Object>> objects = new ArrayList<>();
    int port;
    String hostname;

    @Override
    public List<Message<Object>> getMessages() {
        return objects;
    }

    @Override
    public void setMessages(List<Message<Object>> messages) {
        this.objects = messages;
    }

    @Override
    public void changePort(int port) {

    }

    @Override
    public void changeIP(String hostname) {

    }

    @Override
    public void close() {

    }

    @Override
    public void open() {

    }

    @Override
    public void onReceived(Message<Object> message) {

    }
}
