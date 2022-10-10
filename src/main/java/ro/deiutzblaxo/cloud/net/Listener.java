package ro.deiutzblaxo.cloud.net;

import java.util.ArrayList;
import java.util.List;

public interface Listener<T> {

    public List<Message<T>> getMessages();

    public void setMessages(List<Message<T>> messages);

    public void changePort(int port);

    public void changeIP(String hostname);

    public void close();

    public void open();

    public void onReceived(Message<T> message);
}
