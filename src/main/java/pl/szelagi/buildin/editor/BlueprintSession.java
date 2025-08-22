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
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.session.Session;

import java.io.File;

public class BlueprintSession extends Session {
    private final @NotNull File schematicFile;
    private final @NotNull File markersFile;

    public BlueprintSession(JavaPlugin plugin, @NotNull File schematicFile, @NotNull File markersFile) {
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
    protected @NotNull Board defaultBoard() {
        return new BlueprintBoard(this);
    }

    public @NotNull File getSchematicFile() {
        return schematicFile;
    }

    public @NotNull File getMarkersFile() {
        return markersFile;
    }
}
