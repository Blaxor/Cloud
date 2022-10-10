package ro.deiutzblaxo.cloud.net;

public interface Sender {

    public void sendMessage(byte[] bytes);

    public <T> void sendMessage(T message);

}
