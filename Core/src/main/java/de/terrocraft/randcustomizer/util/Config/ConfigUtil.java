package de.terrocraft.randcustomizer.util.Config;

import de.terrocraft.randcustomizer.RandCustomizer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtil {
    public static Map<String, SConfig> cachemap = new HashMap<>();

    public static SConfig getConfig(String name) {
        if (cachemap.get(name) != null) {
            return cachemap.get(name);
        }

        SConfig sConfig = new SConfig(new File(RandCustomizer.getInstance().getDataFolder(), name + ".yml"), name);
        cachemap.put(name, sConfig);
        return sConfig;
    }

    public void clearAllCache() {
        cachemap.clear();
    }


    public static void saveALL() {
        cachemap.forEach((a, b) -> {
            b.save();
        });
    }
}
