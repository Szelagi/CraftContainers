/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.fawe;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.jetbrains.annotations.NotNull;

public class ClipboardAndHolder implements AutoCloseable {
    private final @NotNull ClipboardHolder holder;
    private final @NotNull Clipboard clipboard;

    public ClipboardAndHolder(@NotNull ClipboardHolder holder) {
        this.holder = holder;

        var clipboards = holder.getClipboards();

        if (clipboards.size() != 1)
            throw new IllegalArgumentException("ClipboardHolder should have exactly one clipboard");

        clipboard = clipboards.getFirst();
    }

    public @NotNull ClipboardHolder holder() {
        return holder;
    }

    public @NotNull Clipboard clipboard() {
        return clipboard;
    }

    @Override
    public void close() {
        holder.close();
    }
}
