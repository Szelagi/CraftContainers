/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.creator;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.GameMap;
import pl.szelagi.component.container.Container;

@Deprecated
public class Creator extends Container {
    private final String mainDirectory;
    private boolean isRecording = false;

    public Creator(JavaPlugin plugin, String mainDirectory) {
        super(plugin);
        this.mainDirectory = mainDirectory;
    }

    @NotNull
    @Override
    protected GameMap defaultBoard() {
        return new CreatorGameMap(this, mainDirectory);
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }
}
