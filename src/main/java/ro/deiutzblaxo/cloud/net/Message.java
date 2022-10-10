package ro.deiutzblaxo.cloud.net;

public interface Message<T> {

    public <T> T getData();

    public void setData(T data);

    public byte[] getBytes();

}
