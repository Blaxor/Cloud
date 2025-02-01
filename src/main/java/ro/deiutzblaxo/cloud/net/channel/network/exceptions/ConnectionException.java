package ro.deiutzblaxo.cloud.net.channel.network.exceptions;

public class ConnectionException extends RuntimeException{
    public ConnectionException(Throwable e) {
        super(e);
    }

    public ConnectionException() {

    }
}
