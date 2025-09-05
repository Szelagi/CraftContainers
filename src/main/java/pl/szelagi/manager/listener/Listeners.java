/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager.listener;

import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Listeners implements ImmutableListeners {
    private final Set<Class<? extends Listener>> listeners = new HashSet<>();

    public <T extends Listener> Listeners add(Class<T> listenerClass) {
        listeners.add(listenerClass);
        return this;
    }

    @Override
    public Set<Class<? extends Listener>> get() {
        return Collections.unmodifiableSet(listeners);
    }
}
