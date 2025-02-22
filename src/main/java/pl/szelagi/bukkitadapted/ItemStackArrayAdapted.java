/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.bukkitadapted;

import org.bukkit.inventory.ItemStack;

import java.io.*;

public class ItemStackArrayAdapted implements Serializable {
    private ItemStackAdapted[] itemStacksAdapted;
    private transient ItemStack[] itemStacks;

    public ItemStackArrayAdapted(ItemStack[] itemStacks) {
        var array = new ItemStackAdapted[itemStacks.length];
        for (int i = 0; i < itemStacks.length; i++) {
            array[i] = new ItemStackAdapted(itemStacks[i]);
        }
        this.itemStacks = itemStacks;
        this.itemStacksAdapted = array;
    }

    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(itemStacksAdapted);
    }

    @Serial
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.itemStacksAdapted = (ItemStackAdapted[]) ois.readObject();
        this.itemStacks = new ItemStack[itemStacksAdapted.length];
        for (int i = 0; i < itemStacks.length; i++)
            itemStacks[i] = itemStacksAdapted[i].getItemStack();
    }

    public ItemStackAdapted[] getItemStacksAdapted() {
        return itemStacksAdapted;
    }

    public ItemStack[] getItemStacks() {
        return itemStacks;
    }
}
