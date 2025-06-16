/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;

import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.session.Session;
import pl.szelagi.util.ServerRuntimeException;

public class SingletonComponentException extends ServerRuntimeException {
    public SingletonComponentException(BaseComponent baseComponent) {
        super("An instance of " + baseComponent.name() +
                " exists more than once in the same session (" +
                baseComponent.session().name() + ").");
    }
}
