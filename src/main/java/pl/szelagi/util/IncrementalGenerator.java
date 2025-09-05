/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util;

import java.io.Serializable;

public class IncrementalGenerator implements Serializable {
    private long nextId;

    public IncrementalGenerator() {
        this(0);
    }

    public IncrementalGenerator(long nextId) {
        this.nextId = nextId;
    }

    public long next() {
        return nextId++;
    }

    public long peekNext() {
        return nextId;
    }

    public long peekCurrent() {
        return nextId - 1;
    }
}
