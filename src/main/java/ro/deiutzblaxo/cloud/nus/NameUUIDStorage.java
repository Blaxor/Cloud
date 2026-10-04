package ro.deiutzblaxo.cloud.nus;


import ro.deiutzblaxo.cloud.expcetions.TooManyArgs;

import java.sql.SQLException;
import java.util.UUID;

public interface NameUUIDStorage {

    String getNameByUUID(UUID uuid);

    String getUUIDByName(String name);

    String getRealName(String fakename);


    PriorityNUS getPriority();

    void add(String name, UUID uuid) throws TooManyArgs, SQLException;
}
