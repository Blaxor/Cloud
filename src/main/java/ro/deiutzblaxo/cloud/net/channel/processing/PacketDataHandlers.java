package ro.deiutzblaxo.cloud.net.channel.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.net.channel.processing.handler.Handler;
import ro.deiutzblaxo.cloud.net.channel.processing.handler.NoneHandler;

import java.util.HashMap;


/**
 * Manages the registration and retrieval of packet data handlers.
 */
public class PacketDataHandlers {

    private static final Logger logger = LogManager.getLogger(PacketDataHandlers.class);
    private static final HashMap<Integer, Class<? extends Handler>> registeredHandlers = new HashMap<>() {{
        put(0, NoneHandler.class);
    }};

    /**
     * Registers a handler for a specific operation.
     *
     * @param operation The operation identifier.
     * @param handler   The handler class to be registered.
     */
    public static void registerHandler(Integer operation, Class<? extends Handler> handler) {
        if (registeredHandlers.containsKey(operation)) {
            logger.warn("The handler for operation " + operation + " exists already. Was not replaced.");
            return;
        }
        registeredHandlers.put(operation, handler);
    }

    /**
     * Retrieves the handler class for a specific operation.
     *
     * @param operation The operation identifier.
     * @return The handler class for the operation,if not found, will return null
     */
    public static Class<? extends Handler> getHandler(Integer operation) {
        return registeredHandlers.get(operation);
    }


    /**
     * Gets an instance of the handler for a specific operation.
     *
     * @param integer The operation identifier.
     * @param <T>     The type of the handler.
     * @return An instance of the handler or null if not found.
     */
    public static <T extends Handler> T getInstanceHandler(Integer integer) {
        Class<? extends Handler> handler = PacketDataHandlers.getHandler(integer);
        if (handler == null) {
            logger.warn("Handler not found for operation " + integer);
            return null;
        }
        try {
            return (T) PacketDataHandlers.getHandler(integer).getConstructors()[0].newInstance();
        } catch (Throwable e) {
            logger.error(e);
            return null;
        }
    }
}
