package ro.deiutzblaxo.cloud.expcetions;

public class RetryLimitExceededException extends RuntimeException{

    public RetryLimitExceededException(Throwable throwable) {
        super(throwable);
    }
}
