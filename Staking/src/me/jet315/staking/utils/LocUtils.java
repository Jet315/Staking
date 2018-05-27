package me.jet315.staking.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocUtils {

    public static Location locationFromString(String section){

        String[] split = section.split(",");

        World world = Bukkit.getWorld(split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = Float.parseFloat(split[4]);
        float pitch = Float.parseFloat(split[5]);

        return new Location(world, x, y, z, yaw, pitch);

    }
    public static String locationToString(Location location){

        String seperator = ",";
        return location.getWorld().getName() + seperator + location.getX() +
                seperator + location.getY() + seperator + location.getZ() +
                seperator + location.getYaw() + seperator + location.getPitch();
    }

}
