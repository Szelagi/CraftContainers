/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test.treeProcessingTest;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentDestructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerConstructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerDestructor;
import pl.szelagi.component.baseComponent.internalEvent.playerRequest.PlayerJoinRequest;
import pl.szelagi.component.controller.Controller;

abstract class CNode extends Controller {
    private final String id;

    public CNode(@NotNull BaseComponent parent) {
        super(parent);
        this.id = getClass().getSimpleName();
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        ((S) session()).componentConstructors.add(id);
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);
        ((S) session()).playerConstructors.add(id);
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        ((S) session()).playerDestructors.add(id);
    }

    @Override
    public void onComponentDestroy(ComponentDestructor event) {
        super.onComponentDestroy(event);
        ((S) session()).componentDestructors.add(id);
    }

    @Override
    public void onPlayerJoinRequest(PlayerJoinRequest event) {
        super.onPlayerJoinRequest(event);
        ((S) session()).playerJoinRequest.add(id);
    }
}
