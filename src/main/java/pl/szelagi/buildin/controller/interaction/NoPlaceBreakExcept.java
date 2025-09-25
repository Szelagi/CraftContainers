/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.interaction;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.szelagi.component.base.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NoPlaceBreakExcept extends NoPlaceBreak {
    private final Set<Material> allowedMaterials;

    public NoPlaceBreakExcept(Component parent, Collection<Material> allowedMaterials) {
        super(parent);
        this.allowedMaterials = new HashSet<>(allowedMaterials);
    }

    private boolean checkAllowed(Material material) {
        return allowedMaterials.contains(material);
    }

    @Override
    protected boolean canBreak(BlockBreakEvent event) {
        return checkAllowed(event.getBlock().getType());
    }

    @Override
    protected boolean canPlace(BlockPlaceEvent event) {
        return checkAllowed(event.getBlock().getType());
    }
}
