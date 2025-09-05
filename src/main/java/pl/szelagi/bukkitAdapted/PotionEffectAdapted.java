/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.bukkitAdapted;

import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;

public class PotionEffectAdapted implements Serializable {
    private transient PotionEffect potionEffect;

    public PotionEffectAdapted(PotionEffect potionEffect) {
        this.potionEffect = potionEffect;
    }

    public PotionEffect getPotionEffect() {
        return potionEffect;
    }

    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        var bos = new BukkitObjectOutputStream(oos);
        bos.writeObject(potionEffect);
    }

    @Serial
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        var bis = new BukkitObjectInputStream(ois);
        this.potionEffect = (PotionEffect) bis.readObject();
    }
}
