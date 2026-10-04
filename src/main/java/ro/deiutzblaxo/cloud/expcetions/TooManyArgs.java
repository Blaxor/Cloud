package ro.deiutzblaxo.cloud.expcetions;

public class TooManyArgs extends RuntimeException {

    public TooManyArgs(String msg) {
        super(msg);
    }

}
