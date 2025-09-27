package ro.deiutzblaxo.cloud.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.expcetions.RetryLimitExceededException;

import java.util.function.Supplier;

public class RetryExecutor {

    private final int MAX_RETRIES;
    private final long DELAY_BETWEEN_RETRIES_MILLIS;
    private static final Logger log = LogManager.getLogger(RetryExecutor.class.getName());

    public RetryExecutor() {
        MAX_RETRIES = 3;
        DELAY_BETWEEN_RETRIES_MILLIS = 500;
    }

    public RetryExecutor(int maxRetries, long delayBetweenRetriesMillis) {
        this.MAX_RETRIES = maxRetries;
        this.DELAY_BETWEEN_RETRIES_MILLIS = delayBetweenRetriesMillis;
    }

    public <T> T executeWithRetry(Supplier<T> operation) throws RetryLimitExceededException {
        int attempt = 0;
        while (true) {
            try {
                return operation.get();
            } catch (Exception e) {
                attempt++;
                if (attempt > MAX_RETRIES) {
                    log.error("Operation failed after {} attempts, giving up", MAX_RETRIES);
                    throw new RetryLimitExceededException(e);
                }

                log.info("Attempt {} to execute operation failed... waiting {} ms before retry", attempt, DELAY_BETWEEN_RETRIES_MILLIS);
                log.debug("Exception details: ", e);

                // Wait before retrying
                try {
                    Thread.sleep(DELAY_BETWEEN_RETRIES_MILLIS);
                } catch (InterruptedException ie) {
                    log.warn("Sleep interrupted during retry, but continuing with retry attempts");
                    Thread.currentThread().interrupt(); // Restore interrupt status but continue
                }
            }
        }
    }


}
