package de.Kingmine.randcustomizer.util;

import com.plotsquared.core.location.Location;

public class ConverterUtil {
    public static Location toPlotsquaredLocation(org.bukkit.Location location) {
        return Location.at(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
