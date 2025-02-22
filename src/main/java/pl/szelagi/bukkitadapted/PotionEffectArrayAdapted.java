/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.bukkitadapted;

import org.bukkit.potion.PotionEffect;

import java.io.Serializable;

public class PotionEffectArrayAdapted implements Serializable {
    private final PotionEffectAdapted[] potionEffectsAdapted;
    private transient PotionEffect[] potionEffects;

    public PotionEffectArrayAdapted(PotionEffect[] potionEffects) {
        this.potionEffects = potionEffects;
        var array = new PotionEffectAdapted[potionEffects.length];
        for (int i = 0; i < potionEffects.length; i++)
            array[i] = new PotionEffectAdapted(potionEffects[i]);
        this.potionEffectsAdapted = array;
    }

    public PotionEffectAdapted[] getPotionEffectsAdapted() {
        return potionEffectsAdapted;
    }

    public PotionEffect[] getPotionEffects() {
        if (potionEffects == null) {
            potionEffects = new PotionEffect[potionEffectsAdapted.length];
            for (int i = 0; i < potionEffects.length; i++)
                potionEffects[i] = potionEffectsAdapted[i].getPotionEffect();
        }
        return potionEffects;
    }
}
