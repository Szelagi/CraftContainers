/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test.treeProcessingTest;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.component.ComponentDestructor;
import pl.szelagi.event.internal.player.PlayerConstructor;
import pl.szelagi.event.internal.player.PlayerDestructor;
import pl.szelagi.event.internal.playerRequest.PlayerJoinRequest;
import pl.szelagi.component.Controller;

abstract class CNode extends Controller {
    private final String id;

    public CNode(@NotNull Component parent) {
        super(parent);
        this.id = getClass().getSimpleName();
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        ((S) container()).componentConstructors.add(id);
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);
        ((S) container()).playerConstructors.add(id);
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        ((S) container()).playerDestructors.add(id);
    }

    @Override
    public void onComponentDestroy(ComponentDestructor event) {
        super.onComponentDestroy(event);
        ((S) container()).componentDestructors.add(id);
    }

    @Override
    public void onPlayerJoinRequest(PlayerJoinRequest event) {
        super.onPlayerJoinRequest(event);
        ((S) container()).playerJoinRequest.add(id);
    }
}
