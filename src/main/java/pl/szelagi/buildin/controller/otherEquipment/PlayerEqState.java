/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.otherEquipment;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pl.szelagi.bukkitadapted.ItemStackArrayAdapted;
import pl.szelagi.bukkitadapted.PotionEffectArrayAdapted;
import pl.szelagi.state.PlayerState;

import java.io.Serializable;
import java.util.Arrays;

public class PlayerEqState extends PlayerState implements Serializable {
    private ItemStackArrayAdapted contents;
    private PotionEffectArrayAdapted potionEffects;
    private double health;
    private double healthScale;
    private int foodLevel;
    private float saturation;
    private int totalExperience;
    private int level;
    private float exp;

    public PlayerEqState(Player player) {
        super(player);
    }

    public void save() {
        this.contents = new ItemStackArrayAdapted(getPlayer().getInventory().getContents());
        this.potionEffects = new PotionEffectArrayAdapted(getPlayer().getActivePotionEffects().toArray(PotionEffect[]::new));
        this.health = getPlayer().getHealth();
        this.healthScale = getPlayer().getHealthScale();
        this.foodLevel = getPlayer().getFoodLevel();
        this.saturation = getPlayer().getSaturation();
        this.totalExperience = getPlayer().getTotalExperience();
        this.level = getPlayer().getLevel();
        this.exp = getPlayer().getExp();
    }

    public void load(Player player) {
        player.getInventory().setContents(getContents());
        player.clearActivePotionEffects();
        player.addPotionEffects(Arrays.asList(getPotionEffects()));
        player.setHealthScale(healthScale);
        player.setHealth(health);
        player.setFoodLevel(foodLevel);
        player.setSaturation(saturation);
        player.setTotalExperience(totalExperience);
        player.setLevel(level);
        player.setExp(exp);
    }

    public ItemStack[] getContents() {
        return contents.getItemStacks();
    }

    public PotionEffect[] getPotionEffects() {
        return potionEffects.getPotionEffects();
    }

    public double getHealth() {
        return health;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public float getSaturation() {
        return saturation;
    }

    public int getTotalExperience() {
        return totalExperience;
    }

    public double getHealthScale() {
        return healthScale;
    }

    public int getLevel() {
        return level;
    }

    public float getExp() {
        return exp;
    }
}
