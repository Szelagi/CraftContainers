/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.editor;

import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.buildin.controller.otherEquipment.OtherEquipment;
import pl.szelagi.buildin.controller.otherGameMode.OtherGameMode;
import pl.szelagi.component.container.Container;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.component.GameMap;

import java.io.File;

public class BlueprintContainer extends Container {
    private final @NotNull File schematicFile;
    private final @NotNull File markersFile;

    public BlueprintContainer(JavaPlugin plugin, @NotNull File schematicFile, @NotNull File markersFile) {
        super(plugin);
        this.schematicFile = schematicFile;
        this.markersFile = markersFile;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        new OtherEquipment(this, true).start();
        new OtherGameMode(this, GameMode.CREATIVE).start();
    }

    @Override
    protected @NotNull GameMap defaultBoard() {
        return new BlueprintGameMap(this);
    }

    public @NotNull File getSchematicFile() {
        return schematicFile;
    }

    public @NotNull File getMarkersFile() {
        return markersFile;
    }
}
