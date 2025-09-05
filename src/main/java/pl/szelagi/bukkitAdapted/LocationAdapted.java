/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.bukkitAdapted;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.*;
import java.util.UUID;

public class LocationAdapted implements Serializable {
    private transient Location location;

    public LocationAdapted(World world, double x, double y, double z) {
        this.location = new Location(world, x, y, z);
    }

    public LocationAdapted(Location location) {
        this.location = location;
    }

    public static String getSerializedLocation(Location loc) { //Converts location -> String
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getWorld().getUID();
        //feel free to use something to split them other than semicolons (Don't use periods or numbers)
    }

    public static Location getDeserializedLocation(String s) {//Converts String -> Location
        String[] parts = s.split(";"); //If you changed the semicolon you must change it here too
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        UUID u = UUID.fromString(parts[3]);
        World w = Bukkit.getServer().getWorld(u);
        return new Location(w, x, y, z); //can return null if the world no longer exists
    }

    public Location getLocation() {
        return location;
    }

    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(getSerializedLocation(this.getLocation()));
    }

    @Serial
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        var string = (String) ois.readObject();
        var l = getDeserializedLocation(string);
        location = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
    }
}
