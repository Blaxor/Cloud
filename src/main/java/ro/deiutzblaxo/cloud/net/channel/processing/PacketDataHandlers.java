package ro.deiutzblaxo.cloud.net.channel.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.net.channel.processing.handler.Handler;
import ro.deiutzblaxo.cloud.net.channel.processing.handler.NoneHandler;

import java.util.HashMap;

public class PacketDataHandlers {

    private static final Logger logger = LogManager.getLogger(PacketDataHandlers.class);
    private static final HashMap<Integer, Class<? extends Handler>> registeredHandlers = new HashMap<>(){{
        put(0, NoneHandler.class);
    }};

    public static void addHandler(Integer operation ,Class<? extends Handler> handler){
        if(registeredHandlers.containsKey(operation)){
            logger.warn("The handler for operation " + operation + " exists already. Was not replaced.");
            return;
        }
        registeredHandlers.put(operation,handler);
    }

    public static Class<? extends Handler> getHandler(Integer operation){
        return registeredHandlers.get(operation);
    }

    public static <T extends Handler> T getInstanceHandler(Integer integer){
        Class<? extends Handler> handler = PacketDataHandlers.getHandler(integer);
        if(handler == null){
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
