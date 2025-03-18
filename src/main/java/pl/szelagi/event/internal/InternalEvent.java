/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.internal;

// Odpowiada, za zdefiniowane systemowo wydarzenie
// Nie używa refleksji
// Przykłady to ComponentConstructor, PlayerDestructor

import java.util.LinkedList;
import java.util.List;

public abstract class InternalEvent {
    public List<Runnable> scheduled = new LinkedList<>();

    public void safeSchedule(Runnable runnable) {
        scheduled.add(runnable);
    }

    public List<Runnable> scheduled() {
        return scheduled;
    }
}
