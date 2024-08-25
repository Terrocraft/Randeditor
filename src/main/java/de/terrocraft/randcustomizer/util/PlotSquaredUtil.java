package de.terrocraft.randcustomizer.util;

import org.bukkit.Location;

public class PlotSquaredUtil {

    public static boolean isLocationInRange(Location loc, Location min, Location max) {
        return loc.getX() >= min.getX() && loc.getX() <= max.getX()
                && loc.getZ() >= min.getZ() && loc.getZ() <= max.getZ()
                && loc.getY() >= min.getY() && loc.getY() <= max.getY();
    }

}
