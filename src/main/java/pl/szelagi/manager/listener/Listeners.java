/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager.listener;

import org.bukkit.event.Listener;

import java.util.HashSet;

public class Listeners implements ImmutableListeners {
    private final HashSet<Class<? extends Listener>> listeners = new HashSet<>();

    public <T extends Listener> Listeners add(Class<T> listenerClass) {
        listeners.add(listenerClass);
        return this;
    }

    public HashSet<Class<? extends Listener>> set() {
        return listeners;
    }
}
