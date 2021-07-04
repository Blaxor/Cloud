package ro.deiutzblaxo.cloud.nus;

import java.util.UUID;

public interface NameUUIDStorage {

    String getNameByUUID(UUID uuid);

    String getUUIDByName(String name);

    String getRealName(String fakename);

    void add(String name, UUID uuid);
}
