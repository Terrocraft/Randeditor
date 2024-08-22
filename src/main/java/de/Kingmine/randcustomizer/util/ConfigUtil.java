package de.Kingmine.randcustomizer.util;

import de.Kingmine.randcustomizer.KingMineRandCustomizer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtil {
    static Map<String, SConfig> cachemap = new HashMap<>();

    public static SConfig getConfig(String name) {
        if(cachemap.get(name) != null) {
            return cachemap.get(name);
        }

        SConfig sConfig = new SConfig(new File(KingMineRandCustomizer.getInstance().getDataFolder(), name+".yml"), name);
        cachemap.put(name, sConfig);
        return sConfig;
    }

    public static void saveALL() {
        cachemap.forEach((a, b) -> {
            b.save();
        });
    }
}
