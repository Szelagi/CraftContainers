/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.internal;

/**
 * Base class for internal events.
 * <p>
 * Used as a type token to uniquely identify event types in the system.
 * Each subclass represents a specific internal event.
 * For example: ComponentConstructor, PlayerDestructor
 */
public abstract class InternalEvent {
}
